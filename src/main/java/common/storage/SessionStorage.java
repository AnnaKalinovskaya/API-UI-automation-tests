package common.storage;

import api.generators.RandomDataGenerator;
import api.models.UserProfileModel;
import api.skelethon.requests.Endpoint;
import api.skelethon.requests.ValidatableCrudRequester;
import api.skelethon.steps.AdminSteps;
import api.skelethon.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.LinkedHashMap;
import java.util.List;

public class SessionStorage {

    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);

    private final LinkedHashMap<UserProfileModel, UserSteps> users = new LinkedHashMap<>();

    private SessionStorage(){};

    public static UserSteps createRandomUser(){
        UserSteps user = new UserSteps(RandomDataGenerator.getRandomUserName(), RandomDataGenerator.getRandomPass());
        UserProfileModel userProfile = AdminSteps.createUser(user.getName(), user.getPass());
        INSTANCE.get().users.put(userProfile, user);
        return user;
    }

    public static UserSteps getUserStep(){
        UserProfileModel userProfile = INSTANCE.get().users.keySet().stream().toList().get(0);
        return INSTANCE.get().users.get(userProfile);
    }

    public static void clear(){
        List<UserProfileModel> createdUsers = INSTANCE.get().users.keySet().stream().toList();
        for (UserProfileModel user : createdUsers) {
            new ValidatableCrudRequester(RequestSpecs.adminSpec(), Endpoint.USERS, ResponseSpecs.returns200())
                    .delete(user.getId());
        }
        INSTANCE.get().users.clear();
    }
}
