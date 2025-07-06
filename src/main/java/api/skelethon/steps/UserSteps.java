package api.skelethon.steps;

import api.models.*;
import api.skelethon.requests.CrudRequester;
import api.skelethon.requests.Endpoint;
import api.skelethon.requests.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.*;

public class UserSteps {

    @Getter
    private String name;
    @Getter
    private String pass;
    private final BigDecimal MAX_DEPOSIT = new BigDecimal(5000);

    public UserSteps(String name, String pass){
        this.name = name;
        this.pass = pass;
    }

    public UserProfileModel getCustomerProfile(){
        return new CrudRequester<UserProfileModel>(
                RequestSpecs.authAsUserSpec(this.name, this.pass),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.returns200())
                .get();
    }

    public BankAccountModel createBankAccount (){
        return new CrudRequester<BankAccountModel>(RequestSpecs.authAsUserSpec(name, pass),
                Endpoint.CREATE_ACCOUNT, ResponseSpecs.returns201())
                .post(null);
    }

    public AllBankAccountsModel getAllBankAccounts() {
        List<BankAccountModel> allAccounts = new CrudRequester<BankAccountModel>(
                RequestSpecs.authAsUserSpec(name, pass),
                Endpoint.CUSTOMER_ACCOUNTS, ResponseSpecs.returns200())
                .getAll();
        return new AllBankAccountsModel(allAccounts);
    }

    public BankAccountModel createAccountWithBalance(BigDecimal balance){
        BankAccountModel bankAccount = createBankAccount();
        deposit(bankAccount.getId(), balance);
        return getAllBankAccounts().getAccount(bankAccount.getId());
    }

    public void deposit (Integer bankAccountId, BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid deposit amount: " + amount);
        }

        int requestCountWithMaxAmount = amount.divide(MAX_DEPOSIT, RoundingMode.DOWN).intValue();

        if (requestCountWithMaxAmount != 0) {
            ExecutorService executorService = Executors.newCachedThreadPool();

            Future<?> depositTask = executorService.submit(() -> {
                for (int i = 0; i < requestCountWithMaxAmount; i++) {
                    sendDepositRequest(bankAccountId, MAX_DEPOSIT);
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
        }

        BigDecimal totalDeposited = MAX_DEPOSIT.multiply(new BigDecimal(requestCountWithMaxAmount));
        BigDecimal leftOverAmount = amount.subtract(totalDeposited);
        if (leftOverAmount.compareTo(BigDecimal.ZERO) > 0) {
            sendDepositRequest(bankAccountId, leftOverAmount);
        }
    }

    public void transfer(Integer senderAccountID, Integer receiverAccountID, BigDecimal amount){
        new ValidatableCrudRequester(RequestSpecs.authAsUserSpec(name, pass),
                Endpoint.TRANSFER, ResponseSpecs.returns200())
                .post(TransferRequestModel
                        .builder()
                        .senderAccountId(senderAccountID)
                        .receiverAccountId(receiverAccountID)
                        .amount(amount)
                        .build());
    }

    private void sendDepositRequest (Integer bankAccountId, BigDecimal amount){
        new ValidatableCrudRequester(RequestSpecs.authAsUserSpec(name, pass),
                Endpoint.DEPOSIT, ResponseSpecs.returns200())
                .post(DepositRequestModel
                        .builder()
                        .id(bankAccountId)
                        .balance(amount)
                        .build());
    }
}
