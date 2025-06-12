package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.LoginRequestModel;
import requests.methods.Post;

import static io.restassured.RestAssured.given;

public class LoginRequest extends Request implements Post<LoginRequestModel> {
    public LoginRequest(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        super(requestSpec, responseSpec);
    }

    @Override
    public ValidatableResponse post(LoginRequestModel model) {
        return given()
                .spec(requestSpec)
                .body(model)
                .post("auth/login")
                .then()
                .assertThat()
                .spec(responseSpec);
    }
}
