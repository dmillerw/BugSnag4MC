package com.dmillerw.bugSnag4MC.api;

import java.util.HashMap;

import com.bugsnag.Client;
import com.dmillerw.bugSnag4MC.BS4MCCore;

import cpw.mods.fml.common.FMLLog;

public class APIHandler {

	public static HashMap<String, ICrashHandler> registeredHandlers = new HashMap<String, ICrashHandler>();
	public static HashMap<String, Client> registeredClients = new HashMap<String, Client>();

	public static void registerHandler(ICrashHandler handler) {
		if (APIHandler.registeredHandlers.containsKey(handler.getModID()) || APIHandler.registeredClients.containsKey(handler.getModID())) {
			FMLLog.warning("[" + BS4MCCore.ID + "] " + handler.getModID() + " tried to register a crash handler, but already has one. This request will be ignored!", new Object[0]);
			return;
		}
		
		registeredHandlers.put(handler.getModID(), handler);
		registeredClients.put(handler.getModID(), new Client(handler.getAPIKey()));
	}
	
}
