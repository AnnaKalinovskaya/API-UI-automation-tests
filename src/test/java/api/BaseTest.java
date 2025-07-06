package api;

import api.generators.RandomDataGenerator;
import api.models.UserProfileModel;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import api.skelethon.requests.Endpoint;
import api.skelethon.requests.ValidatableCrudRequester;
import api.skelethon.steps.AdminSteps;
import api.skelethon.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class BaseTest {

    protected SoftAssertions softly;
    protected static UserProfileModel userProfile;
    protected static UserSteps user;

    @BeforeAll
    public static void createUser(){
        user = new UserSteps(RandomDataGenerator.getRandomUserName(), RandomDataGenerator.getRandomPass());
        userProfile = AdminSteps.createUser(user.getName(), user.getPass());
    }

    @BeforeEach
    public void setUp(){
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest(){
        softly.assertAll();
    }

    @AfterAll
    public static void deleteUser(){
        new ValidatableCrudRequester(RequestSpecs.adminSpec(), Endpoint.USERS, ResponseSpecs.returns200())
                .delete(userProfile.getId());
    }
}
