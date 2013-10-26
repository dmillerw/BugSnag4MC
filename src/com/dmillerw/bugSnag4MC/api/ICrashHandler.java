package com.dmillerw.bugSnag4MC.api;


public interface ICrashHandler {

	/** Unique id for this mod. Using the same ID as you do with FML registration is advised */
	public String getModID();
	
	/** Base package (or packages) that all of your mod's code resides in/under. Used for determining if your mod is the cause of the crash. */
	public String[] getBasePackages();
	
	/** Additional packages to watch */
	public String[] getAdditionalPackages();
	
	/** Whether only crashes related to this mod should be sent to your BugSnag account */
	public boolean submitRelatedCrashesOnly();

	/** BugSnag API key */
	public String getAPIKey();
	
	/** Custom data to be sent along with the crash report. The description of the crash, the side that crashed (SERVER/CLIENT) and Minecraft's full crash report are already sent */
	public void fillUserData(IMetaData meta);
	
	/** Extra code to run when Minecraft crashes 
	 * @return Whether or not the crash report should be sent 
	 */
	public boolean onCrash(String description, Throwable throwable, IMetaData meta);
	
}
