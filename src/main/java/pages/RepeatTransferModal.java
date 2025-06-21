package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class RepeatTransferModal {

    @Getter
    private final String receiverAccount = $(Selectors.byXpath("//div[@class='modal-body']/p/strong")).getText();
    private final ElementsCollection senderAccountOptions = $$(Selectors.byXpath("//div[@class='modal-body']/select/option"));
    private final SelenideElement amountInput = $(Selectors.byXpath("//div[@class='modal-body']/input[@type='number']"));
    private final SelenideElement confirmCheck = $(Selectors.byXpath("//div[@class='modal-body']//input[contains(@class, 'form-check-input')]"));
    private final SelenideElement sendTransferButton = $(Selectors.byXpath("//div[@class='modal-footer']/button[contains(@class, 'btn-success')]"));

    public RepeatTransferModal selectSenderAccount(String accountName){
        senderAccountOptions.get(0).click();
        senderAccountOptions.stream()
                .filter(option -> option.getText().contains(accountName))
                .findFirst().get().click();
        return this;
    }

    public RepeatTransferModal enterAmount(double amount){
        amountInput.sendKeys(String.valueOf(amount));
        return this;
    }

    public RepeatTransferModal confirm(){
        confirmCheck.click();
        return this;
    }

    public TransactionsPage clickSendTransfer(){
        sendTransferButton.click();
        return new TransactionsPage();
    }

}
