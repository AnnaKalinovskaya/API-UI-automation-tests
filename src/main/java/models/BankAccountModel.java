package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountModel extends BaseModel{

    private Integer id;
    private String accountNumber;
    private BigDecimal balance;
    private List<TransactionModel> transactions;

    public BigDecimal getBalance(){
        return this.balance.setScale(2, RoundingMode.HALF_UP);
    }
}
