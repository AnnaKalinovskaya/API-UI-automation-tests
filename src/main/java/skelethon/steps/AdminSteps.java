package skelethon.steps;

import models.CreateUserRequestModel;
import models.UserProfileModel;
import models.UserRole;
import skelethon.requests.CrudRequester;
import skelethon.requests.Endpoint;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class AdminSteps {

    private AdminSteps(){};

    public static UserProfileModel createUser(String name, String pass){
        return new CrudRequester<UserProfileModel>(RequestSpecs.adminSpec(), Endpoint.USERS, ResponseSpecs.returns201())
                .post(new CreateUserRequestModel(name, pass, UserRole.USER));
    }
}
