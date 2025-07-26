package ui;
import api.BaseTest;
import api.config.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import common.storage.SessionStorage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ui.pages.UserDashboard;
import api.specs.RequestSpecs;

import java.util.Map;
import static com.codeborne.selenide.Selenide.executeJavaScript;


public class BaseUiTest extends BaseTest {

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperty("remote");
        Configuration.baseUrl = Config.getProperty("baseUrl");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");
        Configuration.headless = true;
        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    public static UserDashboard openUserDashboard(String userName, String pass){
        Selenide.open("/");
        String userAuthHeader = RequestSpecs.getUserAuthToken(userName, pass);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");
        return new UserDashboard();
    }

    @AfterAll
    public static void deleteUsers(){
        SessionStorage.clear();
    }
}
