package com.mycompany.app;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.hamcrest.CoreMatchers.*;
import org.junit.Assert.*;

import java.net.BindException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.File;

import java.io.IOException;
import java.lang.RuntimeException;

import java.lang.Boolean.*;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import org.sikuli.basics.Settings;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

public class AppTest {

	// private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final StringBuffer verificationErrors = new StringBuffer();
	private static WebDriver driver;
	private static WebDriverWait wait;
	private static Actions actions;
	private static Screen screen;
	private static Pattern pattern = null;
	private static Match match = null;

	private static int flexibleWait = 5;
	private static int implicitWait = 1;
	private static long pollingInterval = 500;
	private static int sikuliTimeout = 5;

	@BeforeClass
	public static void setUp() {
		// driver = new FirefoxDriver();
		System.setProperty("webdriver.chrome.driver",
				"c:/java/selenium/chromedriver.exe");
		driver = new ChromeDriver();
		screen = new Screen();
		screen.setAutoWaitTimeout(sikuliTimeout);
		Settings.MoveMouseDelay = 1;
		Settings.Highlight = false;
		Settings.setShowActions(true);
		Settings.OcrTextRead = true;
		// the following setting does not work
		Settings.OcrDataPath = System.getProperty("user.dir") + "\\tessdata";
		wait = new WebDriverWait(driver, flexibleWait);
		wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
		actions = new Actions(driver);
	}

	@Before
	public void loadiBlankPage() {
		driver.get("about:blank");
	}

	@AfterClass
	public static void tearDown() {
		driver.quit();
		if (verificationErrors.length() != 0) {
			throw new RuntimeException(verificationErrors.toString());
		}
	}

	@Ignore
	@Test
	public void testCalendar() {
		driver.navigate().to(getPageContent("calendar.html"));
		WebElement calendarElement = wait.until(ExpectedConditions.visibilityOf(
				driver.findElement(By.cssSelector("input[name='calendar']"))));
		int xOffset = calendarElement.getSize().getWidth() - 2;
		int yOffset = calendarElement.getSize().getHeight() - 2;
		System.err.println(String.format("Hover at (%d, %d)", xOffset, yOffset));

		actions.moveToElement(calendarElement, xOffset, yOffset).build().perform();
		// Sometimes hover is not enough here for some reason
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		actions.moveToElement(calendarElement, xOffset, yOffset).click().build()
				.perform();
		String calendarDropDownImage = fullPath("calendar_dropdown_1920x1080.png");
		try {
			screen.exists(calendarDropDownImage, sikuliTimeout);
			screen.click(calendarDropDownImage, 0);
		} catch (FindFailed e) {
			verificationErrors.append(e.toString());
		}
		// Month Navigation
		String monthNavigateImage = fullPath("month_navigate_1920x1080.png");
		match = screen.exists(monthNavigateImage, sikuliTimeout);
		// Sikuli highlight method would force the Chrome native calendar to close
		// only useful for debugging during script development
		// match.highlight((float) 3.0);
		match.offset(-32, 0).click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		match = screen.exists(monthNavigateImage, sikuliTimeout);
		match.offset(32, 0).click();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		// Year Navigation
		String yearDropdownImage = "year_dropdown_1920x1080.png";
		match = screen.exists(fullPath(yearDropdownImage), sikuliTimeout);
		match.offset(-40, 0).click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		ArrayList<String> yearImages = new ArrayList<>(Arrays.asList(
				new String[] { "jan_1920x1080.png", "jan_highlight_1920x1080.png" }));
		Boolean done = false;
		for (String image : yearImages) {
			if (!done) {
				match = screen.exists(fullPath(image), sikuliTimeout);
				if (match != null) {
					String text = match.text();
					System.err.format("Sikuli read: '%s'\n", text);
					// if --- no text --- is printed
					// make sure tessdata is copied to
					// ${env:APPDATA}\Sikulix\SikulixTesseract\tessdata
					match.click();
					done = true;
				}
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	@Ignore
	@Test
	// https://www.youtube.com/watch?v=8OfnQEfzfmw
	public void testUpload() {
		String openButtonImage = fullPath("open_1920x1080.png");
		Pattern filenameTextBox = new Pattern(fullPath("filename_1920x1080.png"));
		driver.navigate().to(getPageContent("upload.html"));
		try {
			File tmpFile = File.createTempFile("foo", ".png");
			WebElement element = driver.findElement(By.tagName("input"));
			element.click();
			try {
				screen.exists(openButtonImage, sikuliTimeout);
				screen.type(filenameTextBox, tmpFile.getCanonicalPath());
				System.out.println("Uploading: " + tmpFile.getCanonicalPath());
				screen.click(openButtonImage, 0);
			} catch (FindFailed e) {
				verificationErrors.append(e.toString());
			}
			try {
				element = driver.findElement(By.tagName("input"));
				highlight(element);
				assertThat(element.getAttribute("value"),
						containsString(tmpFile.getName()));
			} catch (Error e) {
				verificationErrors.append(e.toString());
			}
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
	}

	@Ignore
	@Test
	// based on https://github.com/PiotrWysocki/phptravels.net
	// /blob/master/src/test/java/net/phptravels/home/HeaderTopOnFirefox.java
	public void testSikuliFindBasic() {
		int width = 800;
		int height = 600;
		driver.manage().window().setSize(new Dimension(width, height));
		String spanishFlagImage = fullPath("spanish_flag.png");
		driver.get("http://phptravels.net/en");
		WebElement languageElement = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
						"//div[contains(@class, 'navbar')]//a[@class='dropdown-toggle']"))));
		assertThat(languageElement, notNullValue());
		highlight(languageElement);
		languageElement.click();
		try {
			if (screen.exists(spanishFlagImage, sikuliTimeout) != null) {
				System.err.println("Found spanish flag image.");
				screen.click(spanishFlagImage, 0);
			}
		} catch (FindFailed e) {
			System.err.println("Can not Find flag image.");
			verificationErrors.append(e.toString());
		}
		wait.until(ExpectedConditions.urlToBe("http://phptravels.net/es"));
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}

