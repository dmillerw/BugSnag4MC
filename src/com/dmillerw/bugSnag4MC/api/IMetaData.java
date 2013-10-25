package com.dmillerw.bugSnag4MC.api;

import java.util.HashMap;

public interface IMetaData {

	public void add(String key, Object obj);
	
	public void add(String key, String subKey, Object obj);
	
}
