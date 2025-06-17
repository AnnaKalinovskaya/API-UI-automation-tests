package models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.customserializer.BigDecimalRoundedDeserializer;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferResponseModel extends BaseModel {

    private Integer receiverAccountId;

    @JsonDeserialize(using = BigDecimalRoundedDeserializer.class)
    private BigDecimal amount;

    private String message;
    private Integer senderAccountId;

}
