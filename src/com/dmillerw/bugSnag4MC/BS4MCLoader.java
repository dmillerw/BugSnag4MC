package com.dmillerw.bugSnag4MC;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.6.4")
public class BS4MCLoader implements IFMLLoadingPlugin {

	public static boolean deobf;
	
	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return null;
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
	}

}
