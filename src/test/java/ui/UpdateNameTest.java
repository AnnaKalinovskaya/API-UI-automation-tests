package ui;

import com.codeborne.selenide.Selenide;
import generators.RandomDataGenerator;
import models.LoginRequestModel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pages.EditProfilePage;
import pages.UserDashboard;
import skelethon.requests.Endpoint;
import skelethon.requests.ValidatableCrudRequester;
import skelethon.steps.AdminSteps;
import skelethon.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateNameTest extends BaseTest{

    @ParameterizedTest
    @ValueSource(strings = {"Random Name"})
    public void userEditNameWithValidValue(String validValue){
        String userName = RandomDataGenerator.getRandomUserName();
        String pass = RandomDataGenerator.getRandomPass();
        AdminSteps.createUser(userName, pass);

        Selenide.open("/");
        String userAuthHeader = new ValidatableCrudRequester(
                RequestSpecs.unauthSpec(),
                Endpoint.AUTH_LOGIN,
                ResponseSpecs.returns200())
                .post(LoginRequestModel.builder().username(userName).password(pass).build())
                .extract().header("Authorization");

        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
        Selenide.open("/dashboard");

        EditProfilePage editProfilePage = new UserDashboard()
                .goToUserInfo()
                .enterNewName(validValue)
                .saveChanges();

        String alertText = confirmAlertAndGetText();

        assertThat(alertText).contains("Name updated successfully!");
        assertThat(editProfilePage.getName()).isEqualTo(validValue);

        editProfilePage.goHome();
        UserDashboard updatedDashboard = new UserDashboard();

        assertThat(updatedDashboard.getName()).isEqualTo(validValue);
        assertThat(updatedDashboard.getWelcomeLabel().getText()).isEqualTo(String.format("Welcome, %s!", validValue));

        String nameViaAPI = new UserSteps(userName, pass).getCustomerProfile().getName();
        assertThat(nameViaAPI).isEqualTo(validValue);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Random"})
    public void userEditNameWithInvalidValue(String invalidValue){
        String userName = RandomDataGenerator.getRandomUserName();
        String pass = RandomDataGenerator.getRandomPass();
        AdminSteps.createUser(userName, pass);

        String initialName = openUserDashboard(userName, pass).getName();

        EditProfilePage editProfilePage = new UserDashboard()
                .goToUserInfo()
                .enterNewName(invalidValue)
                .saveChanges();

        String alertText = confirmAlertAndGetText();

        assertThat(alertText).contains("Name must contain two words with letters only");
        assertThat(editProfilePage.getName()).isEqualTo(initialName);

        editProfilePage.goHome();
        UserDashboard updatedDashboard = new UserDashboard();
        assertThat(updatedDashboard.getWelcomeLabel().getText()).isEqualTo(String.format("Welcome, %s!", initialName));

        String nameViaAPI = new UserSteps(userName, pass).getCustomerProfile().getName();
        assertThat(nameViaAPI).isEqualTo(null);
    }


}
