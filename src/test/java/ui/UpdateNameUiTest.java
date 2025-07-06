package ui;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ui.pages.BankAlert;
import ui.pages.EditProfilePage;
import ui.pages.UserDashboard;
import api.skelethon.steps.UserSteps;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateNameUiTest extends BaseUiTest {

    @ParameterizedTest
    @ValueSource(strings = {"Random Name"})
    public void userEditNameWithValidValue(String validValue){
        UserSteps user = createRandomUser();

        EditProfilePage editProfilePage = openUserDashboard(user.getName(), user.getPass())
                .goToUserInfo()
                .enterNewName(validValue)
                .saveChanges()
                .checkAlertAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage());


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
                .saveChanges()
                .checkAlertAndAccept(BankAlert.NAME_MUST_CONTAIN.getMessage());

        assertThat(editProfilePage.getName()).isEqualTo(initialName);

        editProfilePage.goHome();
        UserDashboard updatedDashboard = new UserDashboard();
        assertThat(updatedDashboard.getWelcomeLabel().getText())
                .isEqualTo(String.format("Welcome, %s!", initialName.toLowerCase()));

        String nameViaAPI = user.getCustomerProfile().getName();
        assertThat(nameViaAPI).isEqualTo(null);
    }
}
