package com.dmillerw.bugSnag4MC.api;

import java.lang.reflect.Method;

import cpw.mods.fml.common.FMLLog;

public class APIHandler {

	// Currently on the fence about this. Should it be event based?
	public static void registerCrashHandler(ICrashHandler handler) {
		try {
			Class clazz = Class.forName("com.dmillerw.bugSnag4MC.core.InternalAPIHandler");
			Method registerMethod = clazz.getMethod("registerHandler", ICrashHandler.class);
			registerMethod.invoke(null, handler);
		} catch (Exception ex) {
			FMLLog.info("[" + Constants.ID + "] Failed to register crash handler! Report this to one of the " + Constants.ID + " devs!" , new Object[0]);
			ex.printStackTrace();
		}
	}
	
}
