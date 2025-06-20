package ui;

import generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pages.DepositPage;
import skelethon.steps.AdminSteps;
import skelethon.steps.UserSteps;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

public class DepositTest extends BaseTest{

    @ParameterizedTest
    @ValueSource(doubles = {500.00})
    public void depositValidAmount(double validAmount){
        String userName = RandomDataGenerator.getRandomUserName();
        String pass = RandomDataGenerator.getRandomPass();
        AdminSteps.createUser(userName, pass);
        var bankAccount = new UserSteps(userName, pass).createBankAccount();
        double initialBalance = bankAccount.getBalance().doubleValue();

        openUserDashboard(userName, pass)
                .goToDeposit()
                .selectAccount(bankAccount.getAccountNumber())
                .enterAmount(validAmount)
                .clickDeposit();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains(String.format("Successfully deposited %s to account %s!", validAmount, bankAccount.getAccountNumber()));

        String accountInDropDrown = new DepositPage().getAccountOptions()
                .stream().filter(option -> option.getText().contains(bankAccount.getAccountNumber()))
                .findFirst().get().getText();
        double expectedBalance = initialBalance + validAmount;
        assertThat(accountInDropDrown).contains(String.valueOf(expectedBalance));

        BigDecimal balanceApiResult = new UserSteps(userName, pass).getAllBankAccounts()
                .getAccount(bankAccount.getId()).getBalance();
        assertThat(balanceApiResult).isEqualTo(new BigDecimal(expectedBalance));
    }

    @ParameterizedTest
    @ValueSource(doubles = {6000.00})
    public void depositInvalidAmount(double invalidAmount){
        String userName = RandomDataGenerator.getRandomUserName();
        String pass = RandomDataGenerator.getRandomPass();
        AdminSteps.createUser(userName, pass);
        var bankAccount = new UserSteps(userName, pass).createBankAccount();
        BigDecimal initialBalance = bankAccount.getBalance();

        openUserDashboard(userName, pass)
                .goToDeposit()
                .selectAccount(bankAccount.getAccountNumber())
                .enterAmount(invalidAmount)
                .clickDeposit();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please deposit less or equal to 5000$.");

        String createdAccountOption = new DepositPage().getAccountOptions()
                .stream().filter(option -> option.getText().contains(bankAccount.getAccountNumber()))
                .findFirst().get().getText();
        assertThat(createdAccountOption).contains(String.valueOf(initialBalance));

        BigDecimal balanceApiResult = new UserSteps(userName, pass).getAllBankAccounts()
                .getAccount(bankAccount.getId()).getBalance();
        assertThat(balanceApiResult).isEqualTo(initialBalance);
    }

    @ParameterizedTest
    @ValueSource(doubles = {4000})
    public void accountNotSelected(double validAmount){
        String userName = RandomDataGenerator.getRandomUserName();
        String pass = RandomDataGenerator.getRandomPass();
        AdminSteps.createUser(userName, pass);
        var userSteps = new UserSteps(userName, pass);
        userSteps.createBankAccount();
        var accountsApi = userSteps.getAllBankAccounts();

        DepositPage depositPage = openUserDashboard(userName, pass).goToDeposit();
        var accountOptionsBeforeDeposit = depositPage.getAccountOptions();
        depositPage.enterAmount(validAmount).clickDeposit();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please select an account.");

        var accountOptionsAfterDeposit = new DepositPage().getAccountOptions();
        var accountsApiAfterDeposit = userSteps.getAllBankAccounts();
        assertThat(accountOptionsAfterDeposit).isEqualTo(accountOptionsBeforeDeposit);
        assertThat(accountsApiAfterDeposit).isEqualTo(accountsApi);
    }

    @Test
    public void amountFieldIsEmpty(){
        String userName = RandomDataGenerator.getRandomUserName();
        String pass = RandomDataGenerator.getRandomPass();
        AdminSteps.createUser(userName, pass);
        var userSteps = new UserSteps(userName, pass);
        var bankAccount = userSteps.createBankAccount();
        var accountsApi = userSteps.getAllBankAccounts();

        DepositPage depositPage = openUserDashboard(userName, pass).goToDeposit();
        var accountOptionsBeforeDeposit = depositPage.getAccountOptions();
        depositPage.selectAccount(bankAccount.getAccountNumber()).clickDeposit();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please enter a valid amount.");

        var accountOptionsAfterDeposit = new DepositPage().getAccountOptions();
        var accountsApiAfterDeposit = userSteps.getAllBankAccounts();
        assertThat(accountOptionsAfterDeposit).isEqualTo(accountOptionsBeforeDeposit);
        assertThat(accountsApiAfterDeposit).isEqualTo(accountsApi);
    }
}
