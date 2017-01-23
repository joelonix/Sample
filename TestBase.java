package com.ingenico.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.ingenico.commonconstants.CommonConstants;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

public class TestBase 
{

	public static Properties config, objR, commonOR;
	public String  noOfPage,sProject,fileName,imgPath,error,value;
	public static WebDriverWait wait;
	public static WebDriver driver;
	
	/**TODO--Methods which Initialize XML and Current Project--*/
	static
	{
		objR=new Properties();				
		config=new Properties();
		commonOR=new Properties();
	}
	
	/**
	 *  Initializing the Tests
	 *  Loading xml files	 
	 * @throws FileNotFoundException 
	 */
	public void initialize() {
		try {
			config.loadFromXML(new FileInputStream(CommonConstants.CONFIGFILE));
			commonOR.loadFromXML(new FileInputStream(CommonConstants.COMMONCONFIGFILE));
			String pageName = getUIMapPage();
			objR.loadFromXML(new FileInputStream(pageName));
			
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("XML config files not loaded as expected", e);
		}
		
	}
	
	/**
	 *  initializing the Tests
	 * @throws Exception
	 */
	@BeforeSuite(alwaysRun=true)
	public void initSetUp() throws Exception {
		initialize();
	}
	
	
	/**
	 * Get UIMap Page
	 * @return uiMap
	 */
	public String getUIMapPage() {
		String[] projectName = this.getClass().toString().split(" ")[1].trim().split("\\.");	
		String uiMap = ""; 
		sProject=projectName[projectName.length-2];
		if(sProject!="")
		{
			uiMap = CommonConstants.OBJECTLOC + sProject + "_UIMap.xml";
		}
		else{
			Assert.fail("Cannot identify currently running project!!!");
		}
		return uiMap;
	}
	
	/*TODO-Method for setting up browser, Login, Logout and closing the browser-*/
	/**
	 * Setting the browser profiles
	 * @param browser
	 * @throws MalformedURLException	 
	 */
	@Parameters({"browser"})
    @BeforeSuite(alwaysRun=true)
	public void setup(String browser) {
		//		browser=browserVal;
		DesiredCapabilities capability=null;
		try{
			if("firefox".equalsIgnoreCase(browser)){			
				FirefoxProfile profile = new FirefoxProfile();
				profile.setEnableNativeEvents(true);
				profile.setPreference("browser.download.folderList", 2);
				profile.setPreference("browser.download.manager.showWhenStarting",false);
				//profile.setPreference("browser.download.dir",CommonConstants.fileDownloadPath);
				profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf,application/force-download,application/x-download,text/csv,image/jpeg");			    
				profile.setPreference("pdfjs.disabled", true);
				capability= DesiredCapabilities.firefox();			
				capability.setCapability("unexpectedAlertBehaviour", "ignore");
				capability.setCapability(FirefoxDriver.PROFILE, profile);				
				driver = new FirefoxDriver(capability);
			}

			if("internet explorer".equalsIgnoreCase(browser)){		
				System.setProperty("webdriver.ie.driver", CommonConstants.IEDRIVERPATH+"\\IEDriverServer.exe");
				capability= DesiredCapabilities.internetExplorer();				
				capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);  
				capability.setPlatform(org.openqa.selenium.Platform.WINDOWS);
				driver=new InternetExplorerDriver(capability);
			}
			//driver.get(testDataOR.get("URLEverest"));
			//To assert IE certificate error message
			if("internet explorer".equalsIgnoreCase(browser)){
				driver.navigate().to("javascript:document.getElementById('overridelink').click()");
				driver.navigate().to("javascript:document.getElementById('overridelink').click()");
			}
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.manage().window().maximize();
			wait = new WebDriverWait(driver, 10);
			driver.get(config.getProperty("browserURL"));
		}
		catch(Throwable t){
			Assert.fail("Exception thrown while opening browser");
		}
	}
	
	
	/**
	 * It's for common method for object used in module
	 * @param object
	 * @return elt
	 */
	public WebElement getObject(String object) {
		WebElement elt = null;
		String locators;
		int counter =0;
		try {
			
			locators=object.substring(object.lastIndexOf('_')+1);
			forloop:	for(counter=0;counter<4;counter++)
			{
				try{
					switch (locators) {
					case "link":
						elt = driver.findElement(By.linkText(objR.getProperty(object)));
						break forloop;
					case "xpath":					
						elt = driver.findElement(By.xpath(objR.getProperty(object)));
						break forloop;
					case "css":	
						elt = driver.findElement(By.cssSelector(objR.getProperty(object)));
						break forloop;
					case "id":					
						elt = driver.findElement(By.id(objR.getProperty(object)));
						break forloop;
					case "name":
						elt = driver.findElement(By.name(objR.getProperty(object)));	
						break forloop;
					case "classname":
						elt = driver.findElement(By.className(objR.getProperty(object)));	
						break forloop;
					default:
						Assert.fail("Object locator format is not proper");
						break;
					}
				}
				catch (NoSuchElementException e1)
				{				
					//TestBase.waitNSec(1);
				}
			}
			if(counter==4)
			{
				Assert.assertFalse("".equals(elt));

			}
		} catch (NoSuchElementException e)
		{					
			Assert.fail("Cannot find object with key -- "+ object);
		}//end of for							
		return elt;
	}
	
	
	public void login(){
		getObject("login_id").sendKeys("joelonix@gmail.com");
		getObject("login_id").sendKeys("joelonix@gmail.com");
	}
	
	/**
	 * Captures screenshot on failure
	 * @param filename	 
	 */
	public void captureScreenShotOnFailure(String filename) {
		try {
			//WebDriver augmentedDriver = new Augmenter().augment(driver);
			File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);			
			FileUtils.copyFile(source, new File(File.separator+ filename + ".jpg"));
		}
		catch( IOException e) {
			//logger.error("Failed to capture screenshot: " + e.getMessage());
		}
	}
}
