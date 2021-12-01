package com.metrics.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Counter.Builder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.function.Consumer;


/**
 * @author jeff
 * @date 2021/12/1
 */

public class Metrics {

    public static Counter newCounter(String name, Consumer<Builder> consumer) {
        MeterRegistry meterRegistry = SpringContextUtil.getBean(MeterRegistry.class);
        return new CounterBuilder(meterRegistry, name, consumer).build();
    }

    public static Timer newTimer(String name, Consumer<Timer.Builder> consumer) {
        MeterRegistry meterRegistry = SpringContextUtil.getBean(MeterRegistry.class);
        return new TimerBuilder(meterRegistry, name, consumer).build();
    }

    static class CounterBuilder {

        private final MeterRegistry meterRegistry;

        private Counter.Builder builder;

        private Consumer<Builder> consumer;

        public CounterBuilder(MeterRegistry meterRegistry, String name, Consumer<Counter.Builder> consumer) {
            this.builder = Counter.builder(name);
            this.meterRegistry = meterRegistry;
            this.consumer = consumer;
        }

        public Counter build() {
            consumer.accept(builder);
            return builder.register(meterRegistry);
        }
    }

    static class TimerBuilder {

        private final MeterRegistry meterRegistry;

        private Timer.Builder builder;

        private Consumer<Timer.Builder> consumer;

        public TimerBuilder(MeterRegistry meterRegistry, String name, Consumer<Timer.Builder> consumer) {
            this.builder = Timer.builder(name);
            this.meterRegistry = meterRegistry;
            this.consumer = consumer;
        }

        public Timer build() {
            this.consumer.accept(builder);
            return builder.register(meterRegistry);
        }
    }
}