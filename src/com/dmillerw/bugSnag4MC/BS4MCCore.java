package com.dmillerw.bugSnag4MC;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class BS4MCCore extends DummyModContainer {

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
	
}
