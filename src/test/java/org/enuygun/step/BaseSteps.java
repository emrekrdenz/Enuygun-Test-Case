package org.enuygun.step;

import com.thoughtworks.gauge.Gauge;
import com.thoughtworks.gauge.Step;
import org.enuygun.Base.BaseTest;
import org.enuygun.model.ElementInfo;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseSteps extends BaseTest {

    public BaseSteps() {
        initMap(getFileList());
    }

    private void clickElement(WebElement element) {
        element.click();
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

    @Step("<key> elementine <text> textini yaz")
    public void sendKeys(String key, String text) {
        if (!key.equals("")) {
            findElement(key).sendKeys(text);
            logger.info(key + " elementine " + text + " texti yazildi.");
        }
    }

    @Step("<key> Elementine tıkla")
    public void clickElement(String key) {
        if (!key.isEmpty()) {
            clickElement(findElement(key));
            logger.info(key + " elementine tiklandi.");
        }
    }

    @Step("<url> adresine git")
    public void goToUrl(String url){
        driver.navigate().to(url);
        Gauge.writeMessage("Page title is %s",driver.getTitle());
        Gauge.captureScreenshot();
    }

    @Step("<saniye> saniye bekle")
    public void bekle(int saniye) throws InterruptedException {
        Thread.sleep(saniye*1000);
    }


    @Step("<key> li checkbox secili degilse sec")
    public void checkbox(String key){
        boolean isMarkedDone = findElement(key).isSelected();
        System.out.println("Tek yon secili." +isMarkedDone);

        if(isMarkedDone==true){
            clickElement(key);
            System.out.println("Gidis Dönüs olarak secildi.");
        }

    }

    @Step("ddMMyy formatında <day>-<month>-<year> bilet tarihi giriniz.")
    public void selectDate(String day,String month,int year) throws InterruptedException {

        LocalDate localDate=LocalDate.now();
        int localYear=localDate.getYear();
        String ayYil=AylarConst.getAy(month)+" "+year;


        if(localYear==year || localYear==((year)-1)){

            while(true){
                String text=findElement("ayYilBasligi").getText();

                if(text.equals(ayYil)){
                    break;
                }
                else{
                    clickElement("gelecekAyButonu");
                    Thread.sleep(1000);
                }
            }

            List <WebElement> allDates=driver.findElements(By.xpath("//div[@data-visible='true']/table/tbody/tr/td[@role='button' and @aria-disabled='false' and @aria-label=contains(@aria-label,'"+AylarConst.getAy(month)+"')]"));

            for(WebElement ele:allDates)
            {
                String text=ele.getText();
                if(text.equals(day)){
                    ele.click();
                    System.out.println("Elemente tiklandi..");
                    break;
                }
            }
        }
        else {
            Assertions.fail("Lutfen gecerli bir tarih araligi giriniz!.. Girilebilecek Yıllar : "+localYear+ "ve "+((localYear)+1));
        }
    }

    @Step("<key> elementini kontrol et")
    public void checkElement(String key) {
        assertTrue(findElement(key).isDisplayed(), "Aranan element bulunamadi");
        logger.info(key+ "elementi gorundu.");
    }


    @Step("<yetiskin> biletini <adet1> adet <cocuk> biletini <adet2> adet <bebek> biletini <adet3> adet <yas65> biletini <adet4> <ogrenci> biletini <adet5> arttır")
    public void yolcu2(String yetiskin,int adet1,String cocuk,int adet2,String bebek,int adet3,String yas65,int adet4,String ogrenci,int adet5) {

        int count=adet1+adet2+adet4+adet5;


        for(int i=0;i<adet1;i++){
            clickElement(yetiskin);
        }
        clickElement("yetiskinYolcuAzaltma");

        for(int i=0;i<adet2;i++){
            clickElement(cocuk);
        }

        for(int i=0;i<adet3;i++){
            clickElement(bebek);
        }
        for(int i=0;i<adet4;i++){
            clickElement(yas65);
        }

        for(int i=0;i<adet5;i++){
            clickElement(ogrenci);
        }

        if(count>=9){
            checkElement("grupUcusTeklifiAl");
            logger.info("Grup Ucus Teklifi Al Butonu Gorundu");
            Assertions.fail("Maksimum alinabilecek bilet sayisi 9'dur.İstenilen bilet sayisi: "+count);
        }
        else if (adet3>adet1){
            Assertions.fail("Yetişkin sayisi kadar bebek bileti alinabilir!!");
        }

        clickElement("yolcuTamamButonu");

    }

    public void javascriptclicker(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    @Step("<key> elementine javascript ile tıkla")
    public void clickToElementWithJavaScript(String key) {
        WebElement element = findElement(key);
        javascriptclicker(element);
        logger.info(key + " elementine javascript ile tiklandi");
    }


}

