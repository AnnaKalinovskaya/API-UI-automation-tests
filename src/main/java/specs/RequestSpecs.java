package specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import models.LoginRequestModel;
import requests.LoginRequest;

import java.util.List;

public class RequestSpecs {

    private static String baseURI = "http://localhost:4111/api/v1";
    private static String adminName = "admin";
    private static String adminPass = "admin";
    private static String adminToken;

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
        if (adminToken == null) {
            adminToken = new LoginRequest(RequestSpecs.unauthSpec(), ResponseSpecs.returns200())
                    .post(new LoginRequestModel(adminName, adminPass))
                    .extract()
                    .header("Authorization");
        }

        return defaultRequestSpecs()
                .addHeader("Authorization", adminToken)
                .build();
    }

    public static RequestSpecification authAsUserSpec(String name, String pass){
        String userToken = new LoginRequest(RequestSpecs.unauthSpec(), ResponseSpecs.returns200())
                .post(new LoginRequestModel(name, pass))
                .extract()
                .header("Authorization");

        return defaultRequestSpecs()
                .addHeader("Authorization", userToken)
                .build();
    }
}
