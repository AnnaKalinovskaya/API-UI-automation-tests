package ui;
import api.config.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import api.generators.RandomDataGenerator;
import api.models.UserProfileModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ui.pages.UserDashboard;
import api.skelethon.requests.Endpoint;
import api.skelethon.requests.ValidatableCrudRequester;
import api.skelethon.steps.AdminSteps;
import api.skelethon.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.LinkedList;
import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class BaseUiTest {

    private static LinkedList<UserProfileModel> randomUsers = new LinkedList<>();

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.remote = Config.getProperty("remote");
        Configuration.baseUrl = Config.getProperty("baseUrl");
        Configuration.browser = Config.getProperty("browser");
        Configuration.browserSize = Config.getProperty("browserSize");

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    protected static UserSteps createRandomUser(){
        UserSteps user = new UserSteps(RandomDataGenerator.getRandomUserName(), RandomDataGenerator.getRandomPass());
        UserProfileModel userProfile = AdminSteps.createUser(user.getName(), user.getPass());
        randomUsers.add(userProfile);
        return user;
    }

    protected UserDashboard openUserDashboard(String userName, String pass){
        Selenide.open("/");
        String userAuthHeader = RequestSpecs.getUserAuthToken(userName, pass);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");
        return new UserDashboard();
    }

    @AfterAll
    public static void deleteUsers(){
        for (UserProfileModel user : randomUsers) {
            new ValidatableCrudRequester(RequestSpecs.adminSpec(), Endpoint.USERS, ResponseSpecs.returns200())
                    .delete(user.getId());
        }
    }
}
