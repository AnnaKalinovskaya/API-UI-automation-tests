package api.skelethon.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.BaseModel;

import static io.restassured.RestAssured.given;

public class ValidatableCrudRequester extends Request implements CRUD, GetAll {

    public ValidatableCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification){
        super(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpec)
                .get(endpoint.getEndpoint())
                .then()
                .assertThat()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        var body = model == null ? "" : model;
        return given()
                .spec(requestSpec)
                .body(body)
                .post(endpoint.getEndpoint())
                .then()
                .assertThat()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse put(BaseModel model) {
        return given()
                .spec(requestSpec)
                .body(model)
                .put(endpoint.getEndpoint())
                .then()
                .assertThat()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse delete(Long id) {
        return given()
                .spec(requestSpec)
                .delete(endpoint.getEndpoint()  + "/" + id)
                .then()
                .assertThat()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse getAll() {
        return given()
                .spec(requestSpec)
                .get(endpoint.getEndpoint())
                .then()
                .assertThat()
                .spec(responseSpec);
    }
}
