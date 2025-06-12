package requests.methods;

import io.restassured.response.ValidatableResponse;
import models.BaseModel;

public interface Post <T extends BaseModel>{

    public ValidatableResponse post(T model);
}
