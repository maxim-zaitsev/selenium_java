package com.mycompany.app;

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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.openqa.selenium.By.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.seleniumhq.selenium.fluent.*;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.fail;

public class SuvianTest {

	private WebDriver driver;
	private WebDriverWait wait;
	private static Actions actions;
	private static Alert alert;
	private Predicate<WebElement> hasClass;
	private Predicate<WebElement> hasAttr;
	private Predicate<WebElement> hasText;
	private int flexibleWait = 5;
	private int implicitWait = 1;
	private long pollingInterval = 500;
	private String baseUrl = "http://suvian.in/selenium";
	private static String getStyleScript;

	@BeforeSuite
	public void beforeSuiteMethod() throws Exception {
		// driver = new FirefoxDriver();
		System.setProperty("webdriver.chrome.driver",
				"c:/java/selenium/chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, flexibleWait);
		wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
		actions = new Actions(driver);
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

	// the Test0 contains extra debugging in the ExpectedConditions anonimous
	// class and filter
	// this test is somewhat redundant, used for debugging
	// Firebug console validation:
	// $x("<xpath>")
	// $$("<cssSelector>")
	@Test(enabled = false)
	public void test0() {

		// Arrange
		driver.get("http://suvian.in/selenium/1.1link.html");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		String cssSelector = ".container .row .intro-message h3 a";
		String xpath = "//div[1]/div/div/div/div/h3[2]/a";
		wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector(cssSelector))));
		List<WebElement> elements = driver.findElements(By.xpath(xpath));
		assertTrue(elements.size() > 0);

		// Act
		String linkText = "Click Here";
		WebElement element = elements.get(0);
		highlight(element);
		assertTrue(element.getText().equalsIgnoreCase(linkText), element.getText());
		element.click();

		linkText = "Link Successfully clicked";
		cssSelector = ".container .row .intro-message h3";

		// Wait page to load
		try {
			wait.until(ExpectedConditions.urlContains("1.1link_validate.html"));
		} catch (UnreachableBrowserException e) {
		}

