package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.CreateUserRequestModel;
import requests.methods.Post;

import static io.restassured.RestAssured.given;

public class CreateUserRequest extends Request implements Post<CreateUserRequestModel> {
    public CreateUserRequest(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        super(requestSpec, responseSpec);
    }

    @Override
    public ValidatableResponse post(CreateUserRequestModel model) {
        return given()
                .spec(requestSpec)
                .body(model)
                .post("/admin/users")
                .then()
                .assertThat()
                .spec(responseSpec);
    }
}
