package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.DepositRequestModel;
import requests.methods.Post;

import static io.restassured.RestAssured.given;

public class DepositRequest extends Request implements Post<DepositRequestModel> {

    public DepositRequest(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        super(requestSpec, responseSpec);
    }

    @Override
    public ValidatableResponse post(DepositRequestModel model) {
        return given()
                .spec(requestSpec)
                .body(model)
                .post("/accounts/deposit")
                .then()
                .assertThat()
                .spec(responseSpec);
    }
}
