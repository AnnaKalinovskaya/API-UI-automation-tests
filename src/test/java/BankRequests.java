import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class BankRequests {

    static ValidatableResponse depositRequest(User user, int accountID, double amount){
        return  given().
                contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user.getAuthToken())
                .body(String.format("""
                            {
                              "id": %s,
                              "balance": %s
                            }
                            """, accountID, amount))
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then();
    }

    static ValidatableResponse transferRequest (User user, int senderAccountID, int receiverAccountID, double amount){
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user.getAuthToken())
                .body(String.format("""
                        {
                          "senderAccountId": %s,
                          "receiverAccountId": %s,
                          "amount": %s
                        }
                        """, senderAccountID, receiverAccountID, amount))
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then();
    }

    static ValidatableResponse createBankAccountRequest(User user){
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user.getAuthToken())
                .post("http://localhost:4111/api/v1/accounts")
                .then();
    }

    static ValidatableResponse getBankAccountsRequest (User user){
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user.getAuthToken())
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then();
    }

    static ValidatableResponse getCustomerProfile (User user){
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user.getAuthToken())
                .get("http://localhost:4111/api/v1/customer/profile")
                .then();
    }

    static ValidatableResponse updateCustomerName(User user, String customerName){
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", user.getAuthToken())
                .body(String.format("""
                        {
                          "name": "%s"
                        }
                        """, customerName))
                .put("http://localhost:4111/api/v1/customer/profile")
                .then();
    }


}
