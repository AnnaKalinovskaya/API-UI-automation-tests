package api.models;

import api.models.customserializer.BigDecimalRoundedDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionModel extends BaseModel{

    private Integer id;

    @JsonDeserialize(using = BigDecimalRoundedDeserializer.class)
    private BigDecimal amount;

    private TransactionType type;
    private String timestamp;
    private Integer relatedAccountId;


}
