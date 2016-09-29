package com.ibm.app.mgr;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ClientMgr {
	protected String VCAP_SERVICES = null;
	protected JsonObject getCredentialsForService(String service) {
		// VCAP_SERVICES is a system environment variable
		// Parse it to obtain the NoSQL DB connection info
		if (VCAP_SERVICES == null) {
			VCAP_SERVICES = System.getenv("VCAP_SERVICES");
				if (VCAP_SERVICES == null)
					throw new RuntimeException("VCAP_SERVICES not found");
		}
		
		// parse the VCAP JSON structure
		JsonParser parser = new JsonParser();
		JsonObject jObject = null;
		JsonArray jArray = null;
		try {
			jObject = parser.parse(VCAP_SERVICES).getAsJsonObject();
		} catch (JsonSyntaxException e) {
			throw new RuntimeException("VCAP_SERVICES could not be parsed");
		}
		
		if (!jObject.has(service))
			throw new RuntimeException(service + " service not found");
		if (!jObject.get(service).isJsonArray())
			throw new RuntimeException(service + " is not an array");
		
		jArray = jObject.getAsJsonArray(service);
		if (jArray.get(0).isJsonNull())
			throw new RuntimeException(service + " is empty");
		if (!jArray.get(0).isJsonObject())
			throw new RuntimeException(service + " is not an object");
		
		jObject = jArray.get(0).getAsJsonObject();
		if (!jObject.has("credentials"))
			throw new RuntimeException("credentials object was not found");
		if (!jObject.get("credentials").isJsonObject())
			throw new RuntimeException("credentials is not an object");
		
		return jObject.get("credentials").getAsJsonObject();
	}
	
	protected void info(String message) {
		System.out.println(message);
	}
	
	protected void err(String message) {
		System.err.println(message);
	}
}
