package testextensions.extentions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.HashMap;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private HashMap<String, Long> startTime = new HashMap<>();

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        String testName = extensionContext.getRequiredTestClass().getPackageName() +
                "." + extensionContext.getDisplayName();
        var duration = System.currentTimeMillis() - startTime.get(testName);
        System.out.println("Thread " + Thread.currentThread().getName() + " : Test finished: " + testName +
                ", test duration: " + duration);

    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        String testName = extensionContext.getRequiredTestClass().getPackageName() +
                "." + extensionContext.getDisplayName();
        startTime.put(testName, System.currentTimeMillis());
        System.out.println("Thread " + Thread.currentThread().getName() + " : Test started: " + testName);
    }
}
