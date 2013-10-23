package com.dmillerw.bugSnag4MC.core;

import java.util.HashMap;

import com.bugsnag.Client;
import com.dmillerw.bugSnag4MC.api.IBugSnagHandler;

public class BS4MCAPIHandler {

	public static HashMap<String, IBugSnagHandler> registeredHandlers = new HashMap<String, IBugSnagHandler>();
	public static HashMap<String, Client> registeredClients = new HashMap<String, Client>();
	
	public static void registerHandler(IBugSnagHandler handler) {
		registeredHandlers.put(handler.getModID(), handler);
		registeredClients.put(handler.getModID(), new Client(handler.getAPIKey()));
	}
	
}
