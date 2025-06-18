package models;

import java.util.List;

public class AllBankAccountsModel {

    private List<BankAccountModel> allAccounts;

    public AllBankAccountsModel (List<BankAccountModel> allAccounts){
        this.allAccounts = allAccounts;
    }

    public BankAccountModel getAccount(Integer id){
       var optionalResult =  this.allAccounts.stream().filter(ba -> ba.getId().equals(id)).findFirst();
       return optionalResult.orElse(null);
    }
}
