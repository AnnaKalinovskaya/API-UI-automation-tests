package pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public abstract class Dashboard <D extends Dashboard>{

    final SelenideElement profileName = $(Selectors.byClassName("user-name"));
    final SelenideElement homeButton = $(Selectors.byXpath("//button[contains(text(), 'Home')]"));

    public String getName(){
        return profileName.getText();
    }

    public void goHome(){
        homeButton.click();
    }
}
