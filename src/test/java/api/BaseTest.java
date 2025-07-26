package api;

import common.storage.SessionStorage;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import api.skelethon.steps.UserSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import testextensions.extentions.TimingExtension;

@ExtendWith(TimingExtension.class)
public class BaseTest {

    protected SoftAssertions softly;
    public static UserSteps user;

    @BeforeAll
    public static void createUser(){
        user = SessionStorage.createRandomUser();
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
        SessionStorage.clear();
    }
}
