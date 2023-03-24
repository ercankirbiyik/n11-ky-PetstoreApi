package com.mikro.step;

import com.mikro.base.BaseTest;
import com.mikro.model.ElementInfo;
import com.thoughtworks.gauge.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BaseSteps extends BaseTest {


    public static int DEFAULT_MAX_ITERATION_COUNT = 1;
    public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 1;

    public BaseSteps() throws IOException {
        String currentWorkingDir = System.getProperty("user.dir");
        initMap(getFileList(currentWorkingDir + "/src"));
        //initMap(getFileList());
    }

    WebElement findElement(String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait webDriverWait = new WebDriverWait(driver, 30);
        WebElement webElement = webDriverWait
                .until(ExpectedConditions.presenceOfElementLocated(infoParam));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                webElement);
        return webElement;
    }

    List<WebElement> findElements(String key) {
        return driver.findElements(getElementInfoToBy(findElementInfoByKey(key)));
    }

    public By getElementInfoToBy(ElementInfo elementInfo) {
        By by = null;
        if (elementInfo.getType().equals("css")) {
            by = By.cssSelector(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("name"))) {
            by = By.name(elementInfo.getValue());
        } else if (elementInfo.getType().equals("id")) {
            by = By.id(elementInfo.getValue());
        } else if (elementInfo.getType().equals("xpath")) {
            by = By.xpath(elementInfo.getValue());
        } else if (elementInfo.getType().equals("linkText")) {
            by = By.linkText(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("partialLinkText"))) {
            by = By.partialLinkText(elementInfo.getValue());
        }
        return by;
    }

    private void clickElement(WebElement element) {
        element.click();
    }

    public WebElement findElementWithKey(String key) {
        return findElement(key);
    }

    //Javascript driverın başlatılması
    private JavascriptExecutor getJSExecutor() {
        return (JavascriptExecutor) driver;
    }

    //Javascript scriptlerinin çalışması için gerekli fonksiyon
    private Object executeJS(String script, boolean wait) {
        return wait ? getJSExecutor().executeScript(script, "") : getJSExecutor().executeAsyncScript(script, "");
    }

    private void scrollTo(int x, int y) {
        String script = String.format("window.scrollTo(%d, %d);", x, y);
        executeJS(script, true);
    }

    private void hoverElement(WebElement element) {
        Actions actions1 = new Actions(driver);
        actions1.moveToElement(element).perform();
    }

    public void javascriptclicker(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }


    @Step({"Go to <url> address",
            "<url> adresine git"})
    public void goToUrl(String url) {
        driver.get(url);
        logger.info(url + " adresine gidiliyor.");
    }

    @Step({"Wait <value> seconds",
            "<int> saniye bekle"})
    public void waitBySeconds(int seconds) {
        try {
            logger.info(seconds + " saniye bekleniyor.");
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step({"Wait <value> milliseconds",
            "<long> milisaniye bekle"})
    public void waitByMilliSeconds(long milliseconds) {
        try {
            logger.info(milliseconds + " milisaniye bekleniyor.");
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step({"Check if element <key> exists",
            "Element var mı kontrol et <key>"})
    public WebElement getElementWithKeyIfExists(String key) {
        WebElement webElement;
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            try {
                webElement = findElementWithKey(key);
                logger.info(key + " elementi bulundu.");
                return webElement;
            } catch (WebDriverException e) {
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        assertFalse(Boolean.parseBoolean("Element: '" + key + "' bulanamadı"));
        return null;
    }

    @Step({"Check if element <key> exists else print message <message>",
            "Element <key> var mı kontrol et yoksa hata mesajı ver <message>"})
    public void getElementWithKeyIfExistsWithMessage(String key, String message) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        By by = getElementInfoToBy(elementInfo);

        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(by).size() > 0) {
                logger.info(key + " elementi bulundu.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail(message);
    }

    @Step({"Click to element <key>",
            "Elementine tıkla <key>"})
    public void clickElement(String key) {
        if (!key.isEmpty()) {
            //hoverElement(findElement(key));
            clickElement(findElement(key));
            logger.info(key + " elementine tıklandı.");
        }
    }

    @Step({"Wait for element then click <key>",
            "Elementi bekle ve sonra tıkla <key>"})
    public void checkElementExistsThenClick(String key) {
        getElementWithKeyIfExists(key);
        clickElement(key);
        logger.info(key + " elementine tıklandı.");
    }

    @Step({"Send ENTER key to element <key>",
            "Elemente ENTER keyi yolla <key>"})
    public void sendKeyToElementENTER(String key) {
        findElement(key).sendKeys(Keys.ENTER);
        logger.info(key + " elementine ENTER keyi yollandı.");
    }

    @Step({"Write value <text> to element <key>",
            "<text> textini <key> elementine yaz"})
    public void ssendKeys(String text, String key) {
        if (!key.equals("")) {
            findElement(key).sendKeys(text);
            logger.info(key + " elementine " + text + " texti yazıldı.");
        }
    }

    @Step({"Check if current URL contains the value <expectedURL>",
            "Şu anki URL <url> değerini içeriyor mu kontrol et"})
    public void checkURLContainsRepeat(String expectedURL) {
        int loopCount = 0;
        String actualURL = "";
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            actualURL = driver.getCurrentUrl();

            if (actualURL != null && actualURL.contains(expectedURL)) {
                logger.info("Şuanki URL" + expectedURL + " değerini içeriyor.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail(
                "Actual URL doesn't match the expected." + "Expected: " + expectedURL + ", Actual: "
                        + actualURL);
    }

    @Step({"Check if element <key> contains text <expectedText>",
            "<key> elementi <text> değerini içeriyor mu kontrol et"})
    public void checkElementContainsText(String key, String expectedText) {
        Boolean containsText = findElement(key).getText().contains(expectedText);
        logger.info("Beklenen text: " + expectedText);
        logger.info("Gerçek text: " + containsText);
        assertTrue(containsText, "Expected text is not contained!!  " +
                "  Beklenen text ile gerçek text aynı değil!!");
        logger.info(key + " elementi " + "'"+ expectedText + "'" + " degerini iceriyor.");
    }

    @Step({"Check if element <key> not contains text <expectedText>",
            "<key> elementi <text> değerini içermiyor mu kontrol et"})
    public void checkElementNotContainsText(String key, String expectedText) {
        Boolean containsText = findElement(key).getText().contains(expectedText);
        logger.info("Beklenmeyen text: " + expectedText);
        logger.info("Gerçek text: " + containsText);
        assertFalse(containsText, "Expected text is contained!!  " +
                "  Beklenen text ile gerçek text aynı!!!");
        logger.info(key + " elementi " + expectedText + " degerini icermiyor.");
    }

    @Step({"<key> Elementine kadar kaydır",
            "Scroll to Element <key>"})
    public WebElement scrollToElementToBeVisible(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        WebElement webElement = driver.findElement(getElementInfoToBy(elementInfo));
        if (webElement != null) {
            scrollTo(webElement.getLocation().getX(), webElement.getLocation().getY() - 100);
            logger.info("Sayfa '" + key + "' elementine kadar kaydırıldı.");
        }
        return webElement;
    }


    @Step({"<key> elementinin value degeri <expectedValue> degerini iceriyor mu kontrol et"})
    public void checkElementAttributeWithJs(String key, String expectedValue) {
        WebElement webElement = findElement(key);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object objectValue = js.executeScript("return arguments[0].value", webElement);
        assertNotNull(objectValue);
        logger.info(key + " elementinin value degeri " + expectedValue + " degerini iceriyor");
        String value = objectValue.toString();
    }

    @Step("<key> elementin üstünde bekle")
    public void hover(String key) {
        hoverElement(findElement(key));
        logger.info(key + " elementinin üzerinde beklendi.");
    }

    @Step("<key> elementine javascript ile tıkla")
    public void clickToElementWithJavaScript(String key) {
        WebElement element = findElement(key);
        javascriptclicker(element);
        logger.info(key + " elementine javascript ile tıklandı");
    }
}
