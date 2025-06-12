import models.BankAccountModel;
import models.DepositRequestModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.DepositRequest;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.*;
import java.util.stream.Stream;


public class DepositTest extends BaseTest{

    private static BankAccountModel bankAccount;

    @BeforeAll
    public static void prepareData(){
        bankAccount = createBankAccount();
    }

    @ParameterizedTest
    @MethodSource("validDepositAmount")
    public void userCanDepositValidAmount (double validAmount){
        BigDecimal depositAmount = BigDecimal.valueOf(validAmount).setScale(2, RoundingMode.HALF_UP);
        //get initial balance
        BigDecimal initialBalance = getBankAccount(bankAccount.getId()).getBalance();
        BigDecimal expectedBalance = initialBalance.add(depositAmount);

        //send deposit request
        BankAccountModel responseBody = new DepositRequest(RequestSpecs.authAsUserSpec(userName, userPass),
                ResponseSpecs.returns200())
                .post(new DepositRequestModel(bankAccount.getId(), depositAmount))
                .extract().body().as(BankAccountModel.class);

        softly.assertThat(responseBody.getBalance())
                .withFailMessage("Balance in response body of deposit request. " +
                        "Expected: %s; Actual: %s.", expectedBalance, responseBody.getBalance())
                .isEqualTo(expectedBalance);

        //check balance update
        BigDecimal updatedBalance = getBankAccount(bankAccount.getId()).getBalance();

        softly.assertThat(updatedBalance)
                .withFailMessage( "Balance was updated. "+
                        "Expected: %s; Actual: %s.", expectedBalance, updatedBalance)
                .isEqualTo(expectedBalance);
    }

    public static Stream<Arguments> validDepositAmount(){
        return Stream.of(
                Arguments.of(0.01),
                Arguments.of(4999.99),
                Arguments.of(5000),
                Arguments.of(Double.MIN_VALUE)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidAmount")
    public void userCanNotDepositInvalidAmount(double invalidAmount){
        BigDecimal depositAmount = BigDecimal.valueOf(invalidAmount).setScale(2, RoundingMode.HALF_UP);
        //get initial balance
        BigDecimal initialBalance = getBankAccount(bankAccount.getId()).getBalance();

        //send deposit request
        new DepositRequest(RequestSpecs.authAsUserSpec(userName, userPass),
                ResponseSpecs.returns400())
                .post(DepositRequestModel
                        .builder()
                        .id(bankAccount.getId())
                        .balance(depositAmount)
                        .build());

        //check balance after request with invalid amount
        BigDecimal balanceAfterBadRequest = getBankAccount(bankAccount.getId()).getBalance();

        softly.assertThat(balanceAfterBadRequest)
                .withFailMessage( "Balance after bad request: " + balanceAfterBadRequest)
                .isEqualTo(initialBalance);
    }

    public static Stream<Arguments> invalidAmount(){
        return Stream.of(
                Arguments.of(-0.99),
                Arguments.of(0),
                Arguments.of(5000.01),
                Arguments.of(Double.MAX_VALUE)
        );
    }

    @Test
    public void userCanNotDepositToNonExistingAccount(){
        new DepositRequest(RequestSpecs.authAsUserSpec(userName, userPass),
                ResponseSpecs.returns403())
                .post(DepositRequestModel
                        .builder()
                        .id(999999)
                        .balance(BigDecimal.valueOf(5000))
                        .build());
    }

    @Test
    public void depositToAccountConcurrently(){
        //check initial account balance
        BigDecimal initialBalance = getBankAccount(bankAccount.getId()).getBalance();

        //send 50 requests to deposit amount of 5 000
        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<?> depositTask = executorService.submit(() -> {
            for (int i = 1; i <= 50; i++){
                new DepositRequest(RequestSpecs.authAsUserSpec(userName, userPass),
                        ResponseSpecs.returns200())
                        .post(DepositRequestModel
                                .builder()
                                .id(bankAccount.getId())
                                .balance(BigDecimal.valueOf(5000))
                                .build());
            }
        });

        //wait until all deposit requests are sent
        try {
        depositTask.get();
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

        //balance after requests
        BigDecimal balanceAfterRequests = getBankAccount(bankAccount.getId()).getBalance();

        softly.assertThat(balanceAfterRequests)
                .withFailMessage( "Balance was updated incorrectly: " + balanceAfterRequests)
                .isEqualTo(initialBalance.add(BigDecimal.valueOf(250000)));
    }

}
