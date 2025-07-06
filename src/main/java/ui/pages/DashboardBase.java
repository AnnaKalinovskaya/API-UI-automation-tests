package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Alert;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class DashboardBase <D extends DashboardBase> extends BasePage<DashboardBase>{

    protected final SelenideElement profileName = $(Selectors.byClassName("user-name"));
    protected final SelenideElement homeButton = $(Selectors.byXpath("//button[contains(text(), 'Home')]"));

    public String getName(){
        return profileName.getText();
    }

    public UserDashboard goHome(){
        homeButton.click();
        return new UserDashboard();
    }

    public D checkAlertAndAccept(String bankAlert){
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains(bankAlert);
        alert.accept();
        return (D) this;
    }
}
