package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TransferPage extends DashboardBase<TransferPage>{

    private final SelenideElement transferAgainButton = $(Selectors.byXpath("//button[contains(text(), 'Transfer Again')]"));

    private final ElementsCollection senderAccountDropDown = $$(Selectors.byXpath("//select[contains(@class, 'account-selector')]/option"));
    private final SelenideElement recipientName = $(Selectors.byAttribute("placeholder",
            "Enter recipient name"));
    private final SelenideElement recipientAccountNumber = $(Selectors.byAttribute("placeholder",
            "Enter recipient account number"));
    private final SelenideElement amountField = $(Selectors.byAttribute("placeholder",
            "Enter amount"));
    private final SelenideElement confirmCheck = $(Selectors.byId("confirmCheck"));

    private final SelenideElement sendTransferButton = $(Selectors.byXpath("//button[contains(text(), 'Send Transfer')]"));

    public String url(){
        return "/transfer";
    }

    public TransferPage selectSenderAccount(String accountName){
        senderAccountDropDown.get(0).click();
        senderAccountDropDown.stream()
                .filter(option -> option.getText().contains(accountName))
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

    public TransferPage clickConfirm(){
        confirmCheck.click();
        return this;
    }

    public TransferPage cickSendTransfer(){
        sendTransferButton.click();
        return this;
    }

    public String getBankAccountText(String accountNumber){
        return senderAccountDropDown
                .stream().filter(option -> option.getText().contains(accountNumber))
                .findFirst().get().getText();
    }

    public TransactionsPage clickTransferAgain(){
        transferAgainButton.click();
        return new TransactionsPage();
    }
}
