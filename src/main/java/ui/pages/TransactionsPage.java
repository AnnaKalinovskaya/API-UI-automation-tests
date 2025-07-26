package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.TransactionBage;

import java.util.List;

import static com.codeborne.selenide.Selenide.$$;

public class TransactionsPage extends DashboardBase<TransactionsPage> {

    private ElementsCollection transactions = $$(Selectors.byXpath("//li[contains(@class, 'list-group-item')]"));
    private RepeatTransferModal transferModal;

    public String url(){
        return "/transfer";
    }

    public RepeatTransferModal repeatTransaction(TransactionBage transaction){
        transaction.getRepeatButton().click();
        transferModal = new RepeatTransferModal();
        return transferModal;
    }

    public List<TransactionBage> getTransactions () throws InterruptedException {
        Thread.sleep(5000);
        return generateBaseElements(transactions, TransactionBage::new);
    }

}
