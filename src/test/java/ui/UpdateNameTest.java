package ui;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pages.EditProfilePage;
import pages.UserDashboard;
import skelethon.steps.UserSteps;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateNameTest extends BaseTest{

    @ParameterizedTest
    @ValueSource(strings = {"Random Name"})
    public void userEditNameWithValidValue(String validValue){
        UserSteps user = createRandomUser();

        EditProfilePage editProfilePage = openUserDashboard(user.getName(), user.getPass())
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

        String nameViaAPI = user.getCustomerProfile().getName();
        assertThat(nameViaAPI).isEqualTo(validValue);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Random"})
    public void userEditNameWithInvalidValue(String invalidValue){
        UserSteps user = createRandomUser();
        String initialName = openUserDashboard(user.getName(), user.getPass()).getName();

        EditProfilePage editProfilePage = new UserDashboard()
                .goToUserInfo()
                .enterNewName(invalidValue)
                .saveChanges();

        String alertText = confirmAlertAndGetText();

        assertThat(alertText).contains("Name must contain two words with letters only");
        assertThat(editProfilePage.getName()).isEqualTo(initialName);

        editProfilePage.goHome();
        UserDashboard updatedDashboard = new UserDashboard();
        assertThat(updatedDashboard.getWelcomeLabel().getText())
                .isEqualTo(String.format("Welcome, %s!", initialName.toLowerCase()));

        String nameViaAPI = user.getCustomerProfile().getName();
        assertThat(nameViaAPI).isEqualTo(null);
    }
}
