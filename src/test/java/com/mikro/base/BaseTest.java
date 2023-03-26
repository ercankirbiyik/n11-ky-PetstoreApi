package com.mikro.base;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikro.model.ElementInfo;
import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class BaseTest {
    protected static WebDriver driver;
    protected static Actions actions;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    DesiredCapabilities capabilities;
    ChromeOptions chromeOptions;
    FirefoxOptions firefoxOptions;
    EdgeOptions edgeOptions;
    String browserName = "chrome";
    String selectPlatform = "mac";
    String testUrl = "";
    public static String userDir = Paths.get("").toAbsolutePath().toString();
    public static String slash = System.getProperty("file.separator");
    public static final String DEFAULT_DIRECTORY_PATH = "elementValues";
    ConcurrentMap<String, Object> elementMapList = new ConcurrentHashMap<>();

    @BeforeScenario
    public void setUp() {
        logger.info("************************************  BeforeScenario  ************************************");
        try {
            if (StringUtils.isEmpty(System.getenv("key"))) {
                logger.info("Local cihazda " + selectPlatform + " ortaminda " + browserName + " browserinda test ayaga kalkacak");
                if ("mac".equalsIgnoreCase(selectPlatform)) {
                    if ("chrome".equalsIgnoreCase(browserName)) {
                        driver = new ChromeDriver(chromeOptions());
                        // driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                        //driver.get(testUrl);
                        //logger.info(testUrl + " adresi aciliyor!");
                    } else if ("firefox".equalsIgnoreCase(browserName)) {
                        driver = new FirefoxDriver(firefoxOptions());
                        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                        //driver.get(testUrl);
                        //logger.info(testUrl + " adresi aciliyor!");
                    } else if ("edge".equalsIgnoreCase(browserName)) {
                        driver = new EdgeDriver(edgeOptions());
                        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                        //driver.get(testUrl);
                        //logger.info(testUrl + " adresi aciliyor!");
                    }
                } else if ("windows".equalsIgnoreCase(selectPlatform)) {
                    if ("chrome".equalsIgnoreCase(browserName)) {
                        driver = new ChromeDriver(chromeOptions());
                        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                    } else if ("firefox".equalsIgnoreCase(browserName)) {
                        driver = new FirefoxDriver(firefoxOptions());
                        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                    } else if ("edge".equalsIgnoreCase(selectPlatform)) {
                        driver = new EdgeDriver(edgeOptions());
                        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                    }
                    actions = new Actions(driver);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterScenario
    public void tearDown() {
        driver.quit();
    }


    /*
     ****** JSON DOSYALARININ DİRECTORY İÇİNE GÖMÜLEBİLMESİ İÇİN BU METOD İPTAL EDİLDİ HEMEN ALTA YENİSİ YAZILDI. *******

     */
    public void initMap(List<File> fileList) {
        elementMapList = new ConcurrentHashMap<>();
        Type elementType = new TypeToken<List<ElementInfo>>() {
        }.getType();
        Gson gson = new Gson();
        List<ElementInfo> elementInfoList = null;
        for (File file : fileList) {
            try {
                FileReader filez = new FileReader(file);
                elementInfoList = gson
                        .fromJson(new FileReader(file), elementType);
                elementInfoList.parallelStream()
                        .forEach(elementInfo -> elementMapList.put(elementInfo.getKey(), elementInfo));
            } catch (FileNotFoundException e) {

            }
        }
    }

    public static List<File> getFileList(String directoryName) throws IOException {
        List<File> dirList = new ArrayList<>();
        try (Stream<Path> walkStream = Files.walk(Paths.get(directoryName))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                if (f.toString().endsWith(".json")) {
                    // System.out.println(f.toFile().getName() + " json dosyası bulundu.");
                    dirList.add(f.toFile());
                }
            });
        }
        return dirList;
    }


    /**
     * Set Chrome options
     *
     * @return the chrome options
     */
    public ChromeOptions chromeOptions() {
        chromeOptions = new ChromeOptions();
        capabilities = DesiredCapabilities.chrome();
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        chromeOptions.setExperimentalOption("prefs", prefs);
        chromeOptions.addArguments("--ignore-certificate-errors");
        chromeOptions.addArguments("--kiosk");
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--start-fullscreen");
        System.setProperty("webdriver.chrome.driver", "web_driver/chromedriver");
        chromeOptions.merge(capabilities);
        return chromeOptions;
    }

    /**
     * Set Microsoft Edge options
     *
     * @return the edge options
     */
    public EdgeOptions edgeOptions() {
        edgeOptions = new EdgeOptions();
        Map<String, Object> map = new HashMap<>();
        edgeOptions.setCapability("map", map);
        List<String> args = Arrays.asList("--start-fullscreen", "--disable-blink-features=AutomationControlled"
                //,"-inprivate"
                , "--ignore-certificate-errors");
        map.put("args", args);
        edgeOptions.setCapability("ms:edgeOptions", map);
        System.setProperty("webdriver.edge.driver", "web_driver/msedgedriver.exe");
        edgeOptions.merge(capabilities);
        edgeOptions.setCapability("--start-fullscreen", true);
        return edgeOptions;
    }

    /**
     * Set Firefox options
     *
     * @return the firefox options
     */
    public FirefoxOptions firefoxOptions() {
        firefoxOptions = new FirefoxOptions();
        capabilities = DesiredCapabilities.firefox();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        firefoxOptions.addArguments("--kiosk");
        firefoxOptions.addArguments("--disable-notifications");
        firefoxOptions.addArguments("--start-fullscreen");
        FirefoxProfile profile = new FirefoxProfile();
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);
        capabilities.setCapability("marionette", true);
        firefoxOptions.merge(capabilities);
        System.setProperty("webdriver.gecko.driver", "web_driver/geckodriver");
        return firefoxOptions;
    }

    public ElementInfo findElementInfoByKey(String key) {
        return (ElementInfo) elementMapList.get(key);
    }

    public void saveValue(String key, String value) {
        elementMapList.put(key, value);
    }

    public String getValue(String key) {
        return elementMapList.get(key).toString();
    }
}
