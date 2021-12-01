package com.metrics.aspect;

/**
 * @author jeff
 * @date 2021/12/1
 */

import com.metrics.annotation.Count;
import com.metrics.annotation.Monitor;
import com.metrics.annotation.Tp;
import com.metrics.config.Metrics;
import io.micrometer.core.instrument.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Aspect
@Component
public class MetricsAspect {

    /**
     * Prometheus指标管理
     */
    private MeterRegistry registry;

    private Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint;

    public MetricsAspect(MeterRegistry registry) {
        this.init(registry, pjp -> Tags
                .of(new String[]{"class", pjp.getStaticPart().getSignature().getDeclaringTypeName(), "method",
                        pjp.getStaticPart().getSignature().getName()}));
    }

    public void init(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint) {
        this.registry = registry;
        this.tagsBasedOnJoinPoint = tagsBasedOnJoinPoint;
    }


    @Pointcut("@annotation(com.metrics.annotation.Tp)")
    public void timedMethod() {
    }


    /**
     * 针对@Tp指标配置注解的逻辑实现
     */
    @Around(value = "timedMethod() && @annotation(tp)")
    public Object timedMethod(ProceedingJoinPoint pjp, Tp tp) throws Throwable {
        Timer.Sample sample = Timer.start(this.registry);
        String exceptionClass = "None";
        try {
            return pjp.proceed();
        } catch (Exception ex) {
            exceptionClass = ex.getClass().getSimpleName();
            throw ex;
        } finally {
            try {
                String finalExceptionClass = exceptionClass;
                //创建定义计数器，并设置指标的Tags信息（名称可以自定义）
                Timer timer = Metrics.newTimer("tp.method.timed",
                        builder -> builder.tags(new String[]{"exception", finalExceptionClass})
                                .tags(this.tagsBasedOnJoinPoint.apply(pjp)).tag("description", tp.description())
                                .publishPercentileHistogram().register(this.registry));
                sample.stop(timer);
            } catch (Exception exception) {
            }
        }
    }

    @Pointcut("@annotation(com.metrics.annotation.Count)")
    public void countMethod() {
    }

    /**
     * 针对@Count指标配置注解的逻辑实现
     */
    @Around(value = "countMethod() && @annotation(count)")
    public Object countMethod(ProceedingJoinPoint pjp, Count count) throws Throwable {
        String exceptionClass = "none";
        try {
            return pjp.proceed();
        } catch (Exception ex) {
            exceptionClass = ex.getClass().getSimpleName();
            throw ex;
        } finally {
            try {
                String finalExceptionClass = exceptionClass;
                //创建定义计数器，并设置指标的Tags信息（名称可以自定义）
                Counter counter = Metrics.newCounter("count.method.counted",
                        builder -> builder.tags(new String[]{"exception", finalExceptionClass})
                                .tags(this.tagsBasedOnJoinPoint.apply(pjp)).tag("description", count.description())
                                .register(this.registry));
                counter.increment();
            } catch (Exception exception) {
            }
        }
    }

    @Pointcut("@annotation(com.metrics.annotation.Monitor)")
    public void monitorMethod() {
    }

    /**
     * 针对@Monitor通用指标配置注解的逻辑实现
     */
    @Around(value = "monitorMethod() && @annotation(monitor)")
    public Object monitorMethod(ProceedingJoinPoint pjp, Monitor monitor) throws Throwable {
        String exceptionClass = "none";
        try {
            return pjp.proceed();
        } catch (Exception ex) {
            exceptionClass = ex.getClass().getSimpleName();
            throw ex;
        } finally {
            try {
                String finalExceptionClass = exceptionClass;
                //计时器Metric
                Timer timer = Metrics.newTimer("tp.method.timed",
                        builder -> builder.tags(new String[]{"exception", finalExceptionClass})
                                .tags(this.tagsBasedOnJoinPoint.apply(pjp)).tag("description", monitor.description())
                                .publishPercentileHistogram().register(this.registry));
                Timer.Sample sample = Timer.start(this.registry);
                sample.stop(timer);

                //计数器Metric
                Counter counter = Metrics.newCounter("count.method.counted",
                        builder -> builder.tags(new String[]{"exception", finalExceptionClass})
                                .tags(this.tagsBasedOnJoinPoint.apply(pjp)).tag("description", monitor.description())
                                .register(this.registry));
                counter.increment();
            } catch (Exception exception) {
            }
        }
    }
}
