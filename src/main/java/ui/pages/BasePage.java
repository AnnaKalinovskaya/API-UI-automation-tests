package ui.pages;

import com.codeborne.selenide.Selenide;

public abstract class BasePage <P extends BasePage>{

    public abstract String url();

    public P open(){
        return Selenide.open(url(), (Class<P>) this.getClass());
    };

    public P goTo(Class<P> pageClass){
        return Selenide.page(pageClass);
    }
}
