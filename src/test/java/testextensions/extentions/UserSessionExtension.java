package testextensions.extentions;

import testextensions.annotations.UserSession;
import api.BaseTest;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;

public class UserSessionExtension implements BeforeEachCallback {

    private static final Logger logger = LoggerFactory.getLogger(UserSessionExtension.class);

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        Method method = extensionContext.getRequiredTestMethod();
        logger.info("UserSessionListener.beforeInvocation called for method: {}", method.getName());
        UserSession annotation = method.getAnnotation(UserSession.class);

        if (annotation != null) {
            logger.info("UserSession annotation found, proceeding with user creation");
            Optional<Object> testInstanceOptional = extensionContext.getTestInstance();

            if (testInstanceOptional.isPresent()) {
                Object testInstance = testInstanceOptional.get();
                if (testInstance instanceof BaseTest) {
                    logger.info("Test instance is BaseTest, generating test data");
                    BaseTest.user = SessionStorage.createRandomUser();
                    logger.info("Generated test data and created user for test: {}", method.getName());
                } else {
                    logger.warn("Test instance is not BaseTest: {}", testInstance.getClass().getName());
                }

            }

        }

        else {
            logger.info("No UserSession annotation found for method: {}", method.getName());
        }
    }

}
