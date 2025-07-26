package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DepositPage extends DashboardBase<DepositPage> {

    private final ElementsCollection accountsOptions = $$(Selectors.byXpath("//select[contains(@class, 'account-selector')]/option"));
    private final SelenideElement amountField = $(Selectors.byClassName("deposit-input"));
    private final SelenideElement depositButton = $(Selectors.byXpath("//button[contains(text(), 'Deposit')]"));

    public String url(){
        return "/deposit";
    }

    public DepositPage selectAccount (String accountName) throws InterruptedException {
        Thread.sleep(5000);
        accountsOptions.stream()
                .filter(option -> option.getText().contains(accountName))
                .findFirst().ifPresentOrElse(
                        SelenideElement::click,
                        () ->{ throw new RuntimeException("Account not found: " + accountName); }
        );
        return this;
    }

    public ElementsCollection getAccountOptions() throws InterruptedException {
        Thread.sleep(5000);
        return accountsOptions;
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
