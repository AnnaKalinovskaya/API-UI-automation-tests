import models.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import requests.CreateBankAccountRequest;
import requests.CreateUserRequest;
import requests.DeleteUserRequest;
import requests.GetAllBankAccountsRequest;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class BaseTest {
    protected SoftAssertions softly;
    protected static String userName = "hanna69";
    protected static String userPass = "hannaPass1!";
    protected static UserProfileModel userProfile;

    @BeforeAll
    public static void createUser(){
        userProfile = new CreateUserRequest(RequestSpecs.adminSpec(), ResponseSpecs.returns201())
                .post(new CreateUserRequestModel(userName, userPass, UserRole.USER))
                .extract()
                .body().as(UserProfileModel.class);
    }

    @BeforeEach
    public void setUps(){
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest(){
        softly.assertAll();
    }

    @AfterAll
    public static void deleteUser(){
        new DeleteUserRequest(RequestSpecs.adminSpec(), ResponseSpecs.returns200())
                .delete(userProfile.getId());
    }

    protected static BankAccountModel createBankAccount(){
        return new CreateBankAccountRequest(RequestSpecs.authAsUserSpec(userName, userPass),
                ResponseSpecs.returns201())
                .post()
                .extract().body().as(BankAccountModel.class);
    }

    protected static BankAccountModel getBankAccount (Integer accountId){
        return new GetAllBankAccountsRequest(
                RequestSpecs.authAsUserSpec(userName, userPass),
                ResponseSpecs.returns200())
                .getBankAccount(accountId);
    }
}
