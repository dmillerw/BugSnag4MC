package com.dmillerw.bugSnag4MC.api;

import java.util.HashMap;

public interface IBugSnagHandler {

	public String getModID();
	
	public String getBasePackage();
	
	public boolean submitRelatedCrashesOnly();

	public String getAPIKey();
	
	public void fillUserData(HashMap<String, String> map);
	
}
