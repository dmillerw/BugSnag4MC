package com.dmillerw.bugSnag4MC.core;

import java.util.Map.Entry;

import com.dmillerw.bugSnag4MC.api.APIHandler;
import com.dmillerw.bugSnag4MC.api.ICrashHandler;

public class CrashReportHandler {

	public static void handleCrashReport(String description, Throwable thrown, String completeReport) {
		for (Entry<String, ICrashHandler> handler : APIHandler.registeredHandlers.entrySet()) {
			handler.getValue().onCrash(description, thrown);
		}
	}
	
}
