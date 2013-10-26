package com.dmillerw.bugSnag4MC.core;

import com.dmillerw.bugSnag4MC.api.ICrashHandler;
import com.dmillerw.bugSnag4MC.api.IMetaData;

public class BasicCrashHandler implements ICrashHandler {

	private final String modID;
	private final String basePackage;
	private final String apiKey;
	
	public BasicCrashHandler(String modID, String basePackage, String apiKey) {
		this.modID = modID;
		this.basePackage = basePackage;
		this.apiKey = apiKey;
	}
	
	@Override
	public String getModID() {
		return this.modID;
	}

	@Override
	public String[] getBasePackages() {
		return (basePackage != null && basePackage.length() > 0) ? new String[] {basePackage} : null;
	}

	@Override
	public String[] getAdditionalPackages() {
		return null;
	}
	
	@Override
	public boolean submitRelatedCrashesOnly() {
		return (basePackage != null && basePackage.length() > 0);
	}

	@Override
	public String getAPIKey() {
		return this.apiKey;
	}

	@Override
	public void fillUserData(IMetaData meta) {
		
	}

	@Override
	public boolean onCrash(String description, Throwable throwable, IMetaData meta) {
		return true;
	}

	
	
}