		// Assert
		// 1. Expected Condition uses enclosing element
		try {
			(new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver d) {
					String t = d.findElement(By.className("intro-message")).getText();
					Boolean result = t.contains("Link Successfully clicked");
					System.err.println(
							"in apply: Text = " + t + "\n result = " + result.toString());
					return result;
				}
			});
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
		}
		// 2. Expected condition with Iterator, uses String methods
		try {
			(new WebDriverWait(driver, 5)).until(new ExpectedCondition<WebElement>() {

				@Override
				public WebElement apply(WebDriver d) {
					Iterator<WebElement> elementsIterator = d
							.findElements(
									By.cssSelector("div.container div.row div.intro-message h3"))
							.iterator();
					WebElement result = null;
					while (elementsIterator.hasNext()) {
						WebElement e = (WebElement) elementsIterator.next();
						String t = e.getText();
						System.err.println("in apply iterator (1): Text = " + t);
						if (t.contains("Navigate Back")) {
							result = e;
							break;
						}
					}
					return result;
				}
			});
		} catch (

		Exception e) {
			System.err.println("Exception: " + e.toString());
		}

		// 2. Alternative Iterator, uses Regex methods
		try {
			WebElement checkElement = (new WebDriverWait(driver, 5))
					.until(new ExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(WebDriver d) {
							Iterator<WebElement> i = d.findElements(
									By.cssSelector("div.container div.row div.intro-message h3"))
									.iterator();
							WebElement result = null;
							// "(?:" + "Navigate Back" + ")"
							Pattern pattern = Pattern.compile("Navigate Back",
									Pattern.CASE_INSENSITIVE);
							while (i.hasNext()) {
								WebElement e = (WebElement) i.next();
								String t = e.getText();
								System.err.println("in apply iterator (2): Text = " + t);
								Matcher matcher = pattern.matcher(t);
								if (matcher.find()) {
									result = e;
									break;
								}
							}
							return result;
						}
					});
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
		}

		// 3. Alternative wait, functional style, with Optional <WebElement>
		// http://www.nurkiewicz.com/2013/08/optional-in-java-8-cheat-sheet.html
		try {
			WebElement checkElement = (new WebDriverWait(driver, 5))
					.until(new ExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(WebDriver d) {
							Optional<WebElement> e = d
									.findElements(By.cssSelector(
											"div.container div.row div.intro-message h3"))
									.stream().filter(o -> {
										String t = o.getText();
										System.err.println("in stream filter (3): Text = " + t);
										return (Boolean) (t.contains("Navigate Back"));
									}).findFirst();
							return (e.isPresent()) ? e.get() : (WebElement) null;
						}
					});
			System.err
					.println("element check: " + checkElement.getAttribute("innerHTML"));
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
		}

		// http://stackoverflow.com/questions/12858972/how-can-i-ask-the-selenium-webdriver-to-wait-for-few-seconds-in-java
		// http://stackoverflow.com/questions/31102351/selenium-java-lambda-implementation-for-explicit-waits
		elements = driver.findElements(By.cssSelector(cssSelector));
		// longer version
		Stream<WebElement> elementsStream = elements.stream();
		elements = elementsStream.filter(o -> {
			System.err.println("in filter: Text = " + o.getText());
			return (Boolean) (o.getText()
					.equalsIgnoreCase("Link Successfully clicked"));
		}).collect(Collectors.toList());

		// shorter version
		elements = driver.findElements(By.cssSelector(cssSelector)).stream()
				.filter(o -> "Link Successfully clicked".equalsIgnoreCase(o.getText()))
				.collect(Collectors.toList());
		assertThat(elements.size(), equalTo(1));

		elements = driver.findElements(By.cssSelector(cssSelector)).stream()
				.filter(o -> o.getText().equalsIgnoreCase("Link Successfully clicked"))
				.collect(Collectors.toList());
		assertThat(elements.size(), equalTo(1));

		element = elements.get(0);
		highlight(element);
		assertTrue(element.getText().equalsIgnoreCase(linkText), element.getText());
	}

	@Test(enabled = false)
	public void test1() {

		// Arrange
		driver.get("http://suvian.in/selenium/1.1link.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));
		List<WebElement> elements = driver
				.findElements(By.xpath("//div[1]/div/div/div/div/h3[2]/a"));
		assertTrue(elements.size() > 0);

		// Act
		WebElement element = elements.get(0);
		highlight(element);
		assertTrue(element.getText().equalsIgnoreCase("Click Here"),
				element.getText());
		element.click();

		// Wait page to load
		try {
			wait.until(ExpectedConditions.urlContains("1.1link_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Inspect enclosing element to confirm the page loaded
		try {
			(new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver d) {
					return (Boolean) d.findElement(By.className("intro-message"))
							.getText().contains("Link Successfully clicked");
				}
			});
		} catch (Exception e) {
		}

		// Assert
		elements = driver
				.findElements(By.cssSelector(".container .row .intro-message h3"))
				.stream()
				.filter(o -> o.getText().equalsIgnoreCase("Link Successfully clicked"))
				.collect(Collectors.toList());
		assertThat(elements.size(), equalTo(1));
		element = elements.get(0);
		highlight(element);
		assertTrue(element.getText().equalsIgnoreCase("Link Successfully clicked"),
				element.getText());
		elements.forEach(System.out::println); // output : ??
	}

	@Test(enabled = false)
	public void test2() {
		// Arrange
		driver.get("http://suvian.in/selenium/1.2text_field.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(ExpectedConditions.urlContains("1.2text_field_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test3() {
		// Arrange
		driver.get("http://suvian.in/selenium/1.3age_plceholder.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(
					ExpectedConditions.urlContains("1.3age_plceholder_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test4() {
		// Arrange
		driver.get("http://suvian.in/selenium/1.4gender_dropdown.html");
		// Wait page to load
		wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
						".intro-header .container .row .col-lg-12 .intro-message select"))));
		// Act
		WebElement element = driver.findElement(By.cssSelector(
				".intro-header .container .row .col-lg-12 .intro-message select"));
		Select select = new Select(element);
		String optionString = "Male";
		// create predicate driven case-insensitive option search
		hasText = o -> o.getText()
				.matches("(?i:" + optionString.toLowerCase() + ")");
		List<WebElement> options = select.getOptions().stream().filter(hasText)
				.collect(Collectors.toList());
		// Assert
		assertThat(options.size(), equalTo(1));
		element = options.get(0);
		assertTrue(element.getText().equals(optionString), element.getText());
	}

	@Test(enabled = false)
	public void test5_1() {
		// Arrange
		driver.get("http://suvian.in/selenium/1.5married_radio.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		String label = "yes";
		String elementContents = driver
				.findElement(By.xpath(
						"//div[@class='intro-header']/div[@class='container']/div[@class='row']/div[@class='col-lg-12']/div[@class='intro-message']/form"))
				.getAttribute("outerHTML");

		String line = new ArrayList<String>(
				Arrays.asList(elementContents.split("<br/?>"))).stream()
						.filter(o -> o.toLowerCase().indexOf(label) > -1).findFirst().get();
		Matcher matcher = Pattern.compile("value=\\\"([^\"]*)\\\"").matcher(line);
		String checkboxValue = null;
		if (matcher.find()) {
			checkboxValue = matcher.group(1);
			System.err.println("checkox value = " + checkboxValue);
		} else {
			System.err.println("checkox value not found");
		}
		WebElement checkBoxElement = null;
		if (checkboxValue != null) {
			checkBoxElement = driver.findElement(By.xpath(String.format(
					"//div[@class='intro-header']/div[@class='container']/div[@class='row']/div[@class='col-lg-12']/div[@class='intro-message']/form/input[@name='married'][@value='%s']",
					checkboxValue)));
		}
		// Act
		assertThat(checkBoxElement, notNullValue());
		highlight(checkBoxElement);
		checkBoxElement.sendKeys(Keys.SPACE);
		// Assert
		// NOTE: behaves differently in C#
		assertTrue(checkBoxElement.isSelected());
	}

	@Test(enabled = false)
	public void test5_2() {
		// Arrange
		driver.get("http://suvian.in/selenium/1.5married_radio.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		String label = "no";
		WebElement formElement = driver.findElement(By.cssSelector(
				".intro-header .container .row .col-lg-12 .intro-message form"));
		String elementContents = formElement.getAttribute("outerHTML");

		String line = new ArrayList<String>(
				Arrays.asList(elementContents.split("<br/?>"))).stream()
						.filter(o -> o.toLowerCase().indexOf(label) > -1).findFirst().get();
		Matcher matcher = Pattern.compile("value=\\\"([^\"]*)\\\"").matcher(line);
		String checkboxValue = null;
		if (matcher.find()) {
			checkboxValue = matcher.group(1);
			System.err.println("checkox value = " + checkboxValue);
		} else {
			System.err.println("checkox value not found");
		}
		WebElement checkBoxElement = null;
		if (checkboxValue != null) {
			checkBoxElement = formElement.findElement(By.cssSelector(
					String.format("input[name='married'][value='%s']", checkboxValue)));
		}
		// Act
		assertThat(checkBoxElement, notNullValue());
		highlight(checkBoxElement);
		checkBoxElement.click();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// ignore
		}
		// Assert
		assertTrue(checkBoxElement.isSelected());
	}

	@Test(enabled = false)
	public void test6_1() {
		// Arrange
		ArrayList<String> hobbies = new ArrayList<String>(
				Arrays.asList("Singing", "Dancing"));
		driver.get("http://suvian.in/selenium/1.6checkbox.html");
		try {
			WebElement checkElement = (new WebDriverWait(driver, 5))
					.until(new ExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(WebDriver d) {
							return d
									.findElements(By.cssSelector(
											"div.container div.row div.intro-message h3"))
									.stream().filter(o -> o.getText().toLowerCase()
											.indexOf("select your hobbies") > -1)
									.findFirst().get();
						}
					});
			System.err
					.println("element check: " + checkElement.getAttribute("innerHTML"));
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
		}

		// Act
		WebElement formElement = driver.findElement(By.cssSelector("input[id]"))
				.findElement(By.xpath(".."));
		assertThat(formElement, notNullValue());
		highlight(formElement);
		List<WebElement> inputElements = formElement
				.findElements(By.cssSelector("label[for]")).stream()
				.filter(o -> hobbies.contains(o.getText()))
				.collect(Collectors.toList());
		Map<String, String> inputIds = inputElements.stream().collect(
				Collectors.toMap(o -> o.getText(), o -> o.getAttribute("for")));
		ArrayList<WebElement> checkboxes = new ArrayList<WebElement>();
		for (String hobby : hobbies) {
			try {
				System.err.println("finding: " + inputIds.get(hobby));
				checkboxes.add(formElement.findElement(
						// will throw exception
						By.cssSelector(String.format("input#%s", inputIds.get(hobby)))));
			} catch (InvalidSelectorException e) {
				System.err.println("ignored: " + e.toString());
			}
			try {
				checkboxes.add(formElement.findElement(
						// will not throw exception
						By.xpath(String.format("input[@id='%s']", inputIds.get(hobby)))));
			} catch (Exception e) {
				System.err.println("ignored: " + e.toString());
			}

		}
		checkboxes.stream().forEach(o ->

		{
			highlight(o);
			o.click();
		});

		// Assert

		assertTrue(
				formElement.findElements(By.cssSelector("input[type='checkbox']"))
						.stream().filter(o -> o.isSelected()).count() == hobbies.size());
	}

	// NOTE: this test is broken
	// label follows the check box therefore
	// the following-sibling to find the check box by its label does not seem to
	// be appropriate
	// - see the test6_3 for the solution
	// however preceding-sibling always finds the check box #1
	@Test(enabled = false)
	public void test6_2() {
		// Arrange
		ArrayList<String> hobbies = new ArrayList<String>(
				Arrays.asList("Singing", "Dancing", "Sports"));
		driver.get("http://suvian.in/selenium/1.6checkbox.html");
		WebElement checkElement = (new WebDriverWait(driver, 5))
				.until(new ExpectedCondition<WebElement>() {
					@Override
					public WebElement apply(WebDriver d) {
						return d
								.findElements(By
										.cssSelector("div.container div.row div.intro-message h3"))
								.stream().filter(o -> o.getText().toLowerCase()
										.indexOf("select your hobbies") > -1)
								.findFirst().get();
					}
				});
		assertThat(checkElement, notNullValue());
		// Act
		List<WebElement> elements = checkElement
				.findElements(By.xpath("..//label[@for]")).stream()
				.filter(o -> hobbies.contains(o.getText()))
				.collect(Collectors.toList());
		assertTrue(elements.size() > 0);
		List<WebElement> checkBoxes = elements.stream()
				.map(o -> o.findElement(By.xpath("preceding-sibling::input")))
				.collect(Collectors.toList());
		assertTrue(checkBoxes.size() > 0);

		checkBoxes.stream().forEach(o -> {
			highlight(o);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
			o.click();
		});
		// Assert
		assertTrue(
				driver
						.findElements(By.cssSelector(
								".container .intro-message input[type='checkbox']"))
						.stream().filter(o -> o.isSelected()).count() == hobbies.size());
	}

	// reverse usage of following-sibling to locate check box by its label
	@Test(enabled = false)
	public void test6_3() {
		// Arrange
		ArrayList<String> hobbies = new ArrayList<String>(
				Arrays.asList("Singing", "Dancing", "Sports"));
		driver.get("http://suvian.in/selenium/1.6checkbox.html");
		WebElement checkElement = null;
		try {
			checkElement = (new WebDriverWait(driver, 5))
					.until(new ExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(WebDriver d) {
							return d
									.findElements(By.cssSelector(
											"div.container div.row div.intro-message h3"))
									.stream().filter(o -> o.getText().toLowerCase()
											.indexOf("select your hobbies") > -1)
									.findFirst().get();
						}
					});
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
		}
		assertThat(checkElement, notNullValue());
		// Act
		List<WebElement> checkBoxes = checkElement
				.findElements(By.xpath("..//input[@type = 'checkbox']")).stream()
				.filter(o -> {
					WebElement label = o
							.findElement(By.xpath("following-sibling::label"));
					if (hobbies.contains(label.getText())) {
						System.err.println(String.format("checkbox element %s: '%s'",
								o.getAttribute("id"), label.getText()));
						return true;
					} else {
						return false;
					}
				}).collect(Collectors.toList());
		assertTrue(checkBoxes.size() > 0);
		checkBoxes.stream().forEach(o -> {
			highlight(o);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
			o.click();
		});
		// Assert
		assertTrue(
				driver
						.findElements(By.cssSelector(
								".container .intro-message input[type='checkbox']"))
						.stream().filter(o -> o.isSelected()).count() == hobbies.size());
	}

	@Test(enabled = false)
	public void test7() {
		// Arrange
		driver.get("http://suvian.in/selenium/1.7button.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(ExpectedConditions.urlContains("1.7button_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test8() {
		// Arrange
		driver.get("http://suvian.in/selenium/1.8copyAndPasteText.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(
					ExpectedConditions.urlContains("1.8copyAndPasteText_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test9() {
		// Arrange
		driver.get("http://suvian.in/selenium/1.9copyAndPasteTextAdvanced.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(ExpectedConditions
					.urlContains("1.9copyAndPasteTextAdvanced_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test10() {
		// Arrange
		driver.get("http://suvian.in/selenium/1.10selectElementFromDD.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(ExpectedConditions
					.urlContains("1.10selectElementFromDD_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test11() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.1alert.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(ExpectedConditions.urlContains("2.1alert_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test12() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.2browserPopUp.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(
					ExpectedConditions.urlContains("2.2browserPopUp_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test13_1() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.3frame.html");

		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
				By.cssSelector(".container .row .intro-message iframe")));

		// Act
		WebElement buttonElement = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector("h3 button"))));

		assertThat(buttonElement, notNullValue());
		buttonElement.click();
		// Assert
		try {
			// confirm alert
			driver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// Alert not present - ignore
		} catch (WebDriverException e) {
			System.err
					.println("Alert was not handled : " + e.getStackTrace().toString());
			return;
		}
	}

	@Test(enabled = false)
	public void test13_2() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.3frame.html");

		WebElement frameElement = wait
				.until(ExpectedConditions.visibilityOfElementLocated(
						By.cssSelector(".container .row .intro-message iframe")));
		assertThat(frameElement, notNullValue());
		driver.switchTo().frame(frameElement);
		// Act
		WebElement buttonElement = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.cssSelector("h3 button"))));

		assertThat(buttonElement, notNullValue());
		buttonElement.click();
		// Assert
		try {
			// confirm alert
			driver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// Alert not present - ignore
		} catch (WebDriverException e) {
			System.err
					.println("Alert was not handled : " + e.getStackTrace().toString());
			return;
		}
	}

	@Test(enabled = false)
	public void test14_1() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.4mouseHover.html");

		WebElement elementWithTooltip = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(
						By.cssSelector(".container .row .intro-message h3 a"))));
		assertThat(elementWithTooltip, notNullValue());
		// the link title attribute is present regardless of tooltip shown or not
		// Assert
		assertThat(elementWithTooltip.getAttribute("title"),
				equalTo("This is a tooltip."));
		// Act
		actions.moveToElement(elementWithTooltip).build().perform();
		// Assert
		assertThat(elementWithTooltip.getAttribute("title"),
				equalTo("This is a tooltip."));
		// How to discover whether core HTML tooltip is displayed ?

	}

	// http://sqa.stackexchange.com/questions/14247/how-can-i-get-the-value-of-the-tooltip
	@Test(enabled = false)
	public void test14_2() {
		// Arrange
		driver.get("http://yuilibrary.com/yui/docs/charts/charts-pie.html");

		WebElement elementWithTooltip = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
						"//*[contains(@class,'yui3-svgSvgPieSlice')][@fill = '#66007f']"))));
		assertThat(elementWithTooltip, notNullValue());

		WebElement chartTitle = driver
				.findElement(By.cssSelector("div#doc div.content h1"));
		// Act
		actions.moveToElement(chartTitle).build().perform();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		// Assert
		// the tooltip element is present on the page regardless of mouse position,
		// but is not visible.
		assertThat((int) driver
				.findElements(By.cssSelector("div[class $= 'chart-tooltip']")).stream()
				.filter(o -> {
					return (Boolean) (o.getAttribute("style")
							.indexOf("visibility: hidden;") == -1);
				}).count(), is(0));

		// Act
		actions.moveToElement(elementWithTooltip).build().perform();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		// Assert
		List<WebElement> tooltips = driver
				.findElements(By.xpath("//div[contains(@id, '_tooltip')]"));
		assertThat(tooltips.size(), is(1));
		assertThat(tooltips.get(0).getText(), containsString("day: Monday"));
	}

	@Test(enabled = false)
	public void test15() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.5resize.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(ExpectedConditions.urlContains("2.5resize_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test16_1() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.6liCount.html");

		WebElement book2 = driver.findElement(By.xpath(
				"//div[@class='intro-message']/ul/li[ contains(text(), 'Book 2')]"));
		assertThat(book2.getText(), containsString("Book 2"));

		// Act
		int count = 0;
		Enumeration<WebElement> chapters = Collections
				.enumeration(book2.findElements(By.cssSelector("ul li")));

		while (chapters.hasMoreElements()) {
			WebElement currentChapter = chapters.nextElement();
			count++;
			try {
				highlight(currentChapter);
				currentChapter.click();
			} catch (WebDriverException e) {
				System.err
						.println(String.format("Exception (ignored):\n%s", e.toString()));
				// Element is not clickable
			}
		}
		// Assert
		assertThat(count, is(7));
		System.err.println(count);

		WebElement resultElement = driver
				.findElement(By.cssSelector("input#chapbook2"));
		resultElement.clear();
		resultElement.sendKeys(String.format("%d", count));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	@Test(enabled = false)
	public void test16_2() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.6liCount.html");
		// Act

		List<WebElement> elements = driver
				.findElements(
						By.cssSelector(".container .row .intro-message ul#books li"))
				.stream().filter(o -> {
					return (Boolean) (o.getText().indexOf("Book") > -1);
				}).map(o -> o.findElements(By.cssSelector("ul li")))
				.collect(ArrayList::new, List::addAll, List::addAll);
		// http://stackoverflow.com/questions/25147094/turn-a-list-of-lists-into-a-list-using-lambdas

		// Assert
		assertThat(elements.size(), is(15));
		System.err.println("Total:" + elements.size());
		WebElement resultElement = driver
				.findElement(By.cssSelector("input#chapall"));
		resultElement.clear();
		resultElement.sendKeys(String.format("%d", elements.size()));
	}

	@Test(enabled = false)
	public void test16_3() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.6liCount.html");

		// Act

		driver
				.findElements(
						By.cssSelector(".container .row .intro-message ul#books li"))
				.stream().filter(o -> {
					CharSequence text = "Book";
					return (Boolean) !(o.getText().contains(text));
				}).forEach(o -> {
					try {
						highlight(o);
						o.click();
					} catch (WebDriverException e) {
						System.err.println(
								String.format("Exception (ignored):\n%s", e.toString()));
						// Element is not clickable
					}
				});
	}

	@Test(enabled = false)
	public void test17() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.7waitUntil.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(ExpectedConditions.urlContains("2.7waitUntil_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test18() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.8progressBar.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(
					ExpectedConditions.urlContains("2.8progressBar_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test19_1() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.9greenColorBlock.html");

		WebElement greenBoxElement = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
						".container .row .intro-message table div.greenbox"))));
		// Act

		// Assert
		assertThat(greenBoxElement, notNullValue());
		// TODO: computed style of that element

		actions.moveToElement(greenBoxElement).build().perform();
		greenBoxElement.click();
		try {
			// confirm alert
			alert = driver.switchTo().alert();
			String alert_text = alert.getText();
			assertThat(alert_text, containsString("You clicked on Green"));

			alert.accept();
		} catch (NoAlertPresentException e) {
			// Alert not present - ignore
		} catch (WebDriverException e) {
			System.err
					.println("Alert was not handled : " + e.getStackTrace().toString());
			return;
		}

	}

	@Test(enabled = false)
	public void test19_2() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.9greenColorBlock.html");

		WebElement greenBoxElement = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
						".container .row .intro-message table div.greenbox"))));
		// Act

		// Assert
		assertThat(greenBoxElement, notNullValue());
		// simlified scipt for computed style of that element
		String script = "var element = arguments[0]; window.document.defaultView.getComputedStyle(element, null).getPropertyValue('background-color');";

		Object computedStyle = ((JavascriptExecutor) driver).executeScript(script,
				greenBoxElement);

		// System.err.println("Red Box background color: " +
		// computedStyle.toString());

		String style = styleOfElement(greenBoxElement);
		System.err.println("style:\n" + style);

		String backgroundColorAttribute = styleOfElement(greenBoxElement,
				"background-color");
		String heightAttribute = styleOfElement(greenBoxElement, "height");
		String widthAttribute = styleOfElement(greenBoxElement, "width");

		System.err
				.println("backgroundColorAttribute:\n" + backgroundColorAttribute);

		Pattern pattern = Pattern.compile("\\(\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\)");
		Matcher matcher = pattern.matcher(backgroundColorAttribute);
		int red = 0, green = 0, blue = 0;

		if (matcher.find()) {
			red = Integer.parseInt(matcher.group(1).toString());
			green = Integer.parseInt(matcher.group(2).toString());
			blue = Integer.parseInt(matcher.group(3).toString());
			assertTrue(red == 0);
		}
		pattern = Pattern
				.compile("\\(\\s*(?<red>\\d+),\\s*(?<green>\\d+),\\s*(?<blue>\\d+)\\)");
		matcher = pattern.matcher(backgroundColorAttribute);
		if (matcher.find()) {
			red = Integer.parseInt(matcher.group("red").toString());
			green = Integer.parseInt(matcher.group("green").toString());
			blue = Integer.parseInt(matcher.group("blue").toString());
			assertTrue(green >= 128);
		}
		System.err.println("green:" + green);

		System.err.println("heightAttribute:\n" + heightAttribute);
		System.err.println("widthAttribute:\n" + widthAttribute);
		// assertThat(greenBoxElement.getAttribute("background-color"),
		// equalTo("red"));

	}

	@Test(enabled = false)
	public void test20() {
		// Arrange
		driver.get("http://suvian.in/selenium/2.10dragAndDrop.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(
					ExpectedConditions.urlContains("2.10dragAndDrop_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test21() {
		// Arrange
		driver.get("http://suvian.in/selenium/3.1fileupload.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(ExpectedConditions.urlContains("3.1fileupload_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test22() {
		// Arrange
		driver.get("http://suvian.in/selenium/3.2dragAndDrop.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(
					ExpectedConditions.urlContains("3.2dragAndDrop_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test23() {
		// Arrange
		driver.get("http://suvian.in/selenium/3.3javaemail.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(ExpectedConditions.urlContains("3.3javaemail_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test24() {
		// Arrange
		driver.get("http://suvian.in/selenium/3.4readWriteExcel.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(
					ExpectedConditions.urlContains("3.4readWriteExcel_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test25() {
		// Arrange
		driver.get("http://suvian.in/selenium/3.5cricketScorecard.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(
					ExpectedConditions.urlContains("3.5cricketScorecard_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = true)
	public void test26() {
		// Arrange
		driver.get("http://suvian.in/selenium/3.6copyTextFromTextField.html");
		WebElement sourceElement = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(
						".container .row .intro-message form textarea[name='field_one']"))));
		assertThat(sourceElement, notNullValue());
		WebElement targetElement = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
						"//div[@class = 'container']//div[@class = 'row']//div[@class = 'intro-message']//form/textarea[@name='field_two']"))));
		assertThat(targetElement, notNullValue());

		// Act
		String text = sourceElement.getText();
		executeScript("arguments[0].removeAttribute('readonly');", sourceElement);
		sourceElement.clear();
		targetElement.sendKeys((CharSequence) text);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		// Assert
		String textValue = executeScript("return arguments[0].value;",
				targetElement).toString();
		assertThat(textValue, containsString(text));
		System.err.println("value = " + textValue);

		assertThat(targetElement.getAttribute("value"), containsString(text));
		// Assert
		try {
			assertThat(targetElement.getText(), containsString(text));
		} catch (AssertionError e) {
			System.err.println("Exception(ignoredd): " + e.toString());
			// new textArea value is not possible to retrieve with getText()
		}
	}

	@Test(enabled = false)
	public void test27() {
		// Arrange
		driver.get("http://suvian.in/selenium/3.7correspondingRadio.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(ExpectedConditions
					.urlContains("3.7correspondingRadio_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test28() {
		// Arrange
		driver.get("http://suvian.in/selenium/3.8screeshotToEmail.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(
					ExpectedConditions.urlContains("3.8screeshotToEmail_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test29() {
		// Arrange
		driver.get("http://suvian.in/selenium/3.9FacebookTest.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(
					ExpectedConditions.urlContains("3.9FacebookTest_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
	}

	@Test(enabled = false)
	public void test30() {
		// Arrange
		driver.get("http://suvian.in/selenium/3.10select1stFriday.html");

		wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector(".container .row .intro-message h3 a"))));

		// Act

		// Wait page to load
		try {
			wait.until(
					ExpectedConditions.urlContains("3.10select1stFriday_validate.html"));
		} catch (UnreachableBrowserException e) {
		}
		// Assert
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

	private String styleOfElement(WebElement element, Object... arguments) {
		String getStyleScript = getScriptContent("getStyle.js");

		return (String) executeScript(getStyleScript, element, arguments);
	}

	protected static String getScriptContent(String scriptName) {
		try {
			final InputStream stream = SuvianTest.class.getClassLoader()
					.getResourceAsStream(scriptName);
			final byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			return new String(bytes, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(scriptName);
		}
	}
}
