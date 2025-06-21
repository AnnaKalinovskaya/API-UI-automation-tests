package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$$;

public class TransactionsPage {

    @Getter
    private ElementsCollection transactions = $$(Selectors.byXpath("//li[contains(@class, 'list-group-item')]"));
    private RepeatTransferModal transferModal = new RepeatTransferModal();

    public RepeatTransferModal repeatTransaction(int index){
        transactions.get(index)
                .$(Selectors.byXpath("/button[contains(text(), 'Repeat')]"))
                .click();
        return transferModal;
    }

}
