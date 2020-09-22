/**
 * Dieser Klass bleibt immer gleich
 */

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class MyTestRunner {

    public static void main(String[] args) {

//        Result result = JUnitCore.runClasses(MyTestingClass.class, AnnotationsTesting.class);
        Result result = JUnitCore.runClasses(TestSammlung1.class);
        for (Failure failure : result.getFailures()){
            System.out.println(failure.toString()); // Wenn ein fail ist -> returns die Beschreibung des Faehlers
        }
        System.out.println(result.wasSuccessful());  // Wenn alle Test Succesfull returns true. Falls mindestens 1 ist falsch - return false


    }

}
