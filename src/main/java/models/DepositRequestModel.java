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
public class DepositRequestModel extends BaseModel{

    private Integer id;
    private BigDecimal balance;

    public BigDecimal getBalance(){
        return this.balance.setScale(2, RoundingMode.HALF_UP);
    }
}
