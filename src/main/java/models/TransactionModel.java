package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionModel extends BaseModel{

    private Integer id;
    private BigDecimal amount;
    private TransactionType type;
    private String timestamp;
    private Integer relatedAccountId;

    public BigDecimal getAmount(){
        return this.amount.setScale(2, RoundingMode.HALF_UP);
    }
}
