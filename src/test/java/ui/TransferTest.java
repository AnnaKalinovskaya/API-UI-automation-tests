package ui;

import generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;
import pages.TransferPage;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferTest extends BaseTest {


    @Test
    public void transferValidAmount(){
        var validAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(validAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance();
        var initialReceiverBalance = receiverBankAccount.getBalance();

        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientAccount(receiverBankAccount.getAccountNumber())
                .enterAmount(validAmount.doubleValue())
                .confirm()
                .sendTransfer();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains(String.format("Successfully transferred $%s to account %s!",
                validAmount, receiverBankAccount.getAccountNumber()));

        var transferPage = new TransferPage();
        var expectedSenderBalance = initialSenderBalance.subtract(validAmount);
        var expectedReceiverBalance = initialReceiverBalance.add(validAmount);

        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(expectedSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(expectedReceiverBalance));

        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance();
        assertThat(senderBalanceApi).isEqualTo(expectedSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(expectedReceiverBalance);
    }

    @Test
    public void transferInvalidAmount(){
        var senderBankAccount = user.createAccountWithBalance(RandomDataGenerator.getRandomDepositAmount());
        var receiverBankAccount = user.createBankAccount();
        var initialSenderBalance = senderBankAccount.getBalance();
        var initialReceiverBalance = receiverBankAccount.getBalance();
        BigDecimal invalidAmount = initialSenderBalance.add(new BigDecimal(1));

        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientAccount(receiverBankAccount.getAccountNumber())
                .enterAmount(invalidAmount.doubleValue())
                .confirm()
                .sendTransfer();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Invalid transfer: insufficient funds or invalid accounts");

        var transferPage = new TransferPage();

        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialReceiverBalance));

        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

    @Test
    public void transferWhenRecipientNameEmpty(){
        var validAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(validAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance();
        var initialReceiverBalance = receiverBankAccount.getBalance();

        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientAccount(receiverBankAccount.getAccountNumber())
                .enterAmount(validAmount.doubleValue())
                .confirm()
                .sendTransfer();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains(String.format("Successfully transferred $%s to account %s!",
                validAmount, receiverBankAccount.getAccountNumber()));

        var transferPage = new TransferPage();
        var expectedSenderBalance = initialSenderBalance.subtract(validAmount);
        var expectedReceiverBalance = initialReceiverBalance.add(validAmount);

        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(expectedSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(expectedReceiverBalance));

        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance();
        assertThat(senderBalanceApi).isEqualTo(expectedSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(expectedReceiverBalance);
    }

    @Test
    public void errorWhenSenderAccountEmpty() {
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(randomAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance();
        var initialReceiverBalance = receiverBankAccount.getBalance();

        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .enterRecipientAccount(receiverBankAccount.getAccountNumber())
                .enterAmount(randomAmount.doubleValue())
                .confirm()
                .sendTransfer();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please fill all fields and confirm.");

        var transferPage = new TransferPage();
        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialReceiverBalance));

        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

    @Test
    public void errorWhenRecipientAccountEmpty() {
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(randomAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance().doubleValue();
        var initialReceiverBalance = receiverBankAccount.getBalance().doubleValue();

        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterAmount(randomAmount.doubleValue())
                .confirm()
                .sendTransfer();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please fill all fields and confirm.");

        var transferPage = new TransferPage();
        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialReceiverBalance));

        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance().doubleValue();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance().doubleValue();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

    @Test
    public void errorWhenAmountEmpty() {
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(randomAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance().doubleValue();
        var initialReceiverBalance = receiverBankAccount.getBalance().doubleValue();

        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientName(receiverBankAccount.getAccountNumber())
                .confirm()
                .sendTransfer();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please fill all fields and confirm.");

        var transferPage = new TransferPage();
        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialReceiverBalance));

        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance().doubleValue();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance().doubleValue();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

    @Test
    public void errorWhenNoConfirmation() {
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(randomAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance().doubleValue();
        var initialReceiverBalance = receiverBankAccount.getBalance().doubleValue();

        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientName(receiverBankAccount.getAccountNumber())
                .enterAmount(randomAmount.doubleValue())
                .sendTransfer();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Please fill all fields and confirm.");

        var transferPage = new TransferPage();
        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialReceiverBalance));

        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance().doubleValue();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance().doubleValue();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

}
