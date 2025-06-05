import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import java.util.List;
import java.util.stream.Stream;

public class UpdateNameTest {

    private static User user;

    @BeforeAll
    public static void setup(){
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));

        user = Admin.getInstance().createUser("hanna", "hannaPass1!");
    }

    @ParameterizedTest
    @MethodSource("validNameValue")
    public void userCanUpdateNameWithValidValue(String validName){
        SoftAssertions softly = new SoftAssertions();
        var responseBody = BankRequests.updateCustomerName(user, validName)
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.SC_OK)
                .extract().body();

        softly.assertThat(responseBody.jsonPath().getString("message"))
                .isEqualTo("Profile updated successfully");
        softly.assertThat(responseBody.jsonPath().getInt("customer.id"))
                .withFailMessage("User id:")
                .isEqualTo(user.getID());
        softly.assertThat(responseBody.jsonPath().getString("customer.username"))
                .withFailMessage("User name:")
                .isEqualTo(user.getUserName());
        softly.assertThat(responseBody.jsonPath().getString("customer.name"))
                .withFailMessage("Updated customer name:")
                .isEqualTo(user.getCustomerName());
        softly.assertAll();
    }

    public static Stream<Arguments> validNameValue(){
        return Stream.of(
                Arguments.of("N n"),
                Arguments.of(StringUtil.generateRandomStringWithSpace(254)),
                Arguments.of(StringUtil.generateRandomStringWithSpace(255))
        );
    }

    @Test
    public void userCanUpdateNameWithAlreadySetName(){
        SoftAssertions softly = new SoftAssertions();
        //set Customer name as pre-condition
        String customerName = "Customer name";
        BankRequests.updateCustomerName(user, customerName);

        //update name with the value which is already set
        var responseBody = BankRequests.updateCustomerName(user, customerName)
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.SC_OK)
                .extract().body();

        softly.assertThat(responseBody.jsonPath().getString("message"))
                .isEqualTo("Profile updated successfully");
        softly.assertThat(responseBody.jsonPath().getInt("customer.id"))
                .withFailMessage("User id:")
                .isEqualTo(user.getID());
        softly.assertThat(responseBody.jsonPath().getString("customer.username"))
                .withFailMessage("User name:")
                .isEqualTo(user.getUserName());
        softly.assertThat(responseBody.jsonPath().getString("customer.name"))
                .withFailMessage("Updated customer name:")
                .isEqualTo(customerName);
        softly.assertAll();
    }

    @ParameterizedTest
    @MethodSource("inValidNameValue")
    public void userCanNotUpdateNameWithInvalidValue(String inValidName){
        SoftAssertions softly = new SoftAssertions();
        String initialName = user.getCustomerName();

        BankRequests.updateCustomerName(user, inValidName)
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        softly.assertThat(user.getCustomerName())
                .withFailMessage("Name got changed after invalid update name request:")
                .isEqualTo(initialName);
        softly.assertAll();
    }

    public static Stream<Arguments> inValidNameValue(){
        return Stream.of(
                Arguments.of("I"),
                Arguments.of("L "),
                Arguments.of("tree word name"),
                Arguments.of("name withnumbers4"),
                Arguments.of("name withsymbols!"),
                Arguments.of(" "),
                Arguments.of(StringUtil.generateRandomStringWithSpace(256))
        );
    }

    @AfterAll
    public static void deleteUser(){
        Admin.getInstance().deleteUser(user);
    }
}
