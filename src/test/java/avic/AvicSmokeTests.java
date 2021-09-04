package avic;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.xpath;
import static org.testng.Assert.assertEquals;

public class AvicSmokeTests {
    private WebDriver driver;

    @BeforeTest
    public void profileSetUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
    }

    @BeforeMethod
    public void testsSetUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://avic.ua/");
    }

    @Test(priority = 1)
    public void checkChangeLanguageIntoUA() {
        driver.findElement(xpath("//li[@class='has-dropdown']/a")).click();
        driver.findElement(xpath("//li[@class='has-dropdown']/ul[contains(@class, menu-dropdown__lang)]/li/a")).click();

        String actCatalog = driver.findElement(xpath("//span[@class='sidebar-item']")).getAttribute("innerText");
        assertEquals(actCatalog, "Каталог товарів");
    }

    @Test(priority = 2)
    public void checkAutoIncrementFieldInCartAfterClickingPlus() {
        driver.findElement(xpath("//span[@class='sidebar-item']")).click();
        driver.findElement(xpath("//ul[contains(@class,'sidebar-list')]//a[contains(@href, 'apple-store')]")).click();
        driver.findElement(xpath("//div[@class='brand-box__title']/a[contains(@href,'iphone')]")).click();
        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        driver.findElement(xpath("//a[@class='prod-cart__buy' and contains(@data-ecomm-cart, 'iPhone 11 64GB Slim Box Purple')]")).click();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js_cart")));
        driver.findElement(xpath("//span[(contains(@class, 'js_plus'))]")).click();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        String actualProductsCountInCart =
                driver.findElement(xpath("//input[(contains(@class, 'js-change'))]")).getAttribute("value");
        assertEquals(actualProductsCountInCart, "2");
    }

    @Test(priority = 3)
    public void checkCalculationOfTheOrderAmountAfterIncreasingTheQuantityOfGood() {
        driver.findElement(xpath("//span[@class='sidebar-item']")).click();
        driver.findElement(xpath("//ul[contains(@class,'sidebar-list')]//a[contains(@href, 'apple-store')]")).click();
        driver.findElement(xpath("//div[@class='brand-box__title']/a[contains(@href,'iphone')]")).click();
        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        driver.findElement(xpath("//a[@class='prod-cart__buy' and contains(@data-ecomm-cart, 'iPhone 11 64GB Slim Box Purple')]")).click();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js_cart")));

        driver.findElement(xpath("//span[(contains(@class, 'js_plus'))]")).click();
        driver.navigate().refresh();
        int price = Integer.parseInt(driver.findElement(xpath("//div[@class='total-h']/span[@class = 'prise']"))
                .getAttribute("innerText").replace(" грн", ""));

        int totalExpPrice = price * 2;
        int totalActPrice = Integer.parseInt(driver
                .findElement(xpath("//div[@class='item-total']/span[@class = 'prise']"))
                .getAttribute("innerText").replace(" грн", ""));
        assertEquals(totalActPrice, totalExpPrice);

    }

    @Test(priority = 4)
    public void checkDeleteProductFromCart() {
        driver.findElement(xpath("//span[@class='sidebar-item']")).click();
        driver.findElement(xpath("//ul[contains(@class,'sidebar-list')]//a[contains(@href, 'apple-store')]")).click();
        driver.findElement(xpath("//div[@class='brand-box__title']/a[contains(@href,'iphone')]")).click();
        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        driver.findElement(xpath("//a[@class='prod-cart__buy' and contains(@data-ecomm-cart, 'iPhone 11 64GB Slim Box White')]")).click();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js_cart")));

        driver.findElement(xpath("//div[@class = 'item']//i[contains(@class, 'icon-close')]")).click();
        driver.findElement(xpath("//div[contains(@class,'cart-holder')]//a[contains(@class,'orange')]")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("js_cart")));
        String actualProductsCountInCart =
                driver.findElement(xpath("//div[contains(@class,'header-bottom__cart')]//div[contains(@class,'cart_count')]"))
                        .getAttribute("innerText");
        assertEquals(actualProductsCountInCart, "0");

    }

    @AfterMethod
    public void tearDown() {
        driver.close();
    }
}
