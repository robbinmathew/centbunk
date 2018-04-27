package bronz.accounting.bunk;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BunkWebScanner {

    private String custId;
    private String pass;
    private String url;
    private String driverPath;

    public BunkWebScanner(String custId, String pass, String url, String driverPath){
        this.custId = custId;
        this.pass = pass;
        this.url = url;
        this.driverPath = driverPath;
    }

    private WebDriver getDriver() {
        System.setProperty("webdriver.chrome.driver", driverPath);
        ChromeOptions chromeOptions = new ChromeOptions();
        //chromeOptions.setBinary("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
        chromeOptions.addArguments("--headless");

        return new ChromeDriver(chromeOptions);
    }

    public void scanNewLoads() throws BunkMgmtException {
        WebDriver Driver = null;
        try {
            Driver = getDriver();
            Driver.navigate().to(url);

            Driver.findElement(By.id("custid")).sendKeys(custId);
            Driver.findElement(By.id("password")).sendKeys(pass);
            Driver.findElement(By.id("submit")).click();

            WebDriverWait wait = new WebDriverWait(Driver,60);
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("middle"));

            Driver.findElement(By.linkText("TRANSACTIONS")).click();
            //WebDriverWait wait1 = new WebDriverWait(Driver,10);
            //wait1.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("middle"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("txtfrmday")));
//            System.out.print(Driver.getPageSource());

            Driver.findElement(By.name("txtfrmday")).clear();
            Driver.findElement(By.name("txtfrmday")).sendKeys("01");
            Driver.findElement(By.name("txtfrmday")).submit();
            //Driver.findElement(By.id("content")).submit();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("content")));

        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } finally {
            if (Driver != null) Driver.quit();
        }
    }



}
