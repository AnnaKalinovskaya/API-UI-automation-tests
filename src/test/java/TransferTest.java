import generators.RandomDataGenerator;
import models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.DepositRequest;
import requests.TransferRequest;
import specs.RequestSpecs;
import specs.ResponseSpecs;

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
        BankAccountModel senderBankAccount = createBankAccount();
        BankAccountModel receiverBankAccount = createBankAccount();

        for (int i = 0; i < 3; i ++) {
            new DepositRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns200())
                    .post(DepositRequestModel
                                    .builder()
                                    .id(senderBankAccount.getId())
                                    .balance(BigDecimal.valueOf(5000))
                                    .build());
        }

        //get initial values on both accounts
        BigDecimal initialSenderBalance = getBankAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal initialReceiverBalance = getBankAccount(receiverBankAccount.getId())
                .getBalance();

        BigDecimal expectedFinalSenderBalance = initialSenderBalance.subtract(amountToTransfer);
        BigDecimal expectedFinalReceiverBalance = initialReceiverBalance.add(amountToTransfer);

        //send request to transfer valid amount
        TransferResponseModel responseBody = new TransferRequest(
                RequestSpecs.authAsUserSpec(userName, userPass),
                ResponseSpecs.returns200())
                .post(TransferRequestModel
                        .builder()
                        .amount(amountToTransfer)
                        .senderAccountId(senderBankAccount.getId())
                        .receiverAccountId(receiverBankAccount.getId())
                        .build())
                .extract().body().as(TransferResponseModel.class);

        softly.assertThat(responseBody.getSenderAccountId())
                .withFailMessage("Sender account id: " + responseBody.getSenderAccountId())
                .isEqualTo(senderBankAccount.getId());
        softly.assertThat(responseBody.getReceiverAccountId())
                .withFailMessage("Receiver account id: " + responseBody.getReceiverAccountId())
                .isEqualTo(receiverBankAccount.getId());
        softly.assertThat(responseBody.getAmount())
                .withFailMessage("Transfer amount: " + responseBody.getAmount())
                .isEqualTo(amountToTransfer);
        softly.assertThat(responseBody.getMessage())
                .withFailMessage("Response message for transfer request: " + responseBody.getMessage())
                .isEqualTo("Transfer successful");

        //check balance state after transfer of sender and receiver
        BigDecimal updatedSenderBalance = getBankAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal updatedReceiverBalance = getBankAccount(receiverBankAccount.getId())
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
                Arguments.of(10000.00),
                Arguments.of(Double.MIN_VALUE)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTransferAmount")
    public void userCanNotTransferInvalidAmount(double invalidValue){
        BigDecimal amountToTransfer = BigDecimal.valueOf(invalidValue).setScale(2, RoundingMode.HALF_UP);
        //pre-conditions: create account with initial balance on sender account
        BankAccountModel senderBankAccount = createBankAccount();
        BankAccountModel receiverBankAccount = createBankAccount();

        for (int i = 0; i < 3; i ++) {
            new DepositRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns200())
                    .post(DepositRequestModel
                            .builder()
                            .id(senderBankAccount.getId())
                            .balance(BigDecimal.valueOf(5000))
                            .build());
        }

        //get initial values in both accounts
        BigDecimal initialSenderBalance = getBankAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal initialReceiverBalance = getBankAccount(receiverBankAccount.getId())
                .getBalance();

        //send request to transfer invalid amount
        new TransferRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns400())
                .post(TransferRequestModel
                        .builder()
                        .amount(amountToTransfer)
                        .senderAccountId(senderBankAccount.getId())
                        .receiverAccountId(receiverBankAccount.getId())
                        .build());

        //check balance state after invalid transfer
        BigDecimal senderBalanceAfterTransfer = getBankAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal receiverBalanceAfterTransfer = getBankAccount(receiverBankAccount.getId())
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
        BankAccountModel senderBankAccount = createBankAccount();
        BankAccountModel receiverBankAccount =createBankAccount();

        new DepositRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns200())
                .post(DepositRequestModel
                        .builder()
                        .id(senderBankAccount.getId())
                        .balance(amount)
                        .build());

        //check balance before transfer accounts
        BigDecimal initialSenderBalance = getBankAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal initialReceiverBalance = getBankAccount(receiverBankAccount.getId())
                .getBalance();

        //send request to transfer all money
        TransferResponseModel responseBody = new TransferRequest(
                RequestSpecs.authAsUserSpec(userName, userPass),
                ResponseSpecs.returns200())
                .post(TransferRequestModel
                        .builder()
                        .amount(amount)
                        .senderAccountId(senderBankAccount.getId())
                        .receiverAccountId(receiverBankAccount.getId())
                        .build())
                .extract().body().as(TransferResponseModel.class);

        //assert all response fields
        softly.assertThat(responseBody.getSenderAccountId())
                .withFailMessage("Sender account id. " +
                        "Expected: %s; Actual: %s.", senderBankAccount.getId(), responseBody.getSenderAccountId())
                .isEqualTo(senderBankAccount.getId());
        softly.assertThat(responseBody.getReceiverAccountId())
                .withFailMessage("Receiver account id. " +
                        "Expected: %s; Actual: %s.", receiverBankAccount.getId(), responseBody.getReceiverAccountId())
                .isEqualTo(receiverBankAccount.getId());
        softly.assertThat(responseBody.getAmount())
                .withFailMessage("Transfer amount. " +
                        "Expected: %s; Actual: %s.", amount, responseBody.getAmount())
                .isEqualTo(amount);
        softly.assertThat(responseBody.getMessage())
                .withFailMessage("Actual response message for transfer request: " +
                        responseBody.getMessage())
                .isEqualTo("Transfer successful");

        //check balance state after transfer on both accounts
        BigDecimal senderBalanceAfterTransfer = getBankAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal receiverBalanceAfterTransfer = getBankAccount(receiverBankAccount.getId())
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
        BankAccountModel senderBankAccount = createBankAccount();
        BigDecimal randomDepositAmount = RandomDataGenerator.getRandomDepositAmount();
        new DepositRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns200())
                .post(DepositRequestModel
                        .builder()
                        .id(senderBankAccount.getId())
                        .balance(randomDepositAmount)
                        .build());

        BankAccountModel receiverBankAccount = createBankAccount();
        BigDecimal initialReceiverBalance = getBankAccount(receiverBankAccount.getId())
                .getBalance();

        //send transfer request with random valid amount > than balance
        BigDecimal randomTransferAmount = RandomDataGenerator
                .getRandomAmount(randomDepositAmount.doubleValue() + 0.01, 10000);
        new TransferRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns400())
                .post(TransferRequestModel
                        .builder()
                        .amount(randomTransferAmount)
                        .senderAccountId(senderBankAccount.getId())
                        .receiverAccountId(receiverBankAccount.getId())
                        .build());

        //assert that balance of sender and receiver hasn't changed
        BigDecimal senderBalanceAfterTransfer = getBankAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal receiverBalanceAfterTransfer = getBankAccount(receiverBankAccount.getId())
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

        BankAccountModel receiverBankAccount = createBankAccount();
        BigDecimal receiverBalanceBeforeRequest = getBankAccount(receiverBankAccount.getId())
                .getBalance();

        new TransferRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns403())
                .post(TransferRequestModel
                        .builder()
                        .amount(randomValidTransferAmount)
                        .senderAccountId(99999)
                        .receiverAccountId(receiverBankAccount.getId())
                        .build());

        BigDecimal receiverBalanceAfterRequest = getBankAccount(receiverBankAccount.getId())
                .getBalance();

        softly.assertThat(receiverBalanceAfterRequest)
                .withFailMessage( "Receiver balance changed after request: " + receiverBalanceAfterRequest)
                .isEqualTo(receiverBalanceBeforeRequest);
    }

    @Test
    public void userCanNotTransferMoneyToNonExistingAccount(){
        BigDecimal randomValidTransferAmount = RandomDataGenerator.getRandomDepositAmount();

        BankAccountModel senderBankAccount = createBankAccount();
        BigDecimal senderBalanceBeforeRequest = getBankAccount(senderBankAccount.getId())
                .getBalance();

        new TransferRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns400())
                .post(TransferRequestModel
                        .builder()
                        .amount(randomValidTransferAmount)
                        .senderAccountId(senderBankAccount.getId())
                        .receiverAccountId(99999)
                        .build());

        BigDecimal senderBalanceAfterRequest = getBankAccount(senderBankAccount.getId())
                .getBalance();

        softly.assertThat(senderBalanceAfterRequest)
                .withFailMessage( "Sender balance changed after request: " + senderBalanceAfterRequest)
                .isEqualTo(senderBalanceBeforeRequest);
    }

    @Test
    public void senderAndReceiverAccountCannotBeTheSame() {
        //pre-conditions: create account with balance required for test
        BankAccountModel senderBankAccount = createBankAccount();
        BigDecimal randomAmount = RandomDataGenerator.getRandomDepositAmount();
        new DepositRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns200())
                .post(DepositRequestModel
                .builder()
                .id(senderBankAccount.getId())
                .balance(randomAmount)
                .build());

        BigDecimal senderBalanceBeforeRequest = getBankAccount(senderBankAccount.getId())
                .getBalance();

        new TransferRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns400())
                .post(TransferRequestModel
                        .builder()
                        .amount(randomAmount)
                        .senderAccountId(senderBankAccount.getId())
                        .receiverAccountId(senderBankAccount.getId())
                        .build());

        BigDecimal senderBalanceAfterRequest = getBankAccount(senderBankAccount.getId())
                .getBalance();

        softly.assertThat(senderBalanceAfterRequest)
                .withFailMessage("Sender balance changed after request: " + senderBalanceAfterRequest)
                .isEqualTo(senderBalanceBeforeRequest);
    }


    @Test
    public void transferMoneyConcurrentlyWithSameAccounts() {
        //pre-conditions: create account and deposit amount of at least 500 000
        BankAccountModel senderBankAccount = createBankAccount();
        BigDecimal depositAmount = BigDecimal.valueOf(5000);
        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<?> depositTask = executorService.submit(() -> {
                        for (int i = 1; i <= 100; i++) {
                            new DepositRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns200())
                                    .post(DepositRequestModel
                                            .builder()
                                            .id(senderBankAccount.getId())
                                            .balance(depositAmount)
                                            .build());
                        }
            });

        //wait until all deposit requests are sent
        try {
            depositTask.get();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
        }

        BankAccountModel receiverBankAccount = createBankAccount();

        //check balance before requests
        BigDecimal senderBalanceBEFORETransfer = getBankAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal receiverBalanceBEFORETransfer = getBankAccount(receiverBankAccount.getId())
                .getBalance();

        //send 50 transfer requests
        BigDecimal transferAmount = BigDecimal.valueOf(10000);
        Future<?> transferTask = executorService.submit(() -> {
            for (int i = 1; i <= 50; i++){
                new TransferRequest(RequestSpecs.authAsUserSpec(userName, userPass), ResponseSpecs.returns200())
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
        BigDecimal senderBalanceAFTERTransfer = getBankAccount(senderBankAccount.getId())
                .getBalance();
        BigDecimal receiverBalanceAFTERTransfer = getBankAccount(receiverBankAccount.getId())
                .getBalance();

        softly.assertThat(senderBalanceAFTERTransfer)
                .withFailMessage("Sender balance was updated incorrectly: " + senderBalanceAFTERTransfer)
                .isEqualTo(senderBalanceBEFORETransfer.subtract(totalTransferAmount));
        softly.assertThat(receiverBalanceAFTERTransfer)
                .withFailMessage("Receiver balance was updated incorrectly: " + receiverBalanceAFTERTransfer)
                .isEqualTo(receiverBalanceBEFORETransfer.add(totalTransferAmount));
    }
}
