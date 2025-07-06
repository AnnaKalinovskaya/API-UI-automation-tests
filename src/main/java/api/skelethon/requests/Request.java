package api.skelethon.requests;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;


public abstract class Request implements CRUD {

    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;
    protected Endpoint endpoint;

    public Request (RequestSpecification requestSpec, Endpoint endpont, ResponseSpecification responseSpec){
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        this.endpoint = endpont;
    }
}
