package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;

public class DepositPage extends Dashboard{

    private final SelenideElement accountDropDown = $(Selectors.byClassName("form-control account-selector"));
    private final SelenideElement amountField = $(Selectors.byClassName("form-control deposit-input"));
    private final SelenideElement depositButton = $(Selectors.byXpath("//button[contains(text(), 'Deposit')]"));

    public DepositPage selectAccount (String accountName){
        accountDropDown.getSelectedOptions()
                .stream().filter(option -> option.getText().contains(accountName))
                .findFirst().get().click();
        return this;
    }

    public ElementsCollection getAccountOptions(){
        return accountDropDown.getOptions();
    }

    public DepositPage enterAmount(double amount){
        amountField.setValue(Double.toString(amount));
        return this;
    }

    public DepositPage clickDeposit(){
        depositButton.click();
        return this;
    }
}
