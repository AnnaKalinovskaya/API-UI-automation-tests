package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$$;

public class TransactionsPage extends DashboardBase<TransactionsPage> {

    @Getter
    private ElementsCollection transactions = $$(Selectors.byXpath("//li[contains(@class, 'list-group-item')]"));
    private RepeatTransferModal transferModal;

    public String url(){
        return "/transfer";
    }

    public RepeatTransferModal repeatTransaction(SelenideElement transaction){
        transaction.$(Selectors.byXpath("./button[contains(text(), 'Repeat')]")).click();
        transferModal = new RepeatTransferModal();
        return transferModal;
    }

}
