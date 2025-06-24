package api.specs;

import api.config.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import api.models.LoginRequestModel;
import api.skelethon.requests.Endpoint;
import api.skelethon.requests.ValidatableCrudRequester;

import java.util.HashMap;
import java.util.List;

public class RequestSpecs {

    private static String baseURI = Config.getProperty("server") + Config.getProperty("apiVersion");
    private static String adminName = Config.getProperty("admin.name");
    private static String adminPass = Config.getProperty("admin.pass");
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
        return defaultRequestSpecs()
                .addHeader("Authorization", getUserAuthToken(adminName, adminPass))
                .build();
    }

    public static RequestSpecification authAsUserSpec(String name, String pass){
        return defaultRequestSpecs()
                .addHeader("Authorization", getUserAuthToken(name, pass))
                .build();
    }

    public static String getUserAuthToken(String name, String pass){
        String userToken;
        if (!authTokens.containsKey(name)) {
            userToken = new ValidatableCrudRequester(RequestSpecs.unauthSpec(),
                    Endpoint.AUTH_LOGIN, ResponseSpecs.returns200())
                    .post(new LoginRequestModel(name, pass))
                    .extract()
                    .header("Authorization");

            authTokens.put(name, userToken);
        } else {
            userToken = authTokens.get(name);
        }
        return userToken;
    }
}
