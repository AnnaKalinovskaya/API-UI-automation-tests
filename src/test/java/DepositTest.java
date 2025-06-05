import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
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
import java.util.concurrent.*;
import java.util.stream.Stream;


public class DepositTest {

    private static User user;
    private static BankAccount bankAccount;

    @BeforeAll
    public static void setup(){
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));
        user = Admin.getInstance().createUser("hanna", "hannaPass1!");
        bankAccount = user.createBankAccount();
    }

    @ParameterizedTest
    @MethodSource("validDepositAmount")
    public void userCanDepositValidAmount (double validAmount){
        //get initial balance
        BigDecimal initialBalanceAsDouble = user.getAccountBalance(bankAccount.getId());
        BigDecimal expectedBalance = initialBalanceAsDouble
                .add(BigDecimal.valueOf(validAmount)
                        .setScale(2, RoundingMode.HALF_UP));

        //send deposit request
        double balanceAsDouble = BankRequests.depositRequest(user, bankAccount.getId(), validAmount)
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body().jsonPath().getDouble("balance");

        BigDecimal balanceAsBigDecimal = BigDecimal.valueOf(balanceAsDouble).setScale(2, RoundingMode.HALF_UP);

        Assertions.assertEquals(expectedBalance, balanceAsBigDecimal, "Balance in response body of deposit request");

        //check balance update
        BigDecimal updatedBalance = user.getAccountBalance(bankAccount.getId());

        Assertions.assertEquals(expectedBalance, updatedBalance, "Balance after deposit: ");
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
        //get initial balance
        BigDecimal initialBalance = user.getAccountBalance(bankAccount.getId());

        //send deposit request
        BankRequests.depositRequest(user, bankAccount.getId(), invalidAmount)
                .assertThat().statusCode(HttpStatus.SC_BAD_REQUEST);

        //check balance after request with invalid amount
        BigDecimal balanceAfterBadRequest = user.getAccountBalance(bankAccount.getId());

        Assertions.assertEquals(initialBalance, balanceAfterBadRequest, "Balance after bad request: ");
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
        BankRequests.depositRequest(user, 5000, 999999)
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void depositToAccountConcurrently(){
        //check initial account balance
        BigDecimal initialBalance = user.getAccountBalance(bankAccount.getId());

        //send 50 requests to deposit amount of 5 000
        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<?> depositTask = executorService.submit(() -> {
            for (int i = 1; i <= 50; i++){
                BankRequests.depositRequest(user, bankAccount.getId(), 5000);
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

        Assertions.assertEquals(initialBalance.add(BigDecimal.valueOf(250000)),
                user.getAccountBalance(bankAccount.getId()),
                "Balance was updated incorrectly");
    }

    @AfterAll
    public static void deleteUser(){
        Admin.getInstance().deleteUser(user);
    }
}
