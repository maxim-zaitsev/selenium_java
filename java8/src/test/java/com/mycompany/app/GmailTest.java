package com.mycompany.app;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.concurrent.TimeUnit;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.IOException;
import java.io.InputStream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.CoreMatchers;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.fail;

public class GmailTest {
	private WebDriver driver;
	private static Actions actions;
	private WebDriverWait wait;
	private int flexibleWait = 5;
	private int implicitWait = 1;
	private long pollingInterval = 500;
	private String baseUrl = "https://www.google.com/gmail/about/#";
	private static final String browser = "firefox";
	private static String osName;
	private static TakesScreenshot screenshot;

	public static String getOsName() {
		if (osName == null) {
			osName = System.getProperty("os.name");
		}
		return osName;
	}

	@SuppressWarnings("deprecation")
	@BeforeSuite
	public void beforeSuiteMethod() throws Exception {
		getOsName();
		if (browser.equals("chrome")) {
			System.setProperty("webdriver.chrome.driver",
					(new File("c:/java/selenium/chromedriver.exe")).getAbsolutePath());
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			ChromeOptions options = new ChromeOptions();

			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			String downloadFilepath = System.getProperty("user.dir")
					+ System.getProperty("file.separator") + "target"
					+ System.getProperty("file.separator");
			chromePrefs.put("download.default_directory", downloadFilepath);
			chromePrefs.put("enableNetwork", "true");
			options.setExperimentalOption("prefs", chromePrefs);
			options.addArguments("allow-running-insecure-content");
			options.addArguments("allow-insecure-localhost");
			options.addArguments("enable-local-file-accesses");
			options.addArguments("disable-notifications");
			// options.addArguments("start-maximized");
			options.addArguments("browser.download.folderList=2");
			options.addArguments(
					"--browser.helperApps.neverAsk.saveToDisk=image/jpg,text/csv,text/xml,application/xml,application/vnd.ms-excel,application/x-excel,application/x-msexcel,application/excel,application/pdf");
			options.addArguments("browser.download.dir=" + downloadFilepath);
			// options.addArguments("user-data-dir=/path/to/your/custom/profile");
			capabilities
					.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			driver = new ChromeDriver(capabilities);
		} else if (browser.equals("firefox")) {

			System.setProperty("webdriver.gecko.driver",
					osName.toLowerCase().startsWith("windows")
							? new File("c:/java/selenium/geckodriver.exe").getAbsolutePath()
							: "/tmp/geckodriver");
			System
					.setProperty("webdriver.firefox.bin",
							osName.toLowerCase().startsWith("windows") ? new File(
									"c:/Program Files (x86)/Mozilla Firefox/firefox.exe")
											.getAbsolutePath()
									: "/usr/bin/firefox");
			// https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			// use legacy FirefoxDriver
			capabilities.setCapability("marionette", false);
			// http://www.programcreek.com/java-api-examples/index.php?api=org.openqa.selenium.firefox.FirefoxProfile
			capabilities.setCapability("locationContextEnabled", false);
			capabilities.setCapability("acceptSslCerts", true);
			capabilities.setCapability("elementScrollBehavior", 1);
			FirefoxProfile profile = new FirefoxProfile();
			profile.setAcceptUntrustedCertificates(true);
			profile.setAssumeUntrustedCertificateIssuer(true);
			profile.setEnableNativeEvents(false);

			System.out.println(System.getProperty("user.dir"));
			capabilities.setCapability(FirefoxDriver.PROFILE, profile);
			try {
				driver = new FirefoxDriver(capabilities);
			} catch (WebDriverException e) {
				e.printStackTrace();
				throw new RuntimeException("Cannot initialize Firefox driver");
			}
		}
		actions = new Actions(driver);
		driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, flexibleWait);
		wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
		screenshot = ((TakesScreenshot) driver);
	}

	@AfterSuite
	public void afterSuiteMethod() throws Exception {
		driver.quit();
	}

	@BeforeMethod
	public void loadPage() {
		driver.get(baseUrl);
	}

	@AfterMethod
	public void resetBrowser() {
		driver.get("about:blank");
	}

	@Test(priority = 1, enabled = false)
	public void invalidPasswordTest() throws InterruptedException, IOException {

		driver.get("https://www.google.com/gmail/about/#");

		driver.findElement(By.xpath("//*[@data-g-label='Sign in']")).click(); // Sign
																																					// in
		Thread.sleep(1000);

		// Enter the email id
		enterEmailId(By.cssSelector("#identifierId"),
				"automationnewuser24@gmail.com");

		// click on next button
		clickNextButton(By.xpath(
				"//*[@id='identifierNext']/content/span[contains(text(),'Next')]"));

		// Enter the password
		enterPassword(By.xpath("//*[@id='password']//input"), "InvalidPwd");

		// click on next button
		clickNextButton(By.xpath(
				"//*[@id='passwordNext']/content/span[contains(text(),'Next')]"));

		// Inspect error messages
		List<WebElement> errMsg = waitForElements(
				By.xpath("//*[contains (text(),'Wrong password. Try again.')]"));
		assertTrue(errMsg.size() > 0);
	}

	@Test(priority = 2, enabled = true)
	public void invalidUsernameTest() throws InterruptedException, IOException {

		// click on Sign in link
		driver.findElement(By.xpath("/html/body/nav/div/a[2]")).click();

		enterEmailId(By.cssSelector("#identifierId"), "InvalidUser_UVW");

		// click on next button
		clickNextButton(By.xpath(
				"//*[@id='identifierNext']/content/span[contains(text(),'Next')]"));

		// Inspect error messages
		List<WebElement> errMsg = waitForElements(By.xpath(
				"//*[contains (text(), \"Couldn't find your Google Account\")]"));
		assertTrue(errMsg.size() > 0);
	}

	private List<WebElement> waitForElements(By locator) {

		return wait.until((WebDriver d) -> {
			List<WebElement> elements = new ArrayList<>();
			try {
				elements = d.findElements(locator);
			} catch (Exception e) {
				return null;
			}
			return (elements.size() > 0) ? elements : null;
		});

	}

	@Test(priority = 3, enabled = true)
	public void loginTest() throws InterruptedException, IOException {

		// Click on Sign in Link
		driver.findElement(By.xpath("/html/body/nav/div/a[2]")).click();

		// TODO: track the url change
		Thread.sleep(1000);

		// Enter the email id
		enterEmailId(By.cssSelector("#identifierId"),
				"automationnewuser24@gmail.com");
		/*
		File screenshotFile = screenshot.getScreenshotAs(OutputType.FILE);
		// Move image file to new destination
		FileUtils.copyFile(screenshotFile, new File("c:\\temp\\UserID.jpg"));
		*/

		// click on next button
		clickNextButton(By.xpath(
				"//*[@id='identifierNext']/content/span[contains(text(),'Next')]"));

		// Enter the password
		enterPassword(By.xpath("//*[@id='password']//input"),
				"automationnewuser2410");

		// Click on next button
		clickNextButton(By.xpath(
				"//*[@id='passwordNext']/content/span[contains(text(),'Next')]"));

		Thread.sleep(5000);
		// Click on profile image
		driver
				.findElement(
						By.xpath("//*[@id='gb']/div[1]/div[1]/div[2]/div[4]/div[1]/a/span"))
				.click();

		// Sign out
		driver.findElement(By.xpath(".//*[@id='gb_71']")).click();
	}


	private void clickNextButton(By locator) {
		wait.until((WebDriver d) -> {
			WebElement element = null;
			try {
				element = d.findElement(locator);
			} catch (Exception e) {
				return null;
			}
			return (element.isDisplayed()) ? element : null;
		}).click();
	}

	private void enterPassword(By locator, String data) {
		wait.until((WebDriver d) -> {
			WebElement element = null;
			try {
				element = d.findElement(locator);
			} catch (Exception e) {
				return null;
			}
			return (element.isDisplayed()) ? element : null;
		}).sendKeys(data);

	}

	private void enterEmailId(By locator, String data) {
		wait.until((WebDriver d) -> {
			WebElement element = null;
			try {
				element = d.findElement(locator);
			} catch (Exception e) {
				return null;
			}
			return (element.isDisplayed()) ? element : null;
		}).sendKeys(data);

	}

}
