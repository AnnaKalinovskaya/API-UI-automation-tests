package ui;

import api.generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.TransferPage;
import api.skelethon.steps.UserSteps;
import java.math.BigDecimal;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferUiTest extends BaseUiTest {


    @Test
    public void transferValidAmount(){
        //Pre-conditions: User is created and logged in, at least 2 bank account were created,
        // one of them with balance > 0
        UserSteps user = createRandomUser();
        var validAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(validAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance();
        var initialReceiverBalance = receiverBankAccount.getBalance();

        //Test steps via UI
        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientAccount(receiverBankAccount.getAccountNumber())
                .enterAmount(validAmount.doubleValue())
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(String.format("Successfully transferred $%s to account %s!",
                        validAmount, receiverBankAccount.getAccountNumber()));

        //Check 'Select Account' drop-down: sender balance was reduced
        // and receiver balance was increased with transferred amount.
        var transferPage = new TransferPage();
        var expectedSenderBalance = initialSenderBalance.subtract(validAmount);
        var expectedReceiverBalance = initialReceiverBalance.add(validAmount);

        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(expectedSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(expectedReceiverBalance));

        //Check via API that sender balance was reduced and receiver balance was increased with transferred amount.
        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance();

        assertThat(senderBalanceApi).isEqualTo(expectedSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(expectedReceiverBalance);
    }

    @Test
    public void errorWhenTransferInvalidAmount(){
        //Pre-conditions: User is created and logged in, at least 2 bank account were created, one of them with balance > 0
        UserSteps user = createRandomUser();
        var senderBankAccount = user.createAccountWithBalance(RandomDataGenerator.getRandomDepositAmount());
        var receiverBankAccount = user.createBankAccount();
        var initialSenderBalance = senderBankAccount.getBalance();
        var initialReceiverBalance = receiverBankAccount.getBalance();
        BigDecimal invalidAmount = initialSenderBalance.add(new BigDecimal(1));

        //Test steps via UI
        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientAccount(receiverBankAccount.getAccountNumber())
                .enterAmount(invalidAmount.doubleValue())
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(BankAlert.INVALID_TRANSFER.getMessage());

        var transferPage = new TransferPage();

        //Check Select Account drop-down: both accounts balance were NOT updated
        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialReceiverBalance));

        //Check via API that both account balance were NOT updated with transferred amount
        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

    @Test
    public void errorWhenRecipientNameEmpty(){
        //Pre-conditions: User is created and logged in, at least 2 bank account were created, one of them with balance > 0
        UserSteps user = createRandomUser();
        var validAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(validAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance();
        var initialReceiverBalance = receiverBankAccount.getBalance();

        //Test steps via UI
        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientAccount(receiverBankAccount.getAccountNumber())
                .enterAmount(validAmount.doubleValue())
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(BankAlert.FILL_ALL_FIELDS.getMessage());

        //Check Accounts drop-down: accounts balance weren't updated
        var transferPage = new TransferPage();
        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialReceiverBalance));

        //Check via API that accounts balance weren't updated
        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

    @Test
    public void errorWhenSenderAccountEmpty() {
        //Pre-conditions: User is created and logged in, at least 2 bank account were created, one of them with balance > 0
        UserSteps user = createRandomUser();
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(randomAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance();
        var initialReceiverBalance = receiverBankAccount.getBalance();

        //Test steps via UI
        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .enterRecipientAccount(receiverBankAccount.getAccountNumber())
                .enterAmount(randomAmount.doubleValue())
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(BankAlert.FILL_ALL_FIELDS.getMessage());

        //Check Accounts drop-down: accounts balance weren't updated
        var transferPage = new TransferPage();
        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialReceiverBalance));

        //Check via API that accounts balance weren't updated
        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

    @Test
    public void errorWhenRecipientAccountEmpty() {
        //Pre-conditions: User is created and logged in, at least 2 bank account were created, one of them with balance > 0
        UserSteps user = createRandomUser();
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(randomAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance().doubleValue();
        var initialReceiverBalance = receiverBankAccount.getBalance().doubleValue();

        //Test steps via UI
        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterAmount(randomAmount.doubleValue())
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(BankAlert.FILL_ALL_FIELDS.getMessage());

        //Check Accounts drop-down: accounts balance weren't updated
        var transferPage = new TransferPage();
        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialReceiverBalance));

        //Check via API that accounts balance weren't updated
        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance().doubleValue();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance().doubleValue();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

    @Test
    public void errorWhenAmountEmpty() {
        //Pre-conditions: User is created and logged in, at least 2 bank account were created, one of them with balance > 0
        UserSteps user = createRandomUser();
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(randomAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance().doubleValue();
        var initialReceiverBalance = receiverBankAccount.getBalance().doubleValue();

        //Test steps via UI
        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientName(receiverBankAccount.getAccountNumber())
                .clickConfirm()
                .cickSendTransfer()
                .checkAlertAndAccept(BankAlert.FILL_ALL_FIELDS.getMessage());

        //Check Accounts drop-down: accounts balance weren't updated
        var transferPage = new TransferPage();
        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialReceiverBalance));

        //Check via API that accounts balance weren't updated
        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance().doubleValue();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance().doubleValue();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

    @Test
    public void errorWhenNoConfirmation() {
        //Pre-conditions: User is created and logged in, at least 2 bank account were created, one of them with balance > 0
        UserSteps user = createRandomUser();
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(randomAmount);
        var receiverBankAccount = user.createBankAccount();

        var initialSenderBalance = senderBankAccount.getBalance().doubleValue();
        var initialReceiverBalance = receiverBankAccount.getBalance().doubleValue();

        //Test steps via UI
        openUserDashboard(user.getName(), user.getPass()).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientName(receiverBankAccount.getAccountNumber())
                .enterAmount(randomAmount.doubleValue())
                .cickSendTransfer()
                .checkAlertAndAccept(BankAlert.FILL_ALL_FIELDS.getMessage());

        //Check Accounts drop-down: accounts balance weren't updated
        var transferPage = new TransferPage();
        assertThat(transferPage.getBankAccountText(senderBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialSenderBalance));
        assertThat(transferPage.getBankAccountText(receiverBankAccount.getAccountNumber()))
                .contains(String.valueOf(initialReceiverBalance));

        //Check via API that accounts balance weren't updated
        var accountsApi = user.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance().doubleValue();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance().doubleValue();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

    @Test
    public void userCanRepeatTransfer(){
        //Pre-conditions: User is created and logged in, at least 2 bank account were created,
        // one of them with balance > 0. Valid Transfer between accounts was made.
        UserSteps user = createRandomUser();
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(randomAmount);
        var receiverBankAccount = user.createBankAccount();
        //randomAmount is divided by 2 so that this transfer later can be repeated without the risk of exceeding sender balance
        var randomTransferAmount = RandomDataGenerator.getRandomAmount(0.00, randomAmount.doubleValue()/2);
        user.transfer(senderBankAccount.getId(), receiverBankAccount.getId(), randomTransferAmount);

        var allAccounts = user.getAllBankAccounts();
        var iniSenderBalance = allAccounts.getAccount(senderBankAccount.getId()).getBalance();
        var iniReceiverBalance = allAccounts.getAccount(receiverBankAccount.getId()).getBalance();

        //Test steps via UI
        var transactionsPage = openUserDashboard(user.getName(), user.getPass())
                .goToTransfer()
                .clickTransferAgain();

        var transactionToRepeat = transactionsPage.getTransactions().stream()
                .filter(t -> t.getText().contains("OUT") && t.getText().contains(String.valueOf(randomTransferAmount)))
                .findFirst().get();

        var transactionModel = transactionsPage.repeatTransaction(transactionToRepeat);

        //Assert that Recipient Account and Amount fields are pre-filled with selected transaction info
        assertThat(Integer.valueOf(transactionModel.getReceiverAccount()))
                .isEqualTo(receiverBankAccount.getId());
        assertThat(transactionModel.getPrefilledAmount()).isEqualTo(randomTransferAmount);

        //repeat the transaction
        transactionModel.selectSenderAccount(senderBankAccount.getAccountNumber())
                .confirm()
                .clickSendTransfer()
                        .checkAlertAndAccept(String.format("Transfer of $%s successful from Account %s to %s!",
                                randomTransferAmount, senderBankAccount.getId(), receiverBankAccount.getId()));

        //Check via API that sender balance was reduced and receiver balance was increased with transferred amount.
        var allAccountsAfterRepeat = user.getAllBankAccounts();
        var senderBalance = allAccountsAfterRepeat.getAccount(senderBankAccount.getId()).getBalance();
        var receiverBalance = allAccountsAfterRepeat.getAccount(receiverBankAccount.getId()).getBalance();
        assertThat(senderBalance).isEqualTo(iniSenderBalance.subtract(randomTransferAmount));
        assertThat(receiverBalance).isEqualTo(iniReceiverBalance.add(randomTransferAmount));
    }

    @Test
    public void sendTransferButtonDisabledWhenAccountNotSelected(){
        //Pre-conditions: User is created and logged in, at least 2 bank account were created,
        // one of them with balance > 0. Valid Transfer between accounts was made
        UserSteps user = createRandomUser();
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(randomAmount);
        var receiverBankAccount = user.createBankAccount();
        //randomAmount is divided by 2 so that this transfer later can be repeated without the risk of exceeding sender balance
        var randomTransferAmount = RandomDataGenerator.getRandomAmount(0.00, randomAmount.doubleValue()/2);
        user.transfer(senderBankAccount.getId(), receiverBankAccount.getId(), randomTransferAmount);

        //Test steps via UI
        var transactionsPage = openUserDashboard(user.getName(), user.getPass())
                .goToTransfer()
                .clickTransferAgain();

        var transactionToRepeat = transactionsPage.getTransactions().stream()
                .filter(t -> t.getText().contains("OUT") && t.getText().contains(String.valueOf(randomTransferAmount)))
                .findFirst().get();

        var transactionModel = transactionsPage
                .repeatTransaction(transactionToRepeat)
                .confirm();

        assertThat(transactionModel.getSendTransferButton().isEnabled()).isFalse();
    }

    @Test
    public void sendTransferButtonDisabledWhenNotConfirmed(){
        //Pre-conditions: User is created and logged in, at least 2 bank account were created,
        // one of them with balance > 0. Valid Transfer between accounts was made
        UserSteps user = createRandomUser();
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        var senderBankAccount = user.createAccountWithBalance(randomAmount);
        var receiverBankAccount = user.createBankAccount();
        //randomAmount is divided by 2 so that this transfer later can be repeated without the risk of exceeding sender balance
        var randomTransferAmount = RandomDataGenerator.getRandomAmount(0.00, randomAmount.doubleValue()/2);
        user.transfer(senderBankAccount.getId(), receiverBankAccount.getId(), randomTransferAmount);

        //Test steps via UI
        var transactionsPage = openUserDashboard(user.getName(), user.getPass())
                .goToTransfer()
                .clickTransferAgain();

        var transactionToRepeat = transactionsPage.getTransactions().stream()
                .filter(t -> t.getText().contains("OUT") && t.getText().contains(String.valueOf(randomTransferAmount)))
                .findFirst().get();

        var transactionModel = transactionsPage
                .repeatTransaction(transactionToRepeat)
                .selectSenderAccount(senderBankAccount.getAccountNumber());

        assertThat(transactionModel.getSendTransferButton().isEnabled()).isFalse();
    }
}
