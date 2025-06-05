import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class TransferTest {

    private static User user;

    @BeforeAll
    public static void setup(){
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));

        user = Admin.getInstance().createUser("hanna", "hannaPass1!");
    }

    @ParameterizedTest
    @MethodSource("validTransferAmount")
    public void userCanTransferValidAmount(double validValue){
        //pre-conditions: create account with initial balance on sender account
        BankAccount senderBankAccount = user.createBankAccount();
        BankAccount receiverBankAccount = user.createBankAccount();
        for (int i = 0; i < 3; i ++) {
            BankRequests.depositRequest(user, senderBankAccount.getId(), 5000);
        }

        //get initial values on both accounts
        BigDecimal initialReceiverBalance = user.getAccountBalance(receiverBankAccount.getId());
        BigDecimal initialSenderBalance = user.getAccountBalance(senderBankAccount.getId());

        BigDecimal expectedFinalSenderBalance = initialSenderBalance
                .subtract(BigDecimal.valueOf(validValue)
                        .setScale(2, RoundingMode.HALF_UP));

        BigDecimal expectedFinalReceiverBalance = initialReceiverBalance
                .add(BigDecimal.valueOf(validValue)
                        .setScale(2, RoundingMode.HALF_UP));

        //send request to transfer valid amount
        var responseBody = BankRequests.transferRequest(user, senderBankAccount.getId(), receiverBankAccount.getId(), validValue)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().body();

        Assertions.assertEquals(senderBankAccount.getId(), responseBody.jsonPath().getInt("senderAccountId"),
                "Sender account id:");
        Assertions.assertEquals(receiverBankAccount.getId(), responseBody.jsonPath().getInt("receiverAccountId"),
                "Receiver account id:");
        Assertions.assertEquals(BigDecimal.valueOf(validValue), BigDecimal.valueOf(responseBody.jsonPath().getDouble("amount")),
                "Transfer amount:");
        Assertions.assertEquals("Transfer successful", responseBody.jsonPath().getString("message"),
                "Response message for transfer request:");

        //check balance state after transfer of sender and receiver
        BigDecimal updatedReceiverBalance = user.getAccountBalance(receiverBankAccount.getId());
        BigDecimal updatedSenderBalance = user.getAccountBalance(senderBankAccount.getId());

        Assertions.assertEquals(expectedFinalSenderBalance, updatedSenderBalance,
                "Sender balance updated:");
        Assertions.assertEquals(expectedFinalReceiverBalance, updatedReceiverBalance,
                "Receiver balance updated:");
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
        //pre-conditions: create account with initial balance on sender account
        BankAccount senderBankAccount = user.createBankAccount();
        BankAccount receiverBankAccount = user.createBankAccount();
        for (int i = 0; i < 3; i ++) {
            BankRequests.depositRequest(user, senderBankAccount.getId(), 5000);
        }

        //get initial values in both accounts
        BigDecimal initReceiverBalance = user.getAccountBalance(receiverBankAccount.getId());
        BigDecimal initSenderBalance = user.getAccountBalance(senderBankAccount.getId());

        //send request to transfer invalid amount
        BankRequests.transferRequest(user, senderBankAccount.getId(), receiverBankAccount.getId(), invalidValue);
        BankRequests.transferRequest(user, senderBankAccount.getId(), receiverBankAccount.getId(), invalidValue)
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        //check balance state after invalid transfer
        BigDecimal receiverBalanceAfterTransfer = user.getAccountBalance(receiverBankAccount.getId());
        BigDecimal senderBalanceAfterTransfer = user.getAccountBalance(senderBankAccount.getId());

        Assertions.assertEquals(initSenderBalance, senderBalanceAfterTransfer,
                "Sender balance updated after invalid transfer:");
        Assertions.assertEquals(initReceiverBalance, receiverBalanceAfterTransfer,
                "Receiver balance updated after invalid transfer:");
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
        //pre-conditions: create new bank account and deposit initial value for transfer (max allowed)
        BankAccount senderBankAccount = user.createBankAccount();
        BankRequests.depositRequest(user, senderBankAccount.getId(), 5000);
        //pre-conditions: create account with initial balance on sender account
        BankAccount receiverBankAccount = user.createBankAccount();

        //check balance before transfer on receiver account
        BigDecimal receiverBalanceBeforeTransfer = user.getAccountBalance(receiverBankAccount.getId());

        //send request to transfer all money
        var responseBody = BankRequests.transferRequest(user, senderBankAccount.getId(), receiverBankAccount.getId(), 5000)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().body();

        //assert all response fields
        Assertions.assertEquals(senderBankAccount.getId(),
                responseBody.jsonPath().getInt("senderAccountId"),
                "Sender account id:");
        Assertions.assertEquals(receiverBankAccount.getId(),
                responseBody.jsonPath().getInt("receiverAccountId"),
                "Receiver account id:");
        Assertions.assertEquals(BigDecimal.valueOf(5000.00).setScale(2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(responseBody.jsonPath().getDouble("amount"))
                .setScale(2, RoundingMode.HALF_UP),
                "Transfer amount:");
        Assertions.assertEquals("Transfer successful",
                responseBody.jsonPath().getString("message"),
                "Response message for transfer request:");

        //check balance state after transfer on both accounts
        Assertions.assertEquals(receiverBalanceBeforeTransfer.add(BigDecimal.valueOf(5000.00)),
                user.getAccountBalance(receiverBankAccount.getId()),
                "Receiver balance after transfer update:");
        Assertions.assertEquals(BigDecimal.valueOf(0.00)
                        .setScale(2, RoundingMode.HALF_UP),
                user.getAccountBalance(senderBankAccount.getId()),
                "Receiver balance after transfer update:");
    }

    @Test
    public void userCanNotTransferMoreMoneyThanCurrentBalance(){
        //pre-conditions: create account with initial balance on sender account
        double random = new Random().nextDouble((5000.00 - 0.01) + 1) + 0.01;
        double randomSenderBalance = Math.round(random * 100.0) / 100.0;
        BankAccount senderBankAccount = user.createBankAccount();
        BankRequests.depositRequest(user, senderBankAccount.getId(), randomSenderBalance);

        BankAccount receiverBankAccount = user.createBankAccount();
        BigDecimal receiverBalanceBeforeRequest = user.getAccountBalance(receiverBankAccount.getId());

        //send transfer request with random valid amount > than balance
        double randomTransferAmount = new Random()
                .nextDouble((10000.00 - (randomSenderBalance + 0.01)) + 1)
                + (randomSenderBalance + 0.01);
        BankRequests.transferRequest(user, senderBankAccount.getId(), receiverBankAccount.getId(), randomTransferAmount)
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        //assert that balance of sender and receiver hasn't changed
        Assertions.assertEquals(BigDecimal.valueOf(randomSenderBalance)
                        .setScale(2, RoundingMode.HALF_UP),
                user.getAccountBalance(senderBankAccount.getId()),
                "Sender balance has changed after request: ");
        Assertions.assertEquals(receiverBalanceBeforeRequest,
                user.getAccountBalance(receiverBankAccount.getId()),
                "Receiver balance has changed after request");
    }

    @Test
    public void userCanNotTransferMoneyFromNonExistingAccount(){
        double randomValidTransferAmount = new Random()
                .nextDouble((10000.00 - 0.01) + 1) + 0.01;

        BankAccount receiverBankAccount = user.createBankAccount();
        BigDecimal receiverBalanceBeforeRequest = user.getAccountBalance(receiverBankAccount.getId());

        BankRequests.transferRequest(user, 99999, receiverBankAccount.getId(), randomValidTransferAmount)
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.SC_FORBIDDEN);

        Assertions.assertEquals(receiverBalanceBeforeRequest, user.getAccountBalance(receiverBankAccount.getId()),
                "Receiver balance changed after request:");
    }

    @Test
    public void userCanNotTransferMoneyToNonExistingAccount(){
        //pre-conditions: create account with balance required for test
        BankAccount senderBankAccount = user.createBankAccount();
        double randomValidAmount = new Random()
                .nextDouble((5000.00 - 0.01) + 1) + 0.01;
        BankRequests.depositRequest(user, senderBankAccount.getId(), randomValidAmount);

        BigDecimal senderBalanceBeforeRequest = user.getAccountBalance(senderBankAccount.getId());

        BankRequests.transferRequest(user, senderBankAccount.getId(), 99999, randomValidAmount)
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        Assertions.assertEquals(senderBalanceBeforeRequest,
                user.getAccountBalance(senderBankAccount.getId()),
                "Sender balance changed after request:");
    }

    @Test
    public void senderAndReceiverAccountCannotBeTheSame() {
        //pre-conditions: create account with balance required for test
        BankAccount senderBankAccount = user.createBankAccount();
        double randomValidAmount = new Random()
                .nextDouble((5000.00 - 0.01) + 1) + 0.01;
        BankRequests.depositRequest(user, senderBankAccount.getId(), randomValidAmount);

        BigDecimal senderBalanceBeforeRequest = user.getAccountBalance(senderBankAccount.getId());

        BankRequests.transferRequest(user, senderBankAccount.getId(), senderBankAccount.getId(), randomValidAmount)
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        Assertions.assertEquals(senderBalanceBeforeRequest, user.getAccountBalance(senderBankAccount.getId()),
                "Sender balance changed after request:");
    }


    @Test
    public void transferMoneyConcurrentlyWithSameAccounts() {
        //pre-conditions: create account and deposit amount of at least 500 000
        BankAccount senderBankAccount = user.createBankAccount();
        double senderDeposit = 5000;
        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<?> depositTask = executorService.submit(() -> {
                        for (int i = 1; i <= 100; i++) {
                            BankRequests.depositRequest(user, senderBankAccount.getId(), senderDeposit);
                        }
            });

        //wait until all deposit requests are sent
        try {
            depositTask.get();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getMessage());
        }

        BankAccount receiverBankAccount = user.createBankAccount();
        BigDecimal senderBalanceBEFORETransfer = user.getAccountBalance(senderBankAccount.getId());
        BigDecimal receiverBalanceBEFORETransfer = user.getAccountBalance(receiverBankAccount.getId());

        //send 50 transfer requests
        Future<?> transferTask = executorService.submit(() -> {
            for (int i = 1; i <= 50; i++){
                BankRequests.transferRequest(user, senderBankAccount.getId(), receiverBankAccount.getId(), 10000);
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

        BigDecimal totalTransferAmount = BigDecimal.valueOf(10000 * 50);

        //assert that sender and receiver balances were updated accordingly
        BigDecimal senderBalanceAFTERTransfer = user.getAccountBalance(senderBankAccount.getId());
        BigDecimal receiverBalanceAFTERTransfer = user.getAccountBalance(receiverBankAccount.getId());

        Assertions.assertEquals(senderBalanceBEFORETransfer.subtract(totalTransferAmount), senderBalanceAFTERTransfer,
                "Sender balance was updated incorrectly:");
        Assertions.assertEquals(receiverBalanceBEFORETransfer.add(totalTransferAmount), receiverBalanceAFTERTransfer,
                "Receiver balance was updated incorrectly:");
    }

    @AfterAll
    public static void deleteUser(){
        Admin.getInstance().deleteUser(user);
    }
}
