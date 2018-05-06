package me.bourg.breakingpoint.sink;

import java.io.PrintStream;

public class InstrumentationSink {

    private static boolean isTesting = false;

    private InstrumentationSink() {}

    public static void startTest() {
        assertNotTesting("Attempted to start testing, but testing was already started");

        doOutput("Starting testing!");
    }

    public static void endTest() {
        assertIsTesting("Attempted to end testing, but testing was never started");

        doOutput("Ending testing!");
    }

    public static void logCall(String caller, String callee) {
        assertIsTesting("Can only log calls if testing is enabled");

        doOutput("--> " + caller + " " + callee);
    }

    public static void logReturn(String from) {
        assertIsTesting("Can only log returns if testing is enabled");

        doOutput("<-- " + from);
    }

    public static void logPreBranching(String method, int branchNumber) {
        assertIsTesting("Can only log branches if testing is enabled");

        doOutput(String.format("<-> %s branch %d?", method, branchNumber));
    }

    public static void logDidNotBranch() {
        assertIsTesting("Can only log branch taken if testing is enabled");

        doOutput(String.format("\t\tDid not branch!"));
    }

    private static void doOutput(String output) {
        System.out.printf("<<BREAKING_POINT>> [THREAD %d]: %s\n",
                Thread.currentThread().getId(),
                output);
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
