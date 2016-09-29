package com.ibm.app.mgr;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.CouchDbException;
import com.google.gson.JsonObject;

public class CloudantClientMgr extends ClientMgr {
	private static CloudantClientMgr instance = null;

	private CloudantClient cloudant = null;
	private Database db = null;
	private String url = null;
	private String user = null;
	private String password = null;
	private final String databaseName = "orders";

	private CloudantClient createClient() {
		JsonObject credentials = getCredentialsForService("cloudantNoSQLDB");
		if (credentials.has("username"))
			user = credentials.get("username").getAsString();
		else
			throw new RuntimeException("username is missing");
		if (credentials.has("password"))
			password = credentials.get("password").getAsString();
		else
			throw new RuntimeException("password is missing");
		if (credentials.has("url"))
			url = credentials.get("url").getAsString();
		else
			throw new RuntimeException("url is missing");
		
		try {
			return ClientBuilder.account(user).username(user).password(password).build();
		} catch (CouchDbException e) {
			throw new RuntimeException("Unable to connect to repository", e);
		}
	}

	public Database getDB() {
		if (cloudant == null) {
			return null;
		}

		if (db == null) {
			try {
				db = cloudant.database(databaseName, true);
			} catch (Exception e) {
				throw new RuntimeException("DB Not found", e);
			}
		}
		return db;
	}

	public String getDatabaseURL() {
		return url + "/" + databaseName + "/";
	}

	private CloudantClientMgr() {
		cloudant = createClient();
	}
	
	public static CloudantClientMgr getInstance() {
		if (instance == null) {
			instance = new CloudantClientMgr();
		}
		return instance;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getDatabaseName() {
		return databaseName;
	}
}
