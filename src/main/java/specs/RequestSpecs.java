package specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import models.LoginRequestModel;
import skelethon.requests.Endpoint;
import skelethon.requests.ValidatableCrudRequester;

import java.util.HashMap;
import java.util.List;

public class RequestSpecs {

    private static String baseURI = "http://localhost:4111/api/v1";
    private static String adminName = "admin";
    private static String adminPass = "admin";
    private static HashMap<String, String> authTokens = new HashMap<>();

    private RequestSpecs(){}

    private static RequestSpecBuilder defaultRequestSpecs(){
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()))
                .setBaseUri(baseURI);
    }

    public static RequestSpecification unauthSpec(){
        return defaultRequestSpecs().build();
    }

    public static RequestSpecification adminSpec(){
        if (!authTokens.containsKey("admin")) {
            String adminToken = new ValidatableCrudRequester(RequestSpecs.unauthSpec(),
                    Endpoint.AUTH_LOGIN, ResponseSpecs.returns200())
                    .post(new LoginRequestModel(adminName, adminPass))
                    .extract()
                    .header("Authorization");

            authTokens.put(adminName, adminToken);
        }

        return defaultRequestSpecs()
                .addHeader("Authorization", authTokens.get(adminName))
                .build();
    }

    public static RequestSpecification authAsUserSpec(String name, String pass){
        if (!authTokens.containsKey(name)) {
            String userToken = new ValidatableCrudRequester(RequestSpecs.unauthSpec(),
                    Endpoint.AUTH_LOGIN, ResponseSpecs.returns200())
                    .post(new LoginRequestModel(name, pass))
                    .extract()
                    .header("Authorization");

            authTokens.put(name, userToken);
        }

        return defaultRequestSpecs()
                .addHeader("Authorization", authTokens.get(name))
                .build();
    }
}
