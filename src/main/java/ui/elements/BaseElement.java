package ui.elements;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public abstract class BaseElement {

    protected final SelenideElement element;

    public BaseElement (SelenideElement element){
        this.element = element;
    }

    protected SelenideElement find (By selector){
        return element.find(selector);
    }

    protected ElementsCollection findAll (By selector){
        return element.findAll(selector);
    }
}
