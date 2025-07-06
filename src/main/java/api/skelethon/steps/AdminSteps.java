package api.skelethon.steps;

import api.generators.RandomDataGenerator;
import api.skelethon.requests.CrudRequester;
import api.skelethon.requests.Endpoint;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.models.CreateUserRequestModel;
import api.models.UserProfileModel;
import api.models.UserRole;

public class AdminSteps {

    private AdminSteps(){};

    public static UserProfileModel createUser(String name, String pass){
        return new CrudRequester<UserProfileModel>(RequestSpecs.adminSpec(), Endpoint.USERS, ResponseSpecs.returns201())
                .post(new CreateUserRequestModel(name, pass, UserRole.USER));
    }
}
