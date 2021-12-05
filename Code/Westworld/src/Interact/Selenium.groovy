package Interact;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Selenium {
	
	//Two lists for inputs	
	//format: title#subtitle@value
	ArrayList<String> preferencesInputs = new ArrayList<String>();
	//format:#title@value
	ArrayList<String> simulatorsInputs = new ArrayList<String>();
	
	//the input code
	String codePath;
	
	//return string
	String log = "";
	
	//default value
	String defaultValue = "";
	
	//get the input from outside
	public Selenium(ArrayList<String> preferencesInputs, ArrayList<String> simulatorsInputs, String codePath, String defaultValue) 
	{
		this.codePath = codePath;
		this.preferencesInputs = preferencesInputs;
		this.simulatorsInputs = simulatorsInputs;
		this.defaultValue = defaultValue;
		
		System.setProperty("webdriver.chrome.driver","E:\\eclipse-workspace\\IoTConcolicTool\\libary\\chromedriver.exe");
		
		//for windows
		//textArea.sendKeys(Keys.chord(Keys.CONTROL, "a"));
	}
	
	//To get the log
	public String getLog() 
	{
		return log;
	}
	
	//I copied all your code into this function
	public void test() throws InterruptedException
	{
	
		//Read code from file, save as code String
		BufferedReader b = null;
		String s = "";
		String code = "";
		
		try {
			b = new BufferedReader( new FileReader(codePath));
			while( (s = b.readLine()) != null){
				code = code + s + "\n";
			}
		} catch (FileNotFoundException e) {
			System.err.println("Unable to find that file.");
		} catch (IOException e) {
			System.err.println("Unable to read that file.");
		}
	
		
		
		WebDriver driver = new ChromeDriver();
		
		driver.get("https://account.smartthings.com/login?redirect=https%3A%2F%2Fgraph.api.smartthings.com%2F");
		driver.manage().window().maximize();
		//navigate to samsung login page
		WebElement samsungAcc = driver.findElement(By.className("sa-login-btn"));
		samsungAcc.click();
		
		//username
		WebElement username = driver.findElement(By.id("iptLgnPlnID"));
		username.sendKeys("sem15@email.sc.edu");

		//password
		WebElement password = driver.findElement(By.id("iptLgnPlnPD"));
		password.click();
		password.sendKeys("AIG634LM");
		
		//sign in
		WebElement signIn = driver.findElement(By.id("signInButton"));
		signIn.click();
		
		
		//let page load
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		//go to smartapps
		WebElement smartApps = driver.findElement(By.linkText("My SmartApps"));
		smartApps.click();
		
		WebDriverWait l = new WebDriverWait(driver, 10);
		l.until(ExpectedConditions.visibilityOfElementLocated(By.className("namespace-name")));
		
		
		
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		//click on app
		//List<WebElement> apps = driver.findElements(By.className("namespace-name"));
		//apps.get(0).click();
		//WebElement app = driver.findElement(By.cssSelector("#\\33 a0c93e2-d575-4bc6-ae67-35f65d271318 > td.namespace-name.sorting_1 > a"));
		//app.click();
		
		
		
		//find which button is new apps
		//List<WebElement> titleBtns = driver.findElements(By.className("pull-right"));
		//System.out.println(titleBtns.size());
		
		
		
		WebElement newApp = driver.findElement(By.xpath("//a[@href='/ide/app/create']"));
		newApp.click();
		
		Thread.sleep(2000);
		
		WebElement name = driver.findElement(By.id("name"));
		name.sendKeys("test");
		
		WebElement namespace = driver.findElement(By.id("namespace"));
		namespace.sendKeys("sem15");
		
		
		WebElement desc = driver.findElement(By.id("description"));
		desc.sendKeys("test");
		
		
		//find and click create button
		List<WebElement> col = driver.findElements(By.xpath("//*[@id=\"from-form\"]/form/div[2]/div"));
		
		WebElement resources = driver.findElement(By.linkText("OAuth"));
		WebElement create = col.get(0).findElement(By.id("create"));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", resources);
		create.click();
		
		
		
		
		
		
		//locate code mirror scroll *added to help debug
		WebElement codeMirrorScroll = driver.findElement(By.className("CodeMirror-scroll"));
		
		
		//locate and click on code-mirror
		WebElement codeMirror = codeMirrorScroll.findElement(By.className("CodeMirror-code"));
		codeMirror.click();
		
		//select all code and delete, then replace with new code
		WebElement textArea = driver.findElement(By.cssSelector(".CodeMirror textarea"));

		
		
		Thread.sleep(3000);
		textArea.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		textArea.sendKeys(Keys.DELETE);
		textArea.sendKeys(code);
		WebElement save = driver.findElement(By.id("save"));
		save.click();
		
		
		
		
		
		//enter simulator
		WebElement sim = driver.findElement(By.className("run-btn"));
		sim.click();
				
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
				
		//scroll to button and then set location
		//WebElement location = driver.findElement(By.xpath("//*[@id='ide-location-panel']/div/div[2]/button"));
		WebElement location = driver.findElement(By.className("btn-success"));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", location);
				
		//wait until location button loads
		WebDriverWait wait = new WebDriverWait(driver, 8);
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='ide-location-panel']/div/div[2]/button")));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-success")));
		location.click();
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		
		
		
			
		
		
		//Get all preference sections
		List<WebElement> sections = driver.findElements(By.className("section"));


		
		
		
		//create 3 arrayLists for title, subtitle, and then value. the index will be the same for all three
		ArrayList<String> allTitles = new ArrayList<String>();
		ArrayList<String> allSubtitles = new ArrayList<String>();
		ArrayList<String> allValues = new ArrayList<String>();
		ArrayList<String> titlesPlusSubs = new ArrayList<String>();
		
		if(preferencesInputs != null) { //removed preferencesInputs.size() > 0
		if(preferencesInputs.size() > 0) {
		
		for(int i = 0; i < preferencesInputs.size(); i++) {
			String temp = preferencesInputs.get(i);
			String tempTitle = "";
			String tempSubtitle = "";
			String tempValue = "";
			
			//extract titles
			int pIndex = 0;
			int sIndex = 0;
			
			
			//get value from preference string
			for(int t = 0; t < temp.length(); t++) {
				if(temp.charAt(t) == '@') {
					pIndex = t;
					
					for(int p = pIndex+1; p < temp.length(); p++) {
						tempValue = tempValue + temp.charAt(p);
					}
					break;
				}
			}
				allValues.add(tempValue);
			
			
				
				//get value from preference subtitle
				for(int t = 0; t < temp.length(); t++) {
					if(temp.charAt(t) == '#') {
						sIndex = t;
					}
				}
				
				for(int k = sIndex+1; k < pIndex; k++) {
					tempSubtitle = tempSubtitle + temp.charAt(k);
				}
				allSubtitles.add(tempSubtitle);
				
				
				//get value from preference title
				for(int g = 0; g < sIndex; g++) {
				tempTitle = tempTitle + temp.charAt(g);
		}
				allTitles.add(tempTitle);
				
				
		}
				//fill list of title + subtitle
				for(int g = 0; g < allTitles.size(); g++) {
					titlesPlusSubs.add(allTitles.get(g) + allSubtitles.get(g));
				}
				
			
		
		} //end if preferencesInputs != null
	}	//end if preferencesInputs > 0
			
		
		
		
		//Collect sections and then iterate through them one by one
		
		WebElement tempTitle;
		
		//for all sections, determine case and enter input
		for(int i = 0; i < sections.size(); i++) {
			
			tempTitle = sections.get(i);
			WebElement tempSubtitle = sections.get(i);
			WebElement inputWrapper = sections.get(i);
			List<WebElement> subTitles;
			String tempVal = "";
			
			Boolean match = false;
			
			tempTitle = tempTitle.findElement(By.className("title"));
			//System.out.println(tempTitle.getText());
			inputWrapper = sections.get(i).findElement(By.className("input-wrapper"));
			subTitles = inputWrapper.findElements(By.className("input"));
			
			//for each subtitle in title
			for(int g = 0; g < subTitles.size(); g++) {
				tempSubtitle = subTitles.get(g).findElement(By.className("header"));
				tempSubtitle = tempSubtitle.findElement(By.className("title"));
				
				
				
				//we now have title and subtitle, so parse for a match with allTitles and allSubTitles
				
				//cases where we enter value or do nothing, but there is a match
				if(allTitles.size() > 0) {
				String tempTitlePlusSub = tempTitle.getText() + tempSubtitle.getText();
				
				//System.out.println("found value index: " + i + tempTitlePlusSub);
				
				for(int k = 0; k < titlesPlusSubs.size(); k++) {
					
					if(titlesPlusSubs.get(k).equals(tempTitlePlusSub)) {
						//System.out.println("found match: " + titlesPlusSubs.get(k) + " = " + tempTitlePlusSub +
							//	"matched value : " + allValues.get(k));
						
						//we found a match and now know the value for this subtitle
						tempVal = allValues.get(k);
						match = true;
						
					}
				}
				}//just changed
				
				//if there was a match
				if(match == true) {
					
					//if tempVal is "empty"
					if(tempVal.equals("empty")) {
						//System.out.println(tempTitle.getText() + " " + tempSubtitle.getText() + " " +
						//		tempVal + "  was empty so do nothing");
					}
				
					//if tempVal is not "empty" find input type and enter tempVal
					else {
						
						WebElement tray = subTitles.get(g).findElement(By.className("tray"));
						Boolean isSelect = tray.findElements(By.tagName("select")).size() > 0;
						Boolean isUL = subTitles.get(g).findElements(By.tagName("ul")).size() > 0;
							
						
						//Select Case
						if(isSelect == true) {
						//System.out.println(tempSubtitle.getText() + " Is Select Case");
						
						//make sure element is not hidden
						
						List<WebElement> downArrows = subTitles.get(g).findElements(By.cssSelector("div.arrow.up.down"));
						
						
						
						if(downArrows.size() > 0) {
							}
							
							else {
								WebElement btn = subTitles.get(g).findElement(By.className("arrow"));
								btn.click();
							}
						
						
						WebElement correctOption = tray.findElement(By.tagName("select"));
						WebElement select = correctOption; //find select drop-down
						Select dropDown = new Select(select);
						
						List<WebElement> options = dropDown.getOptions();
						
						//scan options for correct index
						for(int k = 0; k < options.size(); k++) {
							String option = options.get(k).getText();
							if(option.contentEquals(tempVal)) {
								dropDown.selectByIndex(k);
								break;
								
							}
						}
					} // select case
						
						
						
						
						
						//UL Case
						else if(isUL == true) {
							//System.out.println(tempSubtitle.getText() + " Is UL Case");
							
							//make sure element is not hidden
							
							List<WebElement> downArrows = subTitles.get(g).findElements(By.cssSelector("div.arrow.up.down"));
							
							if(downArrows.size() > 0) {
								}
								
								else {
									WebElement btn = subTitles.get(g).findElement(By.className("arrow"));
									btn.click();
								}
							
							//select ul class and the gather options
							
							List<WebElement> uls = subTitles.get(g).findElements(By.className("clearfix"));
							
							
							//scan uls options to gather all li options
							
							for(int k = 0; k < uls.size(); k++) {
								List<WebElement> lis = uls.get(k).findElements(By.tagName("li"));
								
								
								//scan lis for matching text
								for(int p = 0; p < lis.size(); p++) {
									if(lis.get(p).getText().equals(tempVal)) {
										WebElement pullRight = lis.get(p).findElement(By.className("pull-right"));
										pullRight.click();
										break;
									}
								}
							}
						} //end UL case
						
						
						//Text Box Case
						else {
							
							//make sure element is not hidden
							
							List<WebElement> downArrows = subTitles.get(g).findElements(By.cssSelector("div.arrow.up.down"));
							
							if(downArrows.size() > 0) {
								}
								
								else {
									WebElement btn = subTitles.get(g).findElement(By.className("arrow"));
									btn.click();
								}
							
							
							
							
							
							//scroll to element
							WebElement temp = subTitles.get(g);
							((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", temp);
							temp = temp.findElement(By.cssSelector("input[type='text']"));
							driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
							temp.click();
							temp.sendKeys(tempVal);
						} //end Text Box Case
						
						
						
						
						
						
						
						
						
				} //else tempVal was not "empty"
			} //end of if section had match
				
				//section didn't have a match
				else {
					
					
					WebElement tray = subTitles.get(g).findElement(By.className("tray"));
					Boolean isSelect = tray.findElements(By.tagName("select")).size() > 0;
					Boolean isUL = subTitles.get(g).findElements(By.tagName("ul")).size() > 0;
						
					
					//Select Case, choose random value
					if(isSelect == true) {
					
						//System.out.println("Select Case");
					//make sure element is not hidden
					
					List<WebElement> downArrows = subTitles.get(g).findElements(By.cssSelector("div.arrow.up.down"));
					
					
					
					if(downArrows.size() > 0) {
						}
						
						else {
							WebElement btn = subTitles.get(g).findElement(By.className("arrow"));
							btn.click();
						}
					
					
					WebElement correctOption = tray.findElement(By.tagName("select"));
					WebElement select = correctOption; //find select drop-down
					Select dropDown = new Select(select);
					
					List<WebElement> options = dropDown.getOptions();
					
					//choose random option
					Random rand = new Random();
					int r = rand.nextInt(options.size());
					dropDown.selectByIndex(r);
					
					
				} // select case
					
					
					
					//UL Case, choose random value
					else if(isUL == true) {
						//System.out.println("UL Case");
						//make sure element is not hidden
						
						List<WebElement> downArrows = subTitles.get(g).findElements(By.cssSelector("div.arrow.up.down"));
						
						if(downArrows.size() > 0) {
							}
							
							else {
								WebElement btn = subTitles.get(g).findElement(By.className("arrow"));
								btn.click();
							}
						
						//select ul class and the gather options
						
						List<WebElement> uls = subTitles.get(g).findElements(By.className("clearfix"));
						
						
						//scan uls options to gather all li options
						
						//choose random uls, then random li
						Random rand = new Random();
						
						List<WebElement> lis = uls.get(0).findElements(By.tagName("li"));
						int r = rand.nextInt(lis.size());
						
						WebElement pullRight = lis.get(r).findElement(By.className("pull-right"));
						pullRight.click();
						
					} //end UL case
					
					
					
					//Text Box Case
					else {
						String phoneVal = "555";
						//System.out.println("Textbox Case");
						
						//make sure element is not hidden
						
						List<WebElement> downArrows = subTitles.get(g).findElements(By.cssSelector("div.arrow.up.down"));
						
						if(downArrows.size() > 0) {
							}
							
							else {
								WebElement btn = subTitles.get(g).findElement(By.className("arrow"));
								btn.click();
							}
						
						
						
						
						
						//scroll to element
						WebElement temp = subTitles.get(g);
						((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", temp);
						List<WebElement> isPhone = temp.findElements(By.className("phone"));
						temp = temp.findElement(By.cssSelector("input[type='text']"));
						driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
						temp.click();
						if(isPhone.size() > 0)
							temp.sendKeys(phoneVal);
						else
						temp.sendKeys(defaultValue);
					} //end Text Box Case
					
						
					
					
				} //end sections didn't have a match
				
				
		} //end for each subtitle
		}
		
		
		
		

	
		
		
				//click install btn
				WebElement install = driver.findElement(By.name("update"));
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", install);
				
				//wait until install button loads
				wait = new WebDriverWait(driver, 8);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("update")));
				install.click();
		
				
				
		
				
				
				
		
				//wait until page has loaded
				//WebDriverWait w = new WebDriverWait(driver, 10);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("form-inline")));
				
				
				
				
				//collect sensors
				WebElement content = driver.findElement(By.cssSelector("#ide-simulator-panel > div.panel-body > div.content"));
				List<WebElement> devices = content.findElements(By.className("deviceLabel"));
				List<WebElement> drops = driver.findElements(By.className("form-inline"));
				
				WebElement device;
				WebElement btn;
				WebElement correctSensor;
				
				
				//System.out.println("devices size: " + devices.size());
				//System.out.println("drops size: " + drops.size());
			
				
				//for all sensor commands
				
				if(simulatorsInputs != null ) {
				if(simulatorsInputs.size() > 0) {
				for(int i = 0; i < simulatorsInputs.size(); i++) {
					
					String tempTitleSim = "";
					String tempVal = "";
					String temp = simulatorsInputs.get(i);
					int valIndex = temp.lastIndexOf('@');
					int correctIndex = 0;
					
					//System.out.println(temp);
					
					//parse title
					for(int j = 1; j < valIndex; j++) {
						tempTitleSim = tempTitleSim + temp.charAt(j);
					}
					
					
					//parse value
					for(int u = valIndex+1; u < temp.length(); u++) {
						tempVal = tempVal + temp.charAt(u);
					}
					//System.out.println("number of devices found: " + devices.size());
					
					//for all devices
					for(int p = 0; p < devices.size(); p++) {
					device = devices.get(p); //findElement(By.className("deviceLabel"));
					//find correct sensor
					if(device.getText().equals(tempTitleSim)) {
						//System.out.println("device found");
						//System.out.println(device.getText());
						correctIndex = p;
						
						//index will be same for all lists at this step
						
					}
					}
					
					//System.out.println("index: " + correctIndex);
					
					//we now have the correct drop-down index
					correctSensor = drops.get(correctIndex);
					correctSensor = correctSensor.findElement(By.className("form-control"));
					//select correct value, then click button of sensor
					
					WebElement select = correctSensor;
					Select dropDown = new Select(select);
					
					List<WebElement> options = dropDown.getOptions();
					//System.out.println(options.size() + " options in select box");
					
					//scan options for correct index
					for(int k = 0; k < options.size(); k++) {
						String option = options.get(k).getText();
						if(option.contentEquals(tempVal)) {
							//System.out.println("Found right one: " + option);
							dropDown.selectByIndex(k);
							btn = drops.get(correctIndex);
							btn = btn.findElement(By.className("btn"));
							btn.click();
							break;
							
						}
					}
					
					
					
				}
				
				
				
				//allow time for log to load
				Thread.sleep(5000);
				//WebDriverWait l = new WebDriverWait(driver, 10);
				//l.until(ExpectedConditions.visibilityOfElementLocated(By.className("debug")));
				
				
				//get log and then save to log variable
				
				
				List<WebElement> logs = driver.findElements(By.className("log"));
				
				for(int i = 0; i < logs.size(); i++) {
					//System.out.println(traces.get(i).getText());
					log = log + logs.get(i).getText() + "\n";
				}
				
				
				}
				
				}
	
		
				
				
		
		

		//uninstall and close.
		Thread.sleep(3000);
		WebElement uninstall = driver.findElement(By.xpath("//*[@id='stop']"));
		uninstall.click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.close();
		
		
		
	}
    
//	//how to run this component
//	public static void main(String[] args) throws InterruptedException {
//		
//		//init the two input lists and the code
//		ArrayList<String> preferencesInputs = new ArrayList<String>();
//		ArrayList<String> simulatorsInputs = new ArrayList<String>();
//		
//		//String codePath = "humidity.txt";
//		//String codePath2 = "rainSensor.txt";
//		//String defaultValue = "5";
//		
//		//preferencesInputs.add("When the humidity falls below:#Percentage ?@20");
//		//preferencesInputs.add("When the humidity rises above:#Percentage ?@60");
//		//preferencesInputs.add("Control this switch:#Which?@<none>");
//		//preferencesInputs.add("Control this switch:#Which?@switch1");
//		//preferencesInputs.add("Notifications#Send a push notification?@empty");
//		//preferencesInputs.add("Notifications#Send a Text Message?@123");
//		//simulatorsInputs.add("#humiditySensor1@70%");
//		//simulatorsInputs.add("#switch1@off");
//		
//		
//		//preferencesInputs = null;
//		//simulatorsInputs = null;
//		
//		//Selenium se = new Selenium(preferencesInputs, simulatorsInputs, codePath, defaultValue);
//		//se.test();
//		//String log = se.getLog();
//		//System.out.println(log);
//		
//		
//		
//	
//	}

}