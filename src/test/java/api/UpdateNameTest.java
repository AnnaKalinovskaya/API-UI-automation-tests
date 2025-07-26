package api;

import api.generators.RandomDataGenerator;
import api.models.CustomerNameRequestModel;
import api.models.CustomerNameResponseModel;
import api.models.UserProfileModel;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.skelethon.requests.CrudRequester;
import api.skelethon.requests.Endpoint;
import api.skelethon.requests.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import testextensions.annotations.UserSession;

import java.util.stream.Stream;

public class UpdateNameTest extends BaseTest{


    @ParameterizedTest
    @UserSession
    @MethodSource("validNameValue")
    public void userCanUpdateNameWithValidValue(String validName){
        UserProfileModel userProfileBeforeChange = user.getCustomerProfile();

        CustomerNameResponseModel responseBody = new CrudRequester<CustomerNameResponseModel>(
                RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.returns200()).put(new CustomerNameRequestModel(validName));

        softly.assertThat(responseBody.getMessage())
                .isEqualTo("Profile updated successfully");
        softly.assertThat(responseBody.getCustomer())
                .withFailMessage("User ID/name/role from request do not match to response user info")
                .isEqualTo(userProfileBeforeChange);
        softly.assertThat(responseBody.getCustomer().getName())
                .withFailMessage("Updated customer name: " + responseBody.getCustomer().getName())
                .isEqualTo(validName);

        //check name via GET request
        String nameAfterUpdateRequest = new CrudRequester<UserProfileModel>(
                RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.returns200())
                .get().getName();

        softly.assertThat(nameAfterUpdateRequest)
                .withFailMessage("Actual customer name from GET request: "
                        + nameAfterUpdateRequest)
                .isEqualTo(validName);
    }

    public static Stream<Arguments> validNameValue(){
        return Stream.of(
                Arguments.of("N n"),
                Arguments.of(RandomDataGenerator.getRandomStringWithSpace(254)),
                Arguments.of(RandomDataGenerator.getRandomStringWithSpace(255))
        );
    }

    @Test
    @UserSession
    public void userCanUpdateNameWithAlreadySetName(){
        //set Customer name as pre-condition
        String customerName = "Customer name";
        new CrudRequester<CustomerNameResponseModel>(RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.returns200()).put(new CustomerNameRequestModel(customerName));

        UserProfileModel userProfileBeforeChange = SessionStorage.getUserStep().getCustomerProfile();
        //update name with the value which is already set
        CustomerNameResponseModel responseBody = new CrudRequester<CustomerNameResponseModel>(
                RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.returns200()).put(new CustomerNameRequestModel(customerName));

        softly.assertThat(responseBody.getMessage())
                .isEqualTo("Profile updated successfully");
        softly.assertThat(responseBody.getCustomer())
                .withFailMessage("User ID/name/role from request do not match to response user info")
                .isEqualTo(userProfileBeforeChange);
        softly.assertThat(responseBody.getCustomer().getName())
                .withFailMessage("Updated customer name: " + responseBody.getCustomer().getName())
                .isEqualTo(customerName);

        //check name via GET request
        String nameAfterUpdateRequest = new CrudRequester<UserProfileModel>(
                RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.returns200())
                .get().getName();

        softly.assertThat(nameAfterUpdateRequest)
                .withFailMessage("Actual customer name from GET request: "
                        + nameAfterUpdateRequest)
                .isEqualTo(customerName);
    }

    @ParameterizedTest
    @UserSession
    @MethodSource("inValidNameValue")
    public void userCanNotUpdateNameWithInvalidValue(String inValidName){
        String initialName = user.getCustomerProfile().getName();

        new ValidatableCrudRequester(
                RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.UPDATE_CUSTOMER_PROFILE,
                ResponseSpecs.returns400())
                .put(new CustomerNameRequestModel(inValidName));

        //check name via GET request
        String nameAfterUpdateRequest = new CrudRequester<UserProfileModel>(
                RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.returns200())
                .get().getName();

        softly.assertThat(nameAfterUpdateRequest)
                .withFailMessage("Name got changed after invalid update name request:")
                .isEqualTo(initialName);
    }

    public static Stream<Arguments> inValidNameValue(){
        return Stream.of(
                Arguments.of("I"),
                Arguments.of("L "),
                Arguments.of("tree word name"),
                Arguments.of("name withnumbers4"),
                Arguments.of("name withsymbols!"),
                Arguments.of(" "),
                Arguments.of(RandomDataGenerator.getRandomStringWithSpace(256))
        );
    }
}
