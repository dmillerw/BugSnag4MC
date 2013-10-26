package com.dmillerw.bugSnag4MC;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;

import com.dmillerw.bugSnag4MC.api.Constants;

import net.minecraft.launchwrapper.LaunchClassLoader;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@MCVersion("1.6.4")
@TransformerExclusions("com.dmillerw.bugSnag4MC")
public class BS4MCLoader implements IFMLLoadingPlugin {

	public static boolean deobf = true;
	public static boolean deobfSet;
	
	public BS4MCLoader() {
        if (!deobfSet) {
        	try {
        		Field deobfField = CoreModManager.class.getDeclaredField("deobfuscatedEnvironment");
            		deobfField.setAccessible(true);
            		deobf = deobfField.getBoolean(null);
            		deobfSet = true;
        	} catch(Exception ex) {
        		FMLLog.info("[" + Constants.ID + "] Failed to get deobf variable! Assuming false.", new Object[0]);
        		deobf = false;
        	}
        }
		
		if (!deobf) {
			URL bugsnag = this.getClass().getResource("/lib/bugsnag.jar");
			URL json = this.getClass().getResource("/lib/json.jar");
			
			LaunchClassLoader classLoader = (LaunchClassLoader)BS4MCLoader.class.getClassLoader();
			classLoader.addURL(bugsnag);
			classLoader.addURL(json);
		}
	}
	
	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {BS4MCTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		return BS4MCCore.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

}
