package bronz.accounting.bunk.scanner;

import bronz.accounting.bunk.framework.exceptions.BunkMgmtException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BunkWebScanner {

    private String custId;
    private String pass;
    private String url;
    private String driverPath;

    private WebDriver webDriver;

    public BunkWebScanner(String custId, String pass, String url, String driverPath){
        this.custId = custId;
        this.pass = pass;
        this.url = url;
        this.driverPath = driverPath;
    }

    private WebDriver getDriver() {
        System.setProperty("webdriver.chrome.driver", driverPath);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBinary("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
        //chromeOptions.addArguments("--headless");

        return new ChromeDriver(chromeOptions);
    }

    public void loginToHpcl() {
        close();
        webDriver = getDriver();
        webDriver.navigate().to(url);

        webDriver.findElement(By.id("custid")).sendKeys(custId);
        webDriver.findElement(By.id("password")).sendKeys(pass);
        webDriver.findElement(By.id("submit")).click();

        WebDriverWait wait = new WebDriverWait(webDriver,120);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("middle"));
    }

    public void close() {
        if (webDriver != null) {
            //Close old if present
            webDriver.close();
            webDriver = null;
        }
    }

    public Map<String, String> scanPrice() throws BunkMgmtException {
        Map<String, String> results = new HashMap<String, String>();

        webDriver.findElement(By.partialLinkText("RETAIL SELLING")).click();
        WebDriverWait wait = new WebDriverWait(webDriver,30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/center/input")));

        for (int i = 3 ; i <=6 ; i++) {
            final String item = webDriver.findElement(By.xpath("/html/body/table/tbody/tr[" + i + "]/td[1]")).getText();
            final String priceText = webDriver.findElement(By.xpath("/html/body/table/tbody/tr[" + i + "]/td[2]")).getText();
            final String date = webDriver.findElement(By.xpath("/html/body/table/tbody/tr[" + i + "]/td[3]")).getText();

            results.put(item, priceText);
            results.put("DATE", date);
        }

        webDriver.findElement(By.xpath("/html/body/center/input")).click();
        return results;
    }
    public Map<String, Map<String, Map<String, String>>> scanTransactions(Calendar currTime) throws BunkMgmtException {
        Map<String, Map<String, Map<String, String>>> results = new LinkedHashMap<String, Map<String, Map<String, String>>>();
        webDriver.findElement(By.partialLinkText("TRANSACTIONS")).click();

        WebDriverWait wait = new WebDriverWait(webDriver,100);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("txtfrmday")));
        webDriver.findElement(By.name("txtfrmday")).clear();

        //Read transactions for last ~10 days
        if (currTime.get(Calendar.DAY_OF_MONTH) < 10) {
            webDriver.findElement(By.name("txtfrmday")).sendKeys(String.valueOf(20 + currTime.get(Calendar.DAY_OF_MONTH)));

            //rollback a month
            webDriver.findElement(By.name("txtfrmmonth")).clear();

            int monthValue = currTime.get(Calendar.MONTH);
            webDriver.findElement(By.name("txtfrmmonth")).sendKeys(String.valueOf(monthValue == 0? 12 : monthValue)); //currTime.get(Calendar.MONTH) starts from 0

            if (monthValue == 0) {
                //rollback a year
                webDriver.findElement(By.name("txtfrmyear")).clear();
                webDriver.findElement(By.name("txtfrmyear")).sendKeys(String.valueOf(currTime.get(Calendar.YEAR) - 1));
            }
        } else {
            webDriver.findElement(By.name("txtfrmday")).sendKeys(String.valueOf(currTime.get(Calendar.DAY_OF_MONTH) - 9));
        }
        webDriver.findElement(By.name("txtfrmday")).submit();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("content")));


        for (int i = 1 ; i <=1000 ; i++) { //Max reads thousand. Just a limit.!
            final String index = webDriver.findElement(By.xpath("/html/body/div/table/tbody/tr[" + i + "]/td[1]")).getText();
            if ("Total".equals(index)) {
                //Read complete
                break;
            }

            final Map<String, String> row = new LinkedHashMap<String, String>();

            final String recId = webDriver.findElement(By.xpath("/html/body/div/table/tbody/tr[" + i + "]/td[2]")).getText();
            final String date = webDriver.findElement(By.xpath("/html/body/div/table/tbody/tr[" + i + "]/td[4]")).getText();
            final String debitAmt = webDriver.findElement(By.xpath("/html/body/div/table/tbody/tr[" + i + "]/td[5]")).getText();
            final String creditAmt = webDriver.findElement(By.xpath("/html/body/div/table/tbody/tr[" + i + "]/td[6]")).getText();
            final String chequeOrDdNo = webDriver.findElement(By.xpath("/html/body/div/table/tbody/tr[" + i + "]/td[7]")).getText();
            final String bankName = webDriver.findElement(By.xpath("/html/body/div/table/tbody/tr[" + i + "]/td[8]")).getText();
            final String reference = webDriver.findElement(By.xpath("/html/body/div/table/tbody/tr[" + i + "]/td[9]")).getText();

            row.put("recId", recId);
            row.put("date", date);
            row.put("debitAmt", debitAmt);
            row.put("creditAmt", creditAmt);
            row.put("chequeOrDdNo", chequeOrDdNo);
            row.put("bankName", bankName);
            row.put("reference", reference);

            Map<String, Map<String, String>> dayRecords = results.get(date);
            if (dayRecords == null) {
                dayRecords = new LinkedHashMap<String, Map<String, String>>();
                results.put(date, dayRecords);
            }
            dayRecords.put(recId, row);
        }
        webDriver.findElement(By.xpath("/html/body/table[2]/tbody/tr/td[3]/a")).click();
        webDriver.findElement(By.xpath("/html/body/form/center/input[1]")).click();


        return results;
    }
}
