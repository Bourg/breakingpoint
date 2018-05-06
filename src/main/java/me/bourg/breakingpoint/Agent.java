package me.bourg.breakingpoint;

import me.bourg.breakingpoint.sink.InstrumentationSink;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String args, Instrumentation instrumentation) {
        InstrumentationSink.startTest(); // TODO this should happen as part of the JUnit tests
        instrumentation.addTransformer(new Transformer());
    }
}
