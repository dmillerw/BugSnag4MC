package com.dmillerw.bugSnag4MC.core;

import com.bugsnag.MetaData;
import com.dmillerw.bugSnag4MC.api.IMetaData;

public class MetaDataHandler implements IMetaData {

	public MetaData meta;
	
	public MetaDataHandler() {
		this.meta = new MetaData();
	}
	
	@Override
	public void add(String key, Object obj) {
		this.meta.addToTab(key, obj);
	}

	@Override
	public void add(String key, String subKey, Object obj) {
		this.meta.addToTab(key, subKey, obj);
	}

}
