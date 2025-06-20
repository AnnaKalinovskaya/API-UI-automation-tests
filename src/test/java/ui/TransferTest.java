package ui;

import generators.RandomDataGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pages.TransferPage;
import skelethon.steps.AdminSteps;
import skelethon.steps.UserSteps;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(doubles = {500.00})
    public void depositValidAmount(double validAmount){
        String userName = RandomDataGenerator.getRandomUserName();
        String pass = RandomDataGenerator.getRandomPass();
        AdminSteps.createUser(userName, pass);

        var userSteps = new UserSteps(userName, pass);
        var senderBankAccount = userSteps.createBankAccount();
        userSteps.deposit(senderBankAccount.getId(), RandomDataGenerator.getRandomDepositAmount());
        var receiverBankAccount = userSteps.createBankAccount();
        var initialSenderBalance = senderBankAccount.getBalance();
        var initialReceiverBalance = receiverBankAccount.getBalance();

        openUserDashboard(userName, pass).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientName(userSteps.getCustomerProfile().getName())

                .enterRecipientAccount(receiverBankAccount.getAccountNumber())
                .enterAmount(validAmount)
                .confirm()
                .sendTransfer();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains(String.format("Successfully transferred %s to account %s!",
                validAmount, receiverBankAccount.getAccountNumber()));

        String senderAccountAfterTransfer = new TransferPage().getAccountOptions()
                .stream().filter(option -> option.getText().contains(senderBankAccount.getAccountNumber()))
                .findFirst().get().getText();
        String receiverAccountAfterTransfer = new TransferPage().getAccountOptions()
                .stream().filter(option -> option.getText().contains(receiverBankAccount.getAccountNumber()))
                .findFirst().get().getText();

        var expectedSenderBalance = initialSenderBalance.doubleValue() - validAmount;
        var expectedReceiverBalance = initialReceiverBalance.doubleValue() + validAmount;
        assertThat(senderAccountAfterTransfer).contains(String.valueOf(expectedSenderBalance));
        assertThat(receiverAccountAfterTransfer).contains(String.valueOf(expectedReceiverBalance));

        var accountsApi = userSteps.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance().doubleValue();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance().doubleValue();
        assertThat(senderBalanceApi).isEqualTo(expectedSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(expectedReceiverBalance);
    }

    @ParameterizedTest
    @ValueSource(doubles = {15000.00})
    public void depositInvalidAmount(double invalidAmount){
        String userName = RandomDataGenerator.getRandomUserName();
        String pass = RandomDataGenerator.getRandomPass();
        AdminSteps.createUser(userName, pass);

        var userSteps = new UserSteps(userName, pass);
        var senderBankAccount = userSteps.createBankAccount();
        userSteps.deposit(senderBankAccount.getId(), RandomDataGenerator.getRandomDepositAmount());
        var receiverBankAccount = userSteps.createBankAccount();
        var initialSenderBalance = senderBankAccount.getBalance().doubleValue();
        var initialReceiverBalance = receiverBankAccount.getBalance().doubleValue();

        openUserDashboard(userName, pass).goToTransfer()
                .selectSenderAccount(senderBankAccount.getAccountNumber())
                .enterRecipientName(userSteps.getCustomerProfile().getName())

                .enterRecipientAccount(receiverBankAccount.getAccountNumber())
                .enterAmount(invalidAmount)
                .confirm()
                .sendTransfer();

        String alertText = confirmAlertAndGetText();
        assertThat(alertText).contains("Invalid transfer: insufficient funds or invalid accounts");

        String senderAccountAfterTransfer = new TransferPage().getAccountOptions()
                .stream().filter(option -> option.getText().contains(senderBankAccount.getAccountNumber()))
                .findFirst().get().getText();
        String receiverAccountAfterTransfer = new TransferPage().getAccountOptions()
                .stream().filter(option -> option.getText().contains(receiverBankAccount.getAccountNumber()))
                .findFirst().get().getText();

        assertThat(senderAccountAfterTransfer).contains(String.valueOf(initialSenderBalance));
        assertThat(receiverAccountAfterTransfer).contains(String.valueOf(initialReceiverBalance));

        var accountsApi = userSteps.getAllBankAccounts();
        var senderBalanceApi = accountsApi.getAccount(senderBankAccount.getId()).getBalance().doubleValue();
        var receiverBalanceApi = accountsApi.getAccount(receiverBankAccount.getId()).getBalance().doubleValue();
        assertThat(senderBalanceApi).isEqualTo(initialSenderBalance);
        assertThat(receiverBalanceApi).isEqualTo(initialReceiverBalance);
    }

}
