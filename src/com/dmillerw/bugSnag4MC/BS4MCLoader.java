package com.dmillerw.bugSnag4MC;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import com.dmillerw.bugSnag4MC.api.Constants;

import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@MCVersion("1.6.4")
@TransformerExclusions("com.dmillerw.bugSnag4MC")
public class BS4MCLoader implements IFMLLoadingPlugin {

	public static boolean deobf;

	public static File dependDir;
	
	public BS4MCLoader() {
		if (dependDir != null) {
			return;
		}

		try {
			Field field = CoreModManager.class.getDeclaredField("mcDir");
			field.setAccessible(true);
			File minecraftDir = (File) field.get(null);
			dependDir = new File(minecraftDir, "mods" + File.pathSeparator + Constants.ID.toLowerCase() + File.pathSeparator + "dependencies");
			dependDir.mkdirs();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
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
	}

}
