package ui.elements;

import api.models.TransactionType;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;

public class TransactionBage extends BaseElement{

    @Getter
    private TransactionType type;
    @Getter
    private String amount;
    @Getter
    private SelenideElement repeatButton;

    public TransactionBage(SelenideElement element) {
        super(element);
        amount = element.find(By.xpath("./span"))
                .getText()
                .split("\n")[0].split("-")[1].trim();
        repeatButton = element.find(By.xpath("./button"));

        var stringType = element.find(By.xpath("./span")).getText().split("\n")[0].split("-")[0].trim();
        switch(stringType){
            case "DEPOSIT":
                type = TransactionType.DEPOSIT;
                break;
            case "TRANSFER_IN":
                type = TransactionType.TRANSFER_IN;
                break;
            case "TRANSFER_OUT":
                type = TransactionType.TRANSFER_OUT;
                break;
            default:
                System.out.println("Such transaction type doesn't exists: " + stringType);
                        type = null;
                break;
        }
    }
}
