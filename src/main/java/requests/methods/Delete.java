package requests.methods;

import io.restassured.response.ValidatableResponse;

public interface Delete {

    public ValidatableResponse delete(Integer userId);
}
