package api.skelethon.requests;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.BaseModel;

import java.util.List;


public class CrudRequester <M extends BaseModel> extends Request implements CRUD, GetAll {

    private final ValidatableCrudRequester validatableCrudRequester;

    public CrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification){
        super(requestSpecification, endpoint, responseSpecification);
        this.validatableCrudRequester = new ValidatableCrudRequester(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public M get() {
        return (M) validatableCrudRequester.get()
                .extract().body().as(endpoint.getResponseModel());
    }

    @Override
    public M post(BaseModel model) {
        return (M) validatableCrudRequester.post(model)
                .extract().as(endpoint.getResponseModel());
    }

    @Override
    public M put(BaseModel model) {
        return (M) validatableCrudRequester.put(model)
                .extract().as(endpoint.getResponseModel());
    }

    @Override
    public M delete(Long id) {
        return (M) validatableCrudRequester.delete(id)
                .extract().as(endpoint.getResponseModel());
    }

    @Override
    public List<M> getAll() {
        return (List<M>) validatableCrudRequester.get()
                .extract().body().jsonPath().getList("", endpoint.getResponseModel());
    }
}
