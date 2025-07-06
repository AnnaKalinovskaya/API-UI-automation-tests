package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditProfilePage extends DashboardBase<EditProfilePage> {

    private final SelenideElement newNameField = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private final SelenideElement saveButton = $(Selectors.byXpath("//button[contains(text(), 'Save Changes')]"));

    public String url(){
        return "/edit-profile";
    }

    public EditProfilePage enterNewName(String name){
        newNameField.sendKeys(name);
        return this;
    }

    public EditProfilePage saveChanges(){
        saveButton.click();
        return this;
    }

}
