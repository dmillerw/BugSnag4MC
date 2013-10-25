package com.dmillerw.bugSnag4MC.core;

import java.util.HashMap;

import com.dmillerw.bugSnag4MC.api.ICrashHandler;
import com.dmillerw.bugSnag4MC.api.IMetaData;

public class BasicCrashHandler implements ICrashHandler {

	private final String modID;
	private final String apiKey;
	
	public BasicCrashHandler(String modID, String apiKey) {
		this.modID = modID;
		this.apiKey = apiKey;
	}
	
	@Override
	public String getModID() {
		return this.modID;
	}

	@Override
	public String getBasePackage() {
		return null;
	}

	@Override
	public boolean submitRelatedCrashesOnly() {
		return false;
	}

	@Override
	public String getAPIKey() {
		return this.apiKey;
	}

	@Override
	public void fillUserData(IMetaData meta) {
		
	}

	@Override
	public boolean onCrash(String description, Throwable throwable) {
		return true;
	}

	
	
}
