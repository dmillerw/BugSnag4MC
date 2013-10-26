package com.dmillerw.bugSnag4MC;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.Map;

import net.minecraft.launchwrapper.LaunchClassLoader;

import org.apache.commons.io.FileUtils;

import com.dmillerw.bugSnag4MC.api.Constants;
import com.dmillerw.bugSnag4MC.asm.BS4MCTransformer;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@MCVersion("1.6.4")
@TransformerExclusions("com.dmillerw.bugSnag4MC")
public class BS4MCLoader implements IFMLLoadingPlugin {

	public static File mcDir;
	
	public static boolean deobf = true;
	public static boolean deobfSet;
	
	public static String[] libFiles = new String[] {"bugsnag", "json"};
	
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
		
        if (mcDir == null) {
        	try {
        		Field mcDirField = CoreModManager.class.getDeclaredField("mcDir");
        			mcDirField.setAccessible(true);
            		mcDir = (File) mcDirField.get(null);
        	} catch(Exception ex) {
        		FMLLog.info("[" + Constants.ID + "] Failed to get Minecraft directory.", new Object[0]);
        	}
        }
        
		if (!deobf) {
			File destDir = new File(mcDir, "mods" + File.separator + Constants.ID.toLowerCase() + File.separator + "dependencies");
			for (String lib : libFiles) {
				if (!new File(destDir, lib + ".jar").exists()) {
					FMLLog.info("[" + Constants.ID + "] Couldn't find " + lib + ".jar. Extracting...", new Object[0]);
					
					try {
						InputStream is = getClass().getResourceAsStream("/lib/" + lib + ".jar");
						FileUtils.copyInputStreamToFile(is, new File(destDir, lib + ".jar"));
					} catch(IOException ex) {
						FMLLog.info("[" + Constants.ID + "] Failed to extract library: [" + lib + ".jar" + "]", new Object[0]);
					}
				} else {
					FMLLog.info("[" + Constants.ID + "] Found " + lib + ".jar. Skipping...", new Object[0]);
				}
			}
			
			LaunchClassLoader classLoader = (LaunchClassLoader)getClass().getClassLoader();
			
			for (File file : destDir.listFiles()) {
				try {
					classLoader.addURL(file.toURI().toURL());
				} catch(MalformedURLException ex) {
					FMLLog.info("[" + Constants.ID + "] Failed to add library to classpath: [" + file.getName() + "]", new Object[0]);
				}
			}
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
