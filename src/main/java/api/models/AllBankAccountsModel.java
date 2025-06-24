package api.models;

import java.util.List;
import java.util.Objects;

public class AllBankAccountsModel {

    private List<BankAccountModel> allAccounts;

    public AllBankAccountsModel (List<BankAccountModel> allAccounts){
        this.allAccounts = allAccounts;
    }

    public BankAccountModel getAccount(Integer id){
       var optionalResult =  this.allAccounts.stream().filter(ba -> ba.getId().equals(id)).findFirst();
       return optionalResult.orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllBankAccountsModel that = (AllBankAccountsModel) o;
        return Objects.equals(allAccounts, that.allAccounts);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(allAccounts);
    }
}
