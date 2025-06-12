package requests;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class Request {

    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    public Request (RequestSpecification requestSpec, ResponseSpecification responseSpec){
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }
}
