package com.dmillerw.bugSnag4MC.core;

import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import com.bugsnag.Client;
import com.bugsnag.MetaData;
import com.dmillerw.bugSnag4MC.api.ICrashHandler;

public class CrashReportHandler {

	public static void handleCrashReport(String description, Throwable thrown, String completeReport) {
		if (description.equalsIgnoreCase("ThisIsFake")) {
			return; // Ignoring fake crashes by FML/Forge
		}
		
		StackTraceElement last = thrown.getStackTrace()[0];
		
		for (Entry<String, ICrashHandler> handler : InternalAPIHandler.registeredHandlers.entrySet()) {
			ICrashHandler crashHandler = handler.getValue();
			
			Client client = InternalAPIHandler.registeredClients.get(crashHandler.getModID());
			client.setContext(description);
			
			String[] basePackages = crashHandler.getBasePackages();
			String[] additionalPackages = crashHandler.getAdditionalPackages();
			
			boolean sendFlag = false;
			
			if (basePackages != null) {
				if (crashHandler.submitRelatedCrashesOnly()) {
					for (String pack : basePackages) {
						if (last.getClassName().contains(pack)) {
							sendFlag = true;
							break;
						}
					}
					
					for (String pack : additionalPackages) {
						if (last.getClassName().contains(pack)) {
							sendFlag = true;
							break;
						}
					}
				} else {
					sendFlag = true;
				}
			}

			client.setProjectPackages(ArrayUtils.addAll(basePackages, additionalPackages));
			
			MetaDataHandler metaData = new MetaDataHandler();
			crashHandler.fillUserData(metaData);
			metaData.add("Minecraft", "Description", description);
			metaData.add("Minecraft", "Crash Log", completeReport);
			
			if (crashHandler.onCrash(description, thrown) && sendFlag) {
				client.notify(thrown, metaData.meta);
			}
		}
	}
	
}
