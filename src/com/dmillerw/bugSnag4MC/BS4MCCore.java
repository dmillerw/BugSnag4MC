package com.dmillerw.bugSnag4MC;

import java.util.Arrays;

import com.dmillerw.bugSnag4MC.core.InternalAPIHandler;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class BS4MCCore extends DummyModContainer {

	public static final String IMC_KEY_BASIC = "api_key";
	public static final String IMC_KEY_ADV = "crash_handler";
	
	public static final String ID = "BS4MC";
	public static final String NAME = "BugSnag4MC";
	public static final String VERSION = "1.0.0";
	
	public static BS4MCCore instance;
	
	public BS4MCCore() {
		super(new ModMetadata());
		ModMetadata meta = super.getMetadata();
		meta.modId = ID;
		meta.name = NAME;
		meta.version = VERSION;
		meta.description = "Integrates BugSnag with Minecraft";
		meta.authorList = Arrays.asList("dmillerw", "Royalixor");
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}

	@Subscribe
	public void preInit(FMLPreInitializationEvent e) {
		instance = this;
	}
	
	@Subscribe
	public void init(FMLInitializationEvent e) {
		ImmutableList<IMCMessage> pendingMessages = FMLInterModComms.fetchRuntimeMessages(this);
		
		for (IMCMessage message : pendingMessages) {
			if (message.isStringMessage()) {
				if (message.key.equalsIgnoreCase(IMC_KEY_BASIC)) {
					InternalAPIHandler.handleBasicIMC(message.getSender(), message.getStringValue());
				} else if (message.key.equalsIgnoreCase(IMC_KEY_ADV)) {
					InternalAPIHandler.handleBasicIMC(message.getSender(), message.getStringValue());
				} else {
					FMLLog.warning("[" + ID + "] " + message.getSender() + " tried to register a crash handler, but failed to do so correctly! " + message.key + " is not a valid key!", new Object[0]);
				}
			} else {
				FMLLog.warning("[" + ID + "] " + message.getSender() + " tried to register a crash handler, but failed to do so correctly! Message value must be a String!", new Object[0]);
			}
		}
	}
	
}
