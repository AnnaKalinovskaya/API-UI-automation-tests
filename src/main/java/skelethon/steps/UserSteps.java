package skelethon.steps;

import models.BankAccountModel;
import skelethon.requests.CrudRequester;
import skelethon.requests.Endpoint;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserSteps {

    private String name;
    private String pass;

    public UserSteps(String name, String pass){
        this.name = name;
        this.pass = pass;
    };

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }

    public BankAccountModel createBankAccount (){
        return new CrudRequester<BankAccountModel>(RequestSpecs.authAsUserSpec(name, pass),
                Endpoint.CREATE_ACCOUNT, ResponseSpecs.returns201())
                .post(null);
    }

    public BankAccountModel getBankAccount (Integer accountId) {
        var optionalResult = new CrudRequester<BankAccountModel>(RequestSpecs.authAsUserSpec(name, pass),
                Endpoint.CUSTOMER_ACCOUNTS, ResponseSpecs.returns200())
                .getList().stream().filter(account -> account.getId().equals(accountId)).findFirst();

        return optionalResult.orElse(null);
    }
}
