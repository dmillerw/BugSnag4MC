package com.dmillerw.bugSnag4MC.core;

import com.dmillerw.bugSnag4MC.BS4MCCore;
import com.dmillerw.bugSnag4MC.api.APIHandler;
import com.dmillerw.bugSnag4MC.api.BasicCrashHandler;
import com.dmillerw.bugSnag4MC.api.ICrashHandler;

import cpw.mods.fml.common.FMLLog;

public class InternalAPIHandler {

	public static void handleBasicIMC(String modID, String apiKey) {
		APIHandler.registerHandler(new BasicCrashHandler(modID, apiKey));
	}
	
	public static void handleAdvIMC(String modID, String classLocation) {
		try {
			ICrashHandler handler = (ICrashHandler) Class.forName(classLocation).newInstance();
			APIHandler.registerHandler(handler);
		} catch (InstantiationException e) {
			FMLLog.warning("[" + BS4MCCore.ID + "] " + modID + " tried to register a crash handler, but an instance of the specified class could not be created!", new Object[0]);
		} catch (IllegalAccessException e) {
			// Huh?
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			FMLLog.warning("[" + BS4MCCore.ID + "] " + modID + " tried to register a crash handler, but the specified class could not be found!", new Object[0]);
		}
	}
	
}