	@Ignore
	@Test
	public void testSikuliFindResponsive() {
		int width = 400;
		int height = 700;
		driver.manage().window().setSize(new Dimension(width, height));
		String ucrainianFlagImage = fullPath("uncrainian_flag.png");
		driver.get("http://phptravels.net/en");
		try {

			WebElement navbarElement = wait
					.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
							"//div[contains(@class, 'navbar')]//button[@class='navbar-toggle']"))));
			System.err
					.println("Found nabvar : " + navbarElement.getAttribute("class"));
			highlight(navbarElement);
			navbarElement.click();
		} catch (Exception e) {
			System.err.println("Ignored Exception : " + e.toString());
		}
		WebElement languageElement = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
						"//div[contains(@class, 'navbar')]//a[@class='dropdown-toggle']"))));

		assertThat(languageElement, notNullValue());
		highlight(languageElement);
		languageElement.click();
		match = screen.exists(ucrainianFlagImage, sikuliTimeout);
		if (match != null) {
			System.err.println("Found ucrainian flag image.");
			match.highlight((float) 1.0);
			match.click();
		} else {

		}
		wait.until(ExpectedConditions.urlToBe("http://phptravels.net/uk"));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	@Ignore
	@Test
	public void testSikuliScroll() {
		int width = 800;
		int height = 400;
		driver.manage().window().setSize(new Dimension(width, height));
		driver.get("http://phptravels.net/en");
		WebElement languageElement = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
						"//div[contains(@class, 'navbar')]//a[@class='dropdown-toggle']"))));

		assertThat(languageElement, notNullValue());
		highlight(languageElement);
		languageElement.click();

		// NOTE: has no effect
		/*
		 * languageElement.sendKeys(Keys.ARROW_DOWN);
		 * actions.moveToElement(languageElement) .sendKeys(languageElement,
		 * Keys.ARROW_DOWN).build().perform();
		 */

		// NOTE: with bad selector get unknown error: cannot focus element
		/*
		 * try { WebElement languageDropdownElement = wait
		 * .until(ExpectedConditions.visibilityOf(driver
		 * .findElement(By.cssSelector(
		 * "body div.navbar div.container div.navbar div.navbar-collapse ul li"))));
		 * assertThat(languageDropdownElement, notNullValue());
		 * languageDropdownElement.sendKeys(Keys.ARROW_DOWN); } catch
		 * (WebDriverException e) { System.err.println("Ignored Exception : " +
		 * e.toString()); }
		 */
		// does not work: scrolls the whole page, drop down item list goes away
		/*
		 * WebElement navBarElement = wait
		 * .until(ExpectedConditions.visibilityOf(driver
		 * .findElement(By.cssSelector("div.navbar")))); assertThat(navBarElement,
		 * notNullValue()); actions.sendKeys(navBarElement,
		 * Keys.ARROW_DOWN).build().perform();
		 */

		// does not quite work: grabs the focus to and scrolls the whole page,
		// drop down item list remains open
		/*
		 * try { WebElement languageDropdownElement = wait.until(
		 * ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
		 * "body div.navbar div.container div.navbar div.navbar-collapse ul li"))));
		 * assertThat(languageDropdownElement, notNullValue());
		 * languageDropdownElement.sendKeys(Keys.ARROW_DOWN); } catch
		 * (WebDriverException e) { System.err.println("Ignored Exception : " +
		 * e.toString()); }
		 */

		sikuliFindOrScroll(fullPath("french_flag.png"), "flag of France",
				1 /* sikuliTimeout */);
		wait.until(ExpectedConditions.urlToBe("http://phptravels.net/fr"));
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}

	@Ignore
	@Test
	public void testSikuliDefultSimilarity() {
		int width = 800;
		int height = 400;
		// With default settings
		// Sikuli will confuse Malay flag with Ucrainian
		driver.manage().window().setSize(new Dimension(width, height));
		String malayFlagImage = fullPath("malay_flag.png");
		driver.get("http://phptravels.net/en");
		WebElement languageElement = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
						"//div[contains(@class, 'navbar')]//a[@class='dropdown-toggle']"))));

		assertThat(languageElement, notNullValue());
		highlight(languageElement);
		languageElement.click();
		sikuliFindOrScroll(fullPath("malay_flag.png"), "flag of Malay",
				1 /* sikuliTimeout */);

		wait.until(ExpectedConditions.urlToBe("http://phptravels.net/uk"));
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}

	// @Ignore
	@Test
	public void testSikuliMinSimilarity() {
		int width = 800;
		int height = 400;
		// With higher MinSimilarity settingd
		// it will find the malay flag
		Settings.MinSimilarity = 0.9;
		driver.manage().window().setSize(new Dimension(width, height));
		driver.get("http://phptravels.net/en");
		WebElement languageElement = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
						"//div[contains(@class, 'navbar')]//a[@class='dropdown-toggle']"))));

		assertThat(languageElement, notNullValue());
		highlight(languageElement);
		languageElement.click();

		sikuliFindOrScroll(fullPath("malay_flag.png"), "flag of Malay",
				1 /* sikuliTimeout */);
		wait.until(ExpectedConditions.urlToBe("http://phptravels.net/ms"));
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}

	// @Ignore
	@Test
	public void testSikuliScrollMiss() {
		int width = 800;
		int height = 400;
		// Color differences are ignored by Sikuli
		// it will confuse flags of France and Italy
		// regardless of similarity setting
		Settings.MinSimilarity = 0.9;
		driver.manage().window().setSize(new Dimension(width, height));
		driver.get("http://phptravels.net/en");
		WebElement languageElement = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
						"//div[contains(@class, 'navbar')]//a[@class='dropdown-toggle']"))));

		assertThat(languageElement, notNullValue());
		highlight(languageElement);
		languageElement.click();
		sikuliFindOrScroll(fullPath("italian_flag.png"), "flag of Italy",
				1 /* sikuliTimeout */);
		wait.until(ExpectedConditions.urlToBe("http://phptravels.net/fr"));
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}

	private String getPageContent(String pagename) {
		try {
			URI uri = AppTest.class.getClassLoader().getResource(pagename).toURI();
			System.err.println("Testing: " + uri.toString());
			return uri.toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private void highlight(WebElement element) {
		highlight(element, 100);
	}

	private void highlight(WebElement element, long highlight_interval) {
		if (wait == null) {
			wait = new WebDriverWait(driver, flexibleWait);
		}
		wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
			if (driver instanceof JavascriptExecutor) {
				((JavascriptExecutor) driver).executeScript(
						"arguments[0].style.border='3px solid yellow'", element);
			}
			Thread.sleep(highlight_interval);
			if (driver instanceof JavascriptExecutor) {
				((JavascriptExecutor) driver)
						.executeScript("arguments[0].style.border=''", element);
			}
		} catch (InterruptedException e) {
			// System.err.println("Ignored: " + e.toString());
		}
	}

	private Object executeScript(String script, Object... arguments) {
		if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class
					.cast(driver);
			return javascriptExecutor.executeScript(script, arguments);
		} else {
			throw new RuntimeException("Script execution failed.");
		}
	}

	private String fullPath(String fileName) {
		return AppTest.class.getClassLoader().getResource(fileName).getPath();
	}

	// http://stackoverflow.com/questions/21093570/force-page-zoom-at-100-with-js
	// http://samples.msdn.microsoft.com/workshop/samples/author/dhtml/refs/zoom.htm#
	// http://jsfiddle.net/5RzJ8/
	// var scale = 'scale(1)';
	// document.body.style.webkitTransform = scale; // Chrome, Opera, Safari
	// document.body.style.msTransform = scale; // IE 9
	// document.body.style.transform = scale; // General
	private void zoomPage() {
		String script = "document.body.style.zoom = (top.window.screen.height - 70) / Math.max(document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight);";
		executeScript(script);
	}

	// NOTE: Sikuli find or scroll is very slow
	private void sikuliFindOrScroll(String image, String description,
			int timeout) {
		int keyCnt = 0;
		boolean foundFlag = false;
		while (!foundFlag) {
			match = screen.exists(image, timeout);
			if (match != null) {
				System.err.println(String.format("Thinks that found %s.", description));
				match.highlight((float) 1.0);
				match.click();
				foundFlag = true;
			} else {
				keyCnt++;
				screen.type(Key.DOWN);
			}
			if (keyCnt > 10) {
				// give up
				throw new RuntimeException(
						String.format("Cannot find %s.", description));
			}
		}
	}
}
