package com.dmillerw.bugSnag4MC.core;

import java.util.Map.Entry;

import com.bugsnag.Client;
import com.bugsnag.MetaData;
import com.dmillerw.bugSnag4MC.api.ICrashHandler;

public class CrashReportHandler {

	public static void handleCrashReport(String description, Throwable thrown, String completeReport) {
		if (description.equalsIgnoreCase("ThisIsFake")) {
			return; // Ignoring fake crashes by FML/Forge
		}
		
		for (Entry<String, ICrashHandler> handler : InternalAPIHandler.registeredHandlers.entrySet()) {
			ICrashHandler crashHandler = handler.getValue();
			Client client = InternalAPIHandler.registeredClients.get(crashHandler.getModID());
			MetaDataHandler metaData = new MetaDataHandler();
			crashHandler.fillUserData(metaData);
			metaData.add("Minecraft", "Description", description);
			metaData.add("Minecraft", "Crash Log", completeReport);
			if (crashHandler.onCrash(description, thrown)) {
				client.notify(thrown, metaData.meta);
			}
		}
	}
	
}
