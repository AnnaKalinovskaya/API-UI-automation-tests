package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import requests.methods.Get;

import static io.restassured.RestAssured.given;

public class GetCustomerProfileRequest extends Request implements Get {


    public GetCustomerProfileRequest(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        super(requestSpec, responseSpec);
    }

    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpec)
                .get("/customer/profile")
                .then()
                .assertThat()
                .spec(responseSpec);
    }
}
