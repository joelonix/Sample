package com.ingenico.commonconstants;

import java.io.File;

public class CommonConstants {
	
	public final static String USERDIR="user.dir";
	public final static String DATAFOLDER="data";
	public final static String CONFIGFILE=System.getProperty(USERDIR)+File.separator+"config"+File.separator+"server-config.xml";
	public final static String IEDRIVERPATH=System.getProperty(USERDIR)+File.separator+"external"+File.separator+"selenium";
	public final static String OBJECTLOC=System.getProperty(USERDIR)+File.separator+"object-locator"+File.separator;
	public final static String COMMONCONFIGFILE = "object-locator"+File.separator+"common_UIMap.xml";

}
