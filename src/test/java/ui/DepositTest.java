package ui;

import com.codeborne.selenide.SelenideElement;
import generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;
import pages.DepositPage;
import pages.UserDashboard;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

public class DepositTest extends BaseTest{

    @Test
    public void depositValidAmount(){
        BigDecimal validAmount = RandomDataGenerator.getRandomDepositAmount();
        var bankAccount = user.createBankAccount();
        BigDecimal initialBalance = bankAccount.getBalance();

        openUserDashboard(user.getName(), user.getPass())
                .goToDeposit()
                .selectAccount(bankAccount.getAccountNumber())
                .enterAmount(validAmount.doubleValue())
                .clickDeposit();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains(String.format("Successfully deposited $%s to account %s!", validAmount, bankAccount.getAccountNumber()));

        String accountInDropDrown = new UserDashboard().goToDeposit().getAccountOptions()
                .stream().filter(option -> option.getText().contains(bankAccount.getAccountNumber()))
                .findFirst().get().getText();
        BigDecimal expectedBalance = initialBalance.add(validAmount);
        assertThat(accountInDropDrown).contains(String.valueOf(expectedBalance));

        BigDecimal balanceApiResult = user.getAllBankAccounts()
                .getAccount(bankAccount.getId()).getBalance();
        assertThat(balanceApiResult).isEqualTo(expectedBalance);
    }

    @Test
    public void depositInvalidAmount(){
        BigDecimal invalidAmount = RandomDataGenerator.getRandomAmount(5000, 10000);
        var bankAccount = user.createBankAccount();
        BigDecimal initialBalance = bankAccount.getBalance();

        openUserDashboard(user.getName(), user.getPass())
                .goToDeposit()
                .selectAccount(bankAccount.getAccountNumber())
                .enterAmount(invalidAmount.doubleValue())
                .clickDeposit();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please deposit less or equal to 5000$.");

        String createdAccountOption = new DepositPage().getAccountOptions()
                .stream().filter(option -> option.getText().contains(bankAccount.getAccountNumber()))
                .findFirst().get().getText();
        assertThat(createdAccountOption).contains(String.valueOf(initialBalance));

        BigDecimal balanceApiResult = user.getAllBankAccounts()
                .getAccount(bankAccount.getId()).getBalance();
        assertThat(balanceApiResult).isEqualTo(initialBalance);
    }

    @Test
    public void accountNotSelected(){
        BigDecimal validAmount = RandomDataGenerator.getRandomDepositAmount();
        user.createBankAccount();
        var accountsApi = user.getAllBankAccounts();

        DepositPage depositPage = openUserDashboard(user.getName(), user.getPass()).goToDeposit();
        var accountOptionsBeforeDeposit = depositPage.getAccountOptions()
                .stream().map(SelenideElement::getText).toList();
        depositPage.enterAmount(validAmount.doubleValue()).clickDeposit();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please select an account.");

        var accountOptionsAfterDeposit = new DepositPage().getAccountOptions()
                .stream().map(SelenideElement::getText).toList();
        var accountsApiAfterDeposit = user.getAllBankAccounts();
        assertThat(accountOptionsAfterDeposit).isEqualTo(accountOptionsBeforeDeposit);
        assertThat(accountsApiAfterDeposit).isEqualTo(accountsApi);
    }

    @Test
    public void amountFieldIsEmpty(){
        var bankAccount = user.createBankAccount();
        var accountsApi = user.getAllBankAccounts();

        DepositPage depositPage = openUserDashboard(user.getName(), user.getPass()).goToDeposit();
        var accountOptionsBeforeDeposit = depositPage.getAccountOptions()
                .stream().map(SelenideElement::getText).toList();
        depositPage.selectAccount(bankAccount.getAccountNumber()).clickDeposit();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please enter a valid amount.");

        var accountOptionsAfterDeposit = new DepositPage().getAccountOptions()
                .stream().map(SelenideElement::getText).toList();
        var accountsApiAfterDeposit = user.getAllBankAccounts();
        assertThat(accountOptionsAfterDeposit).isEqualTo(accountOptionsBeforeDeposit);
        assertThat(accountsApiAfterDeposit).isEqualTo(accountsApi);
    }
}
