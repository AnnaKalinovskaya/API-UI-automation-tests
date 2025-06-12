package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import requests.methods.Delete;

import static io.restassured.RestAssured.given;

public class DeleteUserRequest extends Request implements Delete {

    public DeleteUserRequest(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        super(requestSpec, responseSpec);
    }

    @Override
    public ValidatableResponse delete(Integer userId) {
        return given()
                .spec(requestSpec)
                .delete(String.format("http://localhost:4111/api/v1/admin/users/%s", userId))
                .then()
                .assertThat()
                .spec(responseSpec);
    }
}
