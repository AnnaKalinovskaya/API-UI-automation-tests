package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

public class UserDashboard extends DashboardBase<UserDashboard> {

    @Getter
    private final SelenideElement welcomeLabel = $(Selectors.byClassName("welcome-text"));
    private final SelenideElement depositMoneyButton = $(Selectors.byXpath("//button[contains(text(),'Deposit')]"));
    private final SelenideElement makeTransferButton = $(Selectors.byXpath("//button[contains(text(),'Make a Transfer')]"));

    public String url(){
        return "/dashboard";
    }

    public EditProfilePage goToUserInfo(){
        profileName.click();
        return new EditProfilePage();
    }
    public DepositPage goToDeposit(){
        depositMoneyButton.click();
        return new DepositPage();
    }

    public TransferPage goToTransfer(){
        makeTransferButton.click();
        return new TransferPage();
    }
}
