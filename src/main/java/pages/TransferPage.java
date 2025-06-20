package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage {

    private final SelenideElement senderAccountDropDown = $(Selectors.byClassName("form-control account-selector"));
    private final SelenideElement recipientName = $(Selectors.byAttribute("placeholder",
            "Enter recipient name"));
    private final SelenideElement recipientAccountNumber = $(Selectors.byAttribute("placeholder",
            "Enter recipient account number"));
    private final SelenideElement amountField = $(Selectors.byAttribute("placeholder",
            "Enter amount"));
    private final SelenideElement confirmCheck = $(Selectors.byId("confirmCheck"));
    private final SelenideElement sendTransferButton = $(Selectors.byXpath("//button[contains(text(), 'Send Transfer')]"));


    public TransferPage selectSenderAccount(String accountName){
        senderAccountDropDown.getSelectedOptions()
                .stream().filter(option -> option.getText().contains("accountName"))
                .findFirst().get().click();
        return this;
    }

    public TransferPage enterRecipientName(String name){
        recipientName.sendKeys(name);
        return this;
    }

    public TransferPage enterRecipientAccount(String accountName){
        recipientAccountNumber.sendKeys(accountName);
        return this;
    }

    public TransferPage enterAmount(double amount){
        amountField.sendKeys(String.valueOf(amount));
        return this;
    }

    public TransferPage confirm(){
        confirmCheck.click();
        return this;
    }

    public TransferPage sendTransfer(){
        sendTransferButton.click();
        return this;
    }

    public ElementsCollection getAccountOptions(){
        return senderAccountDropDown.getOptions();
    }
}
