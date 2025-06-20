package ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import models.LoginRequestModel;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.Alert;
import pages.UserDashboard;
import skelethon.requests.Endpoint;
import skelethon.requests.ValidatableCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.switchTo;

public class BaseTest {

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://172.22.224.1:3000";
        Configuration.browser = "firefox";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }


    protected UserDashboard openUserDashboard(String userName, String pass){
        Selenide.open("/");
        String userAuthHeader = new ValidatableCrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.AUTH_LOGIN,
                ResponseSpecs.returns200())
                .post(LoginRequestModel.builder().username(userName).password(pass).build())
                .extract().header("Authorization");

        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");
        return new UserDashboard();
    }

    protected String confirmAlertAndGetText(){
        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        alert.accept();
        return alertText;
    }
}
