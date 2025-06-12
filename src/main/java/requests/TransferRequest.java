package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.TransferRequestModel;
import requests.methods.Post;

import static io.restassured.RestAssured.given;

public class TransferRequest extends Request implements Post<TransferRequestModel> {

    public TransferRequest(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        super(requestSpec, responseSpec);
    }

    @Override
    public ValidatableResponse post(TransferRequestModel model) {
        return given()
                .spec(requestSpec)
                .body(model)
                .post("/accounts/transfer")
                .then()
                .assertThat()
                .spec(responseSpec);
    }
}
