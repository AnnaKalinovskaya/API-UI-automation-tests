package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import requests.methods.PostWithoutBody;

import static io.restassured.RestAssured.given;

public class CreateBankAccountRequest extends Request implements PostWithoutBody {
    public CreateBankAccountRequest(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        super(requestSpec, responseSpec);
    }

    @Override
    public ValidatableResponse post() {
        return given()
                .spec(requestSpec)
                .post("/accounts")
                .then()
                .assertThat()
                .spec(responseSpec);
    }
}
