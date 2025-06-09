package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.CustomerNameRequestModel;
import requests.methods.Put;

import static io.restassured.RestAssured.given;

public class UpdateCustomerNameRequest extends Request implements Put<CustomerNameRequestModel> {
    public UpdateCustomerNameRequest(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        super(requestSpec, responseSpec);
    }

    @Override
    public ValidatableResponse put(CustomerNameRequestModel model) {
        return given()
                .spec(requestSpec)
                .body(model)
                .put("/customer/profile")
                .then()
                .assertThat()
                .spec(responseSpec);
    }
}
