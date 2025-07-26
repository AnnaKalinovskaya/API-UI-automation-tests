package ui.pages;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.fasterxml.jackson.databind.ser.Serializers;
import ui.elements.BaseElement;

import java.util.List;
import java.util.function.Function;


public abstract class BasePage <P extends BasePage>{

    public abstract String url();

    public P open(){
        return Selenide.open(url(), (Class<P>) this.getClass());
    };

    public P goTo(Class<P> pageClass){
        return Selenide.page(pageClass);
    }

    protected <T extends BaseElement> List<T> generateBaseElements(ElementsCollection elCollection, Function<SelenideElement, T> construstor){
        return elCollection.stream().map(construstor).toList();
    }
}
