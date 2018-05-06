package me.bourg.breakingpoint.sink;

import java.io.PrintStream;

public class InstrumentationSink {

    private static boolean isTesting = false;

    private static PrintStream outStream = System.out;

    private InstrumentationSink() {}

    public static void startTest() {
        assertNotTesting("Attempted to start testing, but testing was already started");

        outStream.println("Starting testing!");
    }

    public static void endTest() {
        assertIsTesting("Attempted to end testing, but testing was never started");

        outStream.println("Ending testing!");
    }

    public static void logCall(String caller, String callee) {
        assertIsTesting("Can only log calls if testing is enabled");

        outStream.println("CALL LOG: " + caller + " called " + callee);
    }

    private static void assertIsTesting(boolean shouldBeTesting, String error) {
        if (isTesting && !shouldBeTesting ||
                !isTesting && shouldBeTesting) {
            //throw new InstrumentationStateException(error);
        }
    }

    private static void assertIsTesting(String error) {
        assertIsTesting(true, error);
    }

    private static void assertNotTesting(String error) {
        assertIsTesting(false, error);
    }
}
