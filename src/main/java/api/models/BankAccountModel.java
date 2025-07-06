package api.models;

import api.models.customserializer.BigDecimalRoundedDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountModel extends BaseModel{

    private Integer id;
    private String accountNumber;

    @JsonDeserialize(using = BigDecimalRoundedDeserializer.class)
    private BigDecimal balance;

    private List<TransactionModel> transactions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccountModel that = (BankAccountModel) o;
        return Objects.equals(id, that.id) && Objects.equals(accountNumber, that.accountNumber) && Objects.equals(balance, that.balance) && Objects.equals(transactions, that.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountNumber, balance, transactions);
    }
}
