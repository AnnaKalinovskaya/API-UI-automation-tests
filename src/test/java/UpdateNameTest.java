import generators.RandomDataGenerator;
import models.CustomerNameRequestModel;
import models.CustomerNameResponseModel;
import models.UserProfileModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import skelethon.requests.CrudRequester;
import skelethon.requests.Endpoint;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import java.util.stream.Stream;

public class UpdateNameTest extends BaseTest{


    @ParameterizedTest
    @MethodSource("validNameValue")
    public void userCanUpdateNameWithValidValue(String validName){
        CustomerNameResponseModel responseBody = new CrudRequester<CustomerNameResponseModel>(
                RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.CUSTOMER_PROFILE,
                ResponseSpecs.returns200()).put(new CustomerNameRequestModel(validName));

        softly.assertThat(responseBody.getMessage())
                .isEqualTo("Profile updated successfully");
        softly.assertThat(responseBody.getCustomer().getId())
                .withFailMessage("User id:" + responseBody.getCustomer().getId())
                .isEqualTo(userProfile.getId());
        softly.assertThat(responseBody.getCustomer().getUsername())
                .withFailMessage("User name: " + responseBody.getCustomer().getUsername())
                .isEqualTo(userProfile.getUsername());
        softly.assertThat(responseBody.getCustomer().getName())
                .withFailMessage("Updated customer name: " + responseBody.getCustomer().getName())
                .isEqualTo(userProfile.getName());
    }

    public static Stream<Arguments> validNameValue(){
        return Stream.of(
                Arguments.of("N n"),
                Arguments.of(RandomDataGenerator.getRandomStringWithSpace(254)),
                Arguments.of(RandomDataGenerator.getRandomStringWithSpace(255))
        );
    }

    @Test
    public void userCanUpdateNameWithAlreadySetName(){
        //set Customer name as pre-condition
        String customerName = "Customer name";
        new CrudRequester<CustomerNameResponseModel>(RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.CUSTOMER_PROFILE,
                ResponseSpecs.returns200()).put(new CustomerNameRequestModel(customerName));

        //update name with the value which is already set
        CustomerNameResponseModel responseBody = new CrudRequester<CustomerNameResponseModel>(
                RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.CUSTOMER_PROFILE,
                ResponseSpecs.returns200()).put(new CustomerNameRequestModel(customerName));

        softly.assertThat(responseBody.getMessage())
                .isEqualTo("Profile updated successfully");
        softly.assertThat(responseBody.getCustomer().getId())
                .withFailMessage("User id:" + responseBody.getCustomer().getId())
                .isEqualTo(userProfile.getId());
        softly.assertThat(responseBody.getCustomer().getUsername())
                .withFailMessage("User name: " + responseBody.getCustomer().getUsername())
                .isEqualTo(userProfile.getUsername());
        softly.assertThat(responseBody.getCustomer().getName())
                .withFailMessage("Updated customer name: " + responseBody.getCustomer().getName())
                .isEqualTo(customerName);
    }

    @ParameterizedTest
    @MethodSource("inValidNameValue")
    public void userCanNotUpdateNameWithInvalidValue(String inValidName){
        String initialName = userProfile.getName();

        new CrudRequester<CustomerNameResponseModel>(
                RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.CUSTOMER_PROFILE,
                ResponseSpecs.returns400()).put(new CustomerNameRequestModel(inValidName));

        String nameAfterUpdateRequest = new CrudRequester<UserProfileModel>(
                RequestSpecs.authAsUserSpec(user.getName(), user.getPass()),
                Endpoint.CUSTOMER_PROFILE,
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
