package requests.methods;

import io.restassured.response.ValidatableResponse;
import models.BaseModel;

public interface Put <T extends BaseModel>{

    public ValidatableResponse put(T model);
}
