package com.dmillerw.bugSnag4MC.core;

import java.util.HashMap;

import com.bugsnag.Client;
import com.dmillerw.bugSnag4MC.BS4MCCore;
import com.dmillerw.bugSnag4MC.api.Constants;
import com.dmillerw.bugSnag4MC.api.ICrashHandler;

import cpw.mods.fml.common.FMLLog;

public class InternalAPIHandler {

	public static HashMap<String, ICrashHandler> registeredHandlers = new HashMap<String, ICrashHandler>();
	public static HashMap<String, Client> registeredClients = new HashMap<String, Client>();

	public static void registerHandler(ICrashHandler handler) {
		if (registeredHandlers.containsKey(handler.getModID()) || registeredClients.containsKey(handler.getModID())) {
			FMLLog.warning("[" + Constants.ID + "] " + handler.getModID() + " tried to register a crash handler, but already has one. This request will be ignored!", new Object[0]);
			return;
		}
		
		registeredHandlers.put(handler.getModID(), handler);
		registeredClients.put(handler.getModID(), new Client(handler.getAPIKey()));
	}
	
	public static void handleBasicIMC(String modID, String apiKey) {
		registerHandler(new BasicCrashHandler(modID, apiKey));
	}
	
	public static void handleAdvIMC(String modID, String classLocation) {
		try {
			ICrashHandler handler = (ICrashHandler) Class.forName(classLocation).newInstance();
			registerHandler(handler);
		} catch (InstantiationException e) {
			FMLLog.warning("[" + Constants.ID + "] " + modID + " tried to register a crash handler, but an instance of the specified class could not be created!", new Object[0]);
		} catch (IllegalAccessException e) {
			// Huh?
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			FMLLog.warning("[" + Constants.ID + "] " + modID + " tried to register a crash handler, but the specified class could not be found!", new Object[0]);
		}
	}
	
}
