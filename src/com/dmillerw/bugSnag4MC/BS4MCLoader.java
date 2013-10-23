package com.dmillerw.bugSnag4MC;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@MCVersion("1.6.4")
@TransformerExclusions("com.dmillerw.bugSnag4MC")
public class BS4MCLoader implements IFMLLoadingPlugin {

	public static boolean deobf;
	public static File coremodLocation;
	
	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"com.dmillerw.bugSnag4MC.asm.BS4MCTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return "com.dmillerw.bugSnag4MC.BS4MCCore";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		deobf = (Boolean)data.get("runtimeDeobfuscationEnabled");
		coremodLocation = (File)data.get("coremodLocation");
	}

}
