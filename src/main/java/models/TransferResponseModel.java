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
public class TransferResponseModel extends BaseModel {

    private Integer receiverAccountId;
    private BigDecimal amount;
    private String message;
    private Integer senderAccountId;

    public BigDecimal getAmount (){
        return this.amount.setScale(2, RoundingMode.HALF_UP);
    }

}
