package ui;

import com.codeborne.selenide.SelenideElement;
import generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;
import pages.DepositPage;
import pages.UserDashboard;
import skelethon.steps.UserSteps;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

public class DepositTest extends BaseTest{

    @Test
    public void depositValidAmount(){
        //Pre-conditions: User is created and logged in, at least 1 bank account was created
        UserSteps user = createRandomUser();
        BigDecimal validAmount = RandomDataGenerator.getRandomDepositAmount();
        var bankAccount = user.createBankAccount();
        BigDecimal initialBalance = bankAccount.getBalance();

        //Test steps via UI
        openUserDashboard(user.getName(), user.getPass())
                .goToDeposit()
                .selectAccount(bankAccount.getAccountNumber())
                .enterAmount(validAmount.doubleValue())
                .clickDeposit();

        //Assert alert text
        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains(String.format("Successfully deposited $%s to account %s!", validAmount, bankAccount.getAccountNumber()));

        //Check 'Select Account' drop-down: Account balance should be updated with deposited amount.
        String accountInDropDrown = new UserDashboard().goToDeposit().getAccountOptions()
                .stream().filter(option -> option.getText().contains(bankAccount.getAccountNumber()))
                .findFirst().get().getText();
        BigDecimal expectedBalance = initialBalance.add(validAmount);
        assertThat(accountInDropDrown).contains(String.valueOf(expectedBalance));
        //Check via API that account balance was updated with deposited amount
        BigDecimal balanceApiResult = user.getAllBankAccounts()
                .getAccount(bankAccount.getId()).getBalance();
        assertThat(balanceApiResult).isEqualTo(expectedBalance);
    }

    @Test
    public void errorWhenDepositInvalidAmount(){
        //Pre-conditions: User is created and logged in, at least 1 bank account was created
        UserSteps user = createRandomUser();
        BigDecimal invalidAmount = RandomDataGenerator.getRandomAmount(5000, 10000);
        var bankAccount = user.createBankAccount();
        BigDecimal initialBalance = bankAccount.getBalance();

        //Test steps via UI
        openUserDashboard(user.getName(), user.getPass())
                .goToDeposit()
                .selectAccount(bankAccount.getAccountNumber())
                .enterAmount(invalidAmount.doubleValue())
                .clickDeposit();

        //Assert alert text
        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please deposit less or equal to 5000$.");

        //Check 'Select Account' drop-down: Account balance should NOT get updated.
        String createdAccountOption = new DepositPage().getAccountOptions()
                .stream().filter(option -> option.getText().contains(bankAccount.getAccountNumber()))
                .findFirst().get().getText();
        assertThat(createdAccountOption).contains(String.valueOf(initialBalance));
        //Check via API that account balance was NOT updated
        BigDecimal balanceApiResult = user.getAllBankAccounts()
                .getAccount(bankAccount.getId()).getBalance();
        assertThat(balanceApiResult).isEqualTo(initialBalance);
    }

    @Test
    public void errorWhenAccountNotSelected(){
        //Pre-conditions: User is created and logged in, at least 1 bank account was created
        UserSteps user = createRandomUser();
        BigDecimal validAmount = RandomDataGenerator.getRandomDepositAmount();
        user.createBankAccount();
        var accountsApi = user.getAllBankAccounts();

        //Test steps via UI
        DepositPage depositPage = openUserDashboard(user.getName(), user.getPass()).goToDeposit();
        var accountOptionsBeforeDeposit = depositPage.getAccountOptions()
                .stream().map(SelenideElement::getText).toList();
        depositPage.enterAmount(validAmount.doubleValue()).clickDeposit();

        //Assert alert text
        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please select an account.");

        //Check 'Select Account' drop-down: Account balance was NOT updated.
        var accountOptionsAfterDeposit = new DepositPage().getAccountOptions()
                .stream().map(SelenideElement::getText).toList();
        assertThat(accountOptionsAfterDeposit).isEqualTo(accountOptionsBeforeDeposit);
        //Check via API that account balance was NOT updated
        var accountsApiAfterDeposit = user.getAllBankAccounts();
        assertThat(accountsApiAfterDeposit).isEqualTo(accountsApi);
    }

    @Test
    public void errorWhenAmountFieldIsEmpty(){
        //Pre-conditions: User is created and logged in, at least 1 bank account was created
        UserSteps user = createRandomUser();
        var bankAccount = user.createBankAccount();
        var accountsApi = user.getAllBankAccounts();

        //Test steps via UI
        DepositPage depositPage = openUserDashboard(user.getName(), user.getPass()).goToDeposit();
        var accountOptionsBeforeDeposit = depositPage.getAccountOptions()
                .stream().map(SelenideElement::getText).toList();
        depositPage.selectAccount(bankAccount.getAccountNumber()).clickDeposit();

        //Assert alert text
        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please enter a valid amount.");

        //Check 'Select Account' drop-down: Account balance was NOT updated.
        var accountOptionsAfterDeposit = new DepositPage().getAccountOptions()
                .stream().map(SelenideElement::getText).toList();
        assertThat(accountOptionsAfterDeposit).isEqualTo(accountOptionsBeforeDeposit);
        //Check via API that account balance was NOT updated
        var accountsApiAfterDeposit = user.getAllBankAccounts();
        assertThat(accountsApiAfterDeposit).isEqualTo(accountsApi);
    }
}
