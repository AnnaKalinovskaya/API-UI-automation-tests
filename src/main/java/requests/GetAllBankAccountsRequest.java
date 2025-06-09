package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BankAccountModel;
import requests.methods.Get;

import static io.restassured.RestAssured.given;

public class GetAllBankAccountsRequest extends Request implements Get {
    public GetAllBankAccountsRequest(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        super(requestSpec, responseSpec);
    }

    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpec)
                .get("/customer/accounts")
                .then()
                .assertThat()
                .spec(responseSpec);
    }

    public BankAccountModel getBankAccount (Integer accountId){
        return get()
                .extract().body().jsonPath().getList("", BankAccountModel.class)
                .stream().filter(account -> account.getId().equals(accountId)).findFirst().get();
    }
}
