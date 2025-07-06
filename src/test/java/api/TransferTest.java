package api;

import api.generators.RandomDataGenerator;
import api.models.BankAccountModel;
import api.models.TransferRequestModel;
import api.models.TransferResponseModel;
import api.models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.skelethon.requests.CrudRequester;
import api.skelethon.requests.Endpoint;
import api.skelethon.requests.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class TransferTest extends BaseTest{

    @ParameterizedTest
    @MethodSource("validTransferAmount")
    public void userCanTransferValidAmount(double validValue){
        BigDecimal amountToTransfer = BigDecimal.valueOf(validValue).setScale(2, RoundingMode.HALF_UP);
        //pre-conditions: create account with initial balance on sender account
        BankAccountModel senderBankAccount = user.createAccountWithBalance(new BigDecimal(15000));
        BankAccountModel receiverBankAccount = user.createBankAccount();

        //get initial values on both accounts
        var allAccounts = user.getAllBankAccounts();
        BigDecimal initialSenderBalance = allAccounts.getAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal initialReceiverBalance = allAccounts.getAccount(receiverBankAccount.getId())
                .getBalance();

        BigDecimal expectedFinalSenderBalance = initialSenderBalance.subtract(amountToTransfer);
        BigDecimal expectedFinalReceiverBalance = initialReceiverBalance.add(amountToTransfer);

        //send request to transfer valid amount
        var transferRequestBody = TransferRequestModel
                .builder()
                .amount(amountToTransfer)
                .senderAccountId(senderBankAccount.getId())
                .receiverAccountId(receiverBankAccount.getId())
                .build();

        TransferResponseModel responseBody = new CrudRequester<TransferResponseModel>(
                RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.TRANSFER, ResponseSpecs.returns200())
                .post(transferRequestBody);

        ModelAssertions.assertThatModels(transferRequestBody, responseBody).match();
        softly.assertThat(responseBody.getMessage())
                .withFailMessage("Response message for transfer request: " + responseBody.getMessage())
                .isEqualTo("Transfer successful");

        //check balance state after transfer of sender and receiver
        var allAccountsUpdated = user.getAllBankAccounts();
        BigDecimal updatedSenderBalance = allAccountsUpdated.getAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal updatedReceiverBalance = allAccountsUpdated.getAccount(receiverBankAccount.getId())
                .getBalance();

        softly.assertThat(updatedSenderBalance)
                .withFailMessage("Sender balance updated: " + updatedSenderBalance)
                .isEqualTo(expectedFinalSenderBalance);
        softly.assertThat(updatedReceiverBalance)
                .withFailMessage("Receiver balance updated: " + updatedReceiverBalance)
                .isEqualTo(expectedFinalReceiverBalance);
    }

    public static Stream<Arguments> validTransferAmount(){
        return Stream.of(
                Arguments.of(0.01),
                Arguments.of(9999.99),
                Arguments.of(10000.00)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTransferAmount")
    public void userCanNotTransferInvalidAmount(double invalidValue){
        BigDecimal amountToTransfer = BigDecimal.valueOf(invalidValue).setScale(2, RoundingMode.HALF_UP);
        //pre-conditions: create account with initial balance on sender account
        BankAccountModel senderBankAccount = user.createAccountWithBalance(new BigDecimal(15000));
        BankAccountModel receiverBankAccount = user.createBankAccount();

        //get initial values in both accounts
        var allAccounts = user.getAllBankAccounts();
        BigDecimal initialSenderBalance = allAccounts.getAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal initialReceiverBalance = allAccounts.getAccount(receiverBankAccount.getId())
                .getBalance();

        //send request to transfer invalid amount
        new ValidatableCrudRequester(RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.TRANSFER, ResponseSpecs.returns400())
                .post(TransferRequestModel
                        .builder()
                        .amount(amountToTransfer)
                        .senderAccountId(senderBankAccount.getId())
                        .receiverAccountId(receiverBankAccount.getId())
                        .build());

        //check balance state after invalid transfer
        var allAccountsUpdated = user.getAllBankAccounts();
        BigDecimal senderBalanceAfterTransfer = allAccountsUpdated.getAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal receiverBalanceAfterTransfer = allAccountsUpdated.getAccount(receiverBankAccount.getId())
                .getBalance();

        softly.assertThat(senderBalanceAfterTransfer)
                .withFailMessage("Sender balance after invalid transfer. " + senderBalanceAfterTransfer)
                .isEqualTo(initialSenderBalance);
        softly.assertThat(receiverBalanceAfterTransfer)
                .withFailMessage("Receiver balance after invalid transfer: " + receiverBalanceAfterTransfer)
                .isEqualTo(initialReceiverBalance);
    }

    public static Stream<Arguments> invalidTransferAmount(){
        return Stream.of(
                Arguments.of(-0.01),
                Arguments.of(0.00),
                Arguments.of(10000.01),
                Arguments.of(Double.MAX_VALUE)
        );
    }

    @Test
    public void userCanTransferAllMoneyFromAccount(){
        BigDecimal amount = BigDecimal.valueOf(4000).setScale(2, RoundingMode.HALF_UP);
        //pre-conditions: create new bank account and deposit initial value
        BankAccountModel senderBankAccount = user.createAccountWithBalance(amount);
        BankAccountModel receiverBankAccount = user.createBankAccount();

        //check balance before transfer accounts
        var allAccounts = user.getAllBankAccounts();
        BigDecimal initialSenderBalance = allAccounts.getAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal initialReceiverBalance = allAccounts.getAccount(receiverBankAccount.getId())
                .getBalance();

        //send request to transfer all money
        var transferRequestBody = TransferRequestModel
                .builder()
                .amount(amount)
                .senderAccountId(senderBankAccount.getId())
                .receiverAccountId(receiverBankAccount.getId())
                .build();
        TransferResponseModel responseBody = new CrudRequester<TransferResponseModel>(RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.TRANSFER, ResponseSpecs.returns200())
                .post(transferRequestBody);

        //assert all response fields
        ModelAssertions.assertThatModels(transferRequestBody, responseBody).match();
        softly.assertThat(responseBody.getMessage())
                .withFailMessage("Actual response message for transfer request: " +
                        responseBody.getMessage())
                .isEqualTo("Transfer successful");

        //check balance state after transfer on both accounts
        var allAccountsUpdated = user.getAllBankAccounts();
        BigDecimal senderBalanceAfterTransfer = allAccountsUpdated.getAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal receiverBalanceAfterTransfer = allAccountsUpdated.getAccount(receiverBankAccount.getId())
                .getBalance();

        softly.assertThat(receiverBalanceAfterTransfer)
                .withFailMessage("Receiver balance after transfer update: " + receiverBalanceAfterTransfer)
                .isEqualTo(initialReceiverBalance.add(amount));
        softly.assertThat(senderBalanceAfterTransfer)
                .withFailMessage("Sender balance after transfer update: " +  senderBalanceAfterTransfer)
                .isEqualTo(initialSenderBalance.subtract(amount));
    }

    @Test
    public void userCanNotTransferMoreMoneyThanCurrentBalance(){
        //pre-conditions: create account with initial balance on sender account
        BigDecimal randomDepositAmount = RandomDataGenerator.getRandomDepositAmount();
        BankAccountModel senderBankAccount = user.createAccountWithBalance(randomDepositAmount);

        BankAccountModel receiverBankAccount = user.createBankAccount();
        BigDecimal initialReceiverBalance = user.getAllBankAccounts().getAccount(receiverBankAccount.getId())
                .getBalance();

        //send transfer request with random valid amount > than balance
        BigDecimal randomTransferAmount = RandomDataGenerator
                .getRandomAmount(randomDepositAmount.doubleValue() + 0.01, 10000);

        new ValidatableCrudRequester(RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.TRANSFER, ResponseSpecs.returns400())
                .post(TransferRequestModel
                        .builder()
                        .amount(randomTransferAmount)
                        .senderAccountId(senderBankAccount.getId())
                        .receiverAccountId(receiverBankAccount.getId())
                        .build());

        //assert that balance of sender and receiver hasn't changed
        var allAccounts = user.getAllBankAccounts();
        BigDecimal senderBalanceAfterTransfer = allAccounts.getAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal receiverBalanceAfterTransfer = allAccounts.getAccount(receiverBankAccount.getId())
                .getBalance();

        softly.assertThat(receiverBalanceAfterTransfer)
                .withFailMessage( "Sender balance has changed after request: " + receiverBalanceAfterTransfer)
                .isEqualTo(initialReceiverBalance);
        softly.assertThat(senderBalanceAfterTransfer)
                .withFailMessage( "Sender balance has changed after request: " + senderBalanceAfterTransfer)
                .isEqualTo(randomDepositAmount);
    }

    @Test
    public void userCanNotTransferMoneyFromNonExistingAccount(){
        BigDecimal randomValidTransferAmount = RandomDataGenerator.getRandomDepositAmount();

        BankAccountModel receiverBankAccount = user.createBankAccount();
        BigDecimal receiverBalanceBeforeRequest = user.getAllBankAccounts()
                .getAccount(receiverBankAccount.getId())
                .getBalance();

        new ValidatableCrudRequester(RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.TRANSFER, ResponseSpecs.returns403())
                .post(TransferRequestModel
                        .builder()
                        .amount(randomValidTransferAmount)
                        .senderAccountId(99999)
                        .receiverAccountId(receiverBankAccount.getId())
                        .build());

        BigDecimal receiverBalanceAfterRequest = user.getAllBankAccounts()
                .getAccount(receiverBankAccount.getId())
                .getBalance();

        softly.assertThat(receiverBalanceAfterRequest)
                .withFailMessage( "Receiver balance changed after request: " + receiverBalanceAfterRequest)
                .isEqualTo(receiverBalanceBeforeRequest);
    }

    @Test
    public void userCanNotTransferMoneyToNonExistingAccount(){
        BigDecimal randomValidTransferAmount = RandomDataGenerator.getRandomDepositAmount();

        BankAccountModel senderBankAccount = user.createBankAccount();
        BigDecimal senderBalanceBeforeRequest = user.getAllBankAccounts()
                .getAccount(senderBankAccount.getId())
                .getBalance();

        new ValidatableCrudRequester(RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.TRANSFER, ResponseSpecs.returns400())
                .post(TransferRequestModel
                        .builder()
                        .amount(randomValidTransferAmount)
                        .senderAccountId(senderBankAccount.getId())
                        .receiverAccountId(99999)
                        .build());

        BigDecimal senderBalanceAfterRequest = user.getAllBankAccounts()
                .getAccount(senderBankAccount.getId())
                .getBalance();

        softly.assertThat(senderBalanceAfterRequest)
                .withFailMessage( "Sender balance changed after request: " + senderBalanceAfterRequest)
                .isEqualTo(senderBalanceBeforeRequest);
    }

    @Test
    public void senderAndReceiverAccountCannotBeTheSame() {
        //pre-conditions: create account with balance required for test
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        BankAccountModel senderBankAccount = user.createAccountWithBalance(randomAmount);

        BigDecimal senderBalanceBeforeRequest = user.getAllBankAccounts()
                .getAccount(senderBankAccount.getId())
                .getBalance();

        new ValidatableCrudRequester(RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.TRANSFER, ResponseSpecs.returns400())
                .post(TransferRequestModel
                        .builder()
                        .amount(randomAmount)
                        .senderAccountId(senderBankAccount.getId())
                        .receiverAccountId(senderBankAccount.getId())
                        .build());

        BigDecimal senderBalanceAfterRequest = user.getAllBankAccounts()
                .getAccount(senderBankAccount.getId())
                .getBalance();

        softly.assertThat(senderBalanceAfterRequest)
                .withFailMessage("Sender balance changed after request: " + senderBalanceAfterRequest)
                .isEqualTo(senderBalanceBeforeRequest);
    }


    @Test
    public void transferMoneyConcurrentlyWithSameAccounts() {
        //pre-conditions: create account and deposit amount of at least 500 000
        BankAccountModel senderBankAccount = user.createAccountWithBalance(new BigDecimal(500000));
        BankAccountModel receiverBankAccount = user.createBankAccount();

        //check balance before requests
        var allAccounts = user.getAllBankAccounts();
        BigDecimal senderBalanceBEFORETransfer = allAccounts.getAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal receiverBalanceBEFORETransfer = allAccounts.getAccount(receiverBankAccount.getId())
                .getBalance();

        //send 50 transfer requests
        BigDecimal transferAmount = BigDecimal.valueOf(10000);
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<?> transferTask = executorService.submit(() -> {
            for (int i = 1; i <= 50; i++){
                new ValidatableCrudRequester(RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                        Endpoint.TRANSFER, ResponseSpecs.returns200())
                        .post(TransferRequestModel
                                .builder()
                                .amount(transferAmount)
                                .senderAccountId(senderBankAccount.getId())
                                .receiverAccountId(receiverBankAccount.getId())
                                .build());
            }
        });

        //wait until all transfer requests are sent
        try {
            transferTask.get();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        
        executorService.shutdown();
        try {
            if (executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ie){
            executorService.shutdownNow();
        }

        BigDecimal totalTransferAmount = transferAmount.multiply(BigDecimal.valueOf(50));

        //assert that sender and receiver balances were updated accordingly
        var allAccountsUpdated = user.getAllBankAccounts();
        BigDecimal senderBalanceAFTERTransfer = allAccountsUpdated.getAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal receiverBalanceAFTERTransfer = allAccountsUpdated.getAccount(receiverBankAccount.getId())
                .getBalance();

        softly.assertThat(senderBalanceAFTERTransfer)
                .withFailMessage("Sender balance was updated incorrectly: " + senderBalanceAFTERTransfer)
                .isEqualTo(senderBalanceBEFORETransfer.subtract(totalTransferAmount));
        softly.assertThat(receiverBalanceAFTERTransfer)
                .withFailMessage("Receiver balance was updated incorrectly: " + receiverBalanceAFTERTransfer)
                .isEqualTo(receiverBalanceBEFORETransfer.add(totalTransferAmount));
    }
}
