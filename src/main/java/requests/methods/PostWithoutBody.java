package requests.methods;

import io.restassured.response.ValidatableResponse;

public interface PostWithoutBody {

    public ValidatableResponse post();
}
