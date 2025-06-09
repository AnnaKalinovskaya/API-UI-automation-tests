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
public class TransferRequestModel extends BaseModel{

    private Integer senderAccountId;
    private Integer receiverAccountId;
    private BigDecimal amount;

    public BigDecimal getAmount (){
        return this.amount.setScale(2, RoundingMode.HALF_UP);
    }
}
