package com.ibm.app.mgr;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.JsonObject;
import com.ibm.tws.api.ApiException;
import com.ibm.tws.model.CloudantAction;
import com.ibm.tws.model.CloudantBody;
import com.ibm.tws.model.CloudantConnection;
import com.ibm.tws.model.CloudantStep;
import com.ibm.tws.model.CommandStep;
import com.ibm.tws.model.Process;
import com.ibm.tws.model.Step;
import com.ibm.tws.api.ApiClient;
import com.ibm.tws.api.ProcessApi;
import com.ibm.tws.api.ProcessLibraryApi;
import com.ibm.tws.model.ProcessLibrary;

public class AppLabClientMgr extends ClientMgr {
	private static final String PROCESS_NAME = "OrderProcess";

	private static final String LIBRARY_NAME = "BeerShop";

	private static AppLabClientMgr instance = null;
	
	private String tenantId, user, password, engineName, engineOwner, basePath;
	private Integer processID = null;
	
	public static AppLabClientMgr getInstance() {
		if (instance == null) {
			instance = new AppLabClientMgr();
		}
		return instance;
	}
	
	private AppLabClientMgr() {
		JsonObject credentials = getCredentialsForService("WorkloadScheduler");
		if (credentials.has("userId"))
			try {
				user = URLDecoder.decode(credentials.get("userId").getAsString(), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				throw new RuntimeException("username is malformed");
			}
		else
			throw new RuntimeException("username is missing");
		if (credentials.has("password"))
			try {
				password = URLDecoder.decode(credentials.get("password").getAsString(), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				throw new RuntimeException("password is malformed");
			}
		else
			throw new RuntimeException("password is missing");
		
		String urlString = null;
		if (credentials.has("url"))
			urlString = credentials.get("url").getAsString();
		else
			throw new RuntimeException("url is missing");
		try {
			int startQuery = urlString.indexOf("?");
			if (startQuery == -1) throw new RuntimeException("query params not found");
			String query = urlString.substring(startQuery + 1);
			if (urlString.contains("@")) //Auth in url
				basePath = "https://" + urlString.substring(urlString.indexOf("@") + 1, startQuery);
			else
				basePath = urlString.substring(0, startQuery);
		    String[] pairs = query.split("&");
		    for (String pair : pairs) {
		        int idx = pair.indexOf("=");
		    	String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
		    	switch (key) {
				case "tenantId":
					tenantId = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
					break;
				case "engineName":
					engineName = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
					break;
				case "engineOwner":
					engineOwner = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
					break;
				default:
					break;
				}
		    }
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported encoding was found");
		}
		
		try {
			createProcess();
		} catch (Exception e) {
			throw new RuntimeException("Could not create process", e);
		}
	}

	private void createProcess() throws ApiException {
		if (processID != null) return;
		disableCertificateValidation();

		ApiClient apiClient = new ApiClient();
		apiClient.setUsername(user);
		apiClient.setPassword(password);
		apiClient.setBasePath(basePath);
        ProcessApi processApi = new ProcessApi(apiClient);
        ProcessLibraryApi libApi = new ProcessLibraryApi(apiClient);
        
        // Determine Library
        List<ProcessLibrary> libraries = libApi.listProcessLibrary(tenantId, engineName, engineOwner);
        info("Listed process libraries");
        boolean present = false;
        Integer libID = null;
        for (ProcessLibrary l: libraries) {
        	if (l.getName().equals(LIBRARY_NAME)) {
        		present = true;
        		libID = l.getId();
                info("Found existing process library");
        	}
        }
        if (!present) {
        	List<ProcessLibrary> result = libApi.createProcessLibrary((new ProcessLibrary()).name(LIBRARY_NAME).parentid(-1), tenantId, engineName, engineOwner);
        	if (result.isEmpty())
    			throw new RuntimeException("Could not create library");
        	libID = result.get(0).getId();
            info("Created process library");
        }
        
        //Determine Process
        List<Process> existingProcesses = libApi.listProcessesInLibrary(libID.toString(), tenantId, engineName, engineOwner);
        for (Process p: existingProcesses) {
        	if (p.getName().equals(PROCESS_NAME)){
        		processID = p.getId();
                info("Found existing process");
        		return;
        	}
        }

        Process newProcess = new Process();
        newProcess.setName(PROCESS_NAME);
        newProcess.setProcesslibraryid(libID);
        newProcess.setProcessstatus(false);
        List<Step> steps = new ArrayList<Step>();
        
        //STEP1: Cloudant read all docs
        Step step = new Step();
        CloudantStep clStep = getCloudantStepWithConnection();
        CloudantAction clAct = new CloudantAction();
        CloudantBody clBody = new CloudantBody();
        clBody.setType("mock");
        clBody.setInputDocument("mock");
        clBody.setInputFileName("mock");
        clAct.setType("documentOperation");
        clAct.setDocumentOperation("READ");
        clAct.setDocumentId("_all_docs");
        clAct.setDocumentRevision("mock");
        clStep.setCloudantAction(clAct);
        clStep.setCloudantBody(clBody);
        step.setCloudantStep(clStep);
        steps.add(step);
        info("Created Step 1");
        
        //STEP2: Cloudant read order
        step = new Step();
        clStep = getCloudantStepWithConnection();
        clAct = new CloudantAction();
        clAct.setType("documentOperation");
        clAct.setDocumentOperation("READ");
        clAct.setDocumentId("${job:STEP1.jsonResult.rows[0].id}");
        clAct.setDocumentRevision("test");
        clStep.setCloudantAction(clAct);
        clStep.setCloudantBody(clBody);
        step.setCloudantStep(clStep);
        steps.add(step);
        info("Created Step 2");
        
        String from = System.getenv("FROM"), to = System.getenv("TO");
        if (from != null && to != null) {
	        //STEP3: Run processing script step
	        step = new Step();
	        CommandStep cmdStep = new CommandStep();
	        cmdStep.setAgent(getHybridAgentName());
	        String script = "#!/bin/bash" + System.lineSeparator() +
	        		"ORDER=\"Congratulations! Your purchase of ${job:STEP2.jsonResult.order_description} was processed\"" + System.lineSeparator() +
	        		"echo $ORDER" + System.lineSeparator() + 
	        		"echo $ORDER | mail -s \"Beershop Purchase\" -r " + from + " " + to;
	        cmdStep.setCommand(script);
	        step.setCommandStep(cmdStep);
	        steps.add(step);
	        info("Created Step 3");
        } else {
        	err("Missing FROM and TO env variables: skipping step 3");
        }
        
        //STEP4: Cloudant delete order
        step = new Step();
        clStep = getCloudantStepWithConnection();
        clAct = new CloudantAction();
        clAct.setType("documentOperation");
        clAct.setDocumentOperation("DELETE");
        clAct.setDocumentId("${job:STEP2.jsonResult._id}");
        clAct.setDocumentRevision("${job:STEP2.jsonResult._rev}");
        clStep.setCloudantAction(clAct);;
        clStep.setCloudantBody(clBody);
        step.setCloudantStep(clStep);
        steps.add(step);
        info("Created Step 4");

        newProcess.setSteps(steps);
        Process process = processApi.createProcess(newProcess, tenantId, engineName, engineOwner);
        info("Created process on Application Lab");
        Integer tempProcessID = process.getId();
        Process p = processApi.getProcess(tempProcessID.toString(), tenantId, engineName, engineOwner);
        info("Retrived process on Application Lab");
        p.setProcessstatus(true);
        for (Step s: p.getSteps()) {
        	if (s != null && s.getCloudantStep() != null)
        		s.getCloudantStep().setCloudantBody(clBody);
        }
        processApi.updateProcess(tempProcessID.toString(), p, tenantId, engineName, engineOwner);
        info("Enabled process on Application Lab");
        processID = tempProcessID;
	}

	private CloudantStep getCloudantStepWithConnection() {
		CloudantStep toReturn = new CloudantStep();
		toReturn.setAgent(getCloudAgentName());
        CloudantConnection clConn = new CloudantConnection();
        clConn.setUser(CloudantClientMgr.getInstance().getUser());
        clConn.setPassword(CloudantClientMgr.getInstance().getPassword());
        clConn.setAccount(CloudantClientMgr.getInstance().getUser());
        clConn.setDatabasename(CloudantClientMgr.getInstance().getDatabaseName());
        toReturn.setCloudantConnection(clConn);
		return toReturn;
	}

	private String getCloudAgentName() {
		String result = null;
		if (tenantId != null)
			result = tenantId + "_CLOUD";
		return result;
	}

	private String getHybridAgentName() {
		String agentName = System.getenv("HYBRID_AGENT");
		if (agentName == null)
			agentName = "HYBRID";
		return tenantId == null ? "" : tenantId + agentName;
	}
	
	public void runProcess() throws ApiException {
		if (processID == null)
			throw new RuntimeException("Could not run null process");
		ApiClient apiClient = new ApiClient();
		apiClient.setUsername(user);
		apiClient.setPassword(password);
		apiClient.setBasePath(basePath);
        ProcessApi processApi = new ProcessApi(apiClient);
        processApi.runNowProcess(processID.toString(), tenantId, "", engineName, engineOwner);
        info("Started process on Application Lab");
	}

	/**
	 * Disables https certificate validation
	 */
	public static void disableCertificateValidation() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {

					@Override
					public void checkClientTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {
						// TODO Auto-generated method stub
						
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						// TODO Auto-generated method stub
						return new X509Certificate[0];
					}}
		};
		
		// Ignore differences between given hostname and certificate hostname
		HostnameVerifier hv = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				// TODO Auto-generated method stub
				return true;
			}
		};
		
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {
		}
	}
}
