package sweng.penelope;

public class TestUtils {
    public static void println(String message) {
        System.out.println(message);
        System.out.flush();
    }

    public static void testStart(String testName) {
        println("---- " + testName + " Start ----");
    }

    public static void testEnd(String testName) {
        println("---- " + testName + " End ----%n%n");
    }
}
