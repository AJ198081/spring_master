package dev.aj.full_stack_v6.common.aspects.logging;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class PerformanceAspect {

    private static final ConcurrentHashMap<String, List<Long>> performanceRecorder = new ConcurrentHashMap<>();

    @SneakyThrows
    @Around("@annotation(dev.aj.full_stack_v6.common.aspects.logging.LogExecutionTiming)")
    public Object timingAdvice(ProceedingJoinPoint proceedingJoinPoint) {

        Method targetMethod = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();

        if (Objects.isNull(targetMethod.getAnnotation(LogExecutionTiming.class))) {
            return proceedingJoinPoint.proceed();
        }

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        Object response = proceedingJoinPoint.proceed();
        stopWatch.stop();

        log.warn("Method {}.{} took {} {} in total",
                targetMethod.getDeclaringClass().getName(),
                targetMethod.getName(),
                stopWatch.getTime(TimeUnit.MILLISECONDS),
                TimeUnit.MILLISECONDS);

        return response;
    }

    @Around("@annotation(dev.aj.full_stack_v6.common.aspects.logging.MeasurePerformance)")
    public Object logPerformanceOfMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Method targetMethod = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();

        if (Objects.isNull(targetMethod.getAnnotation(MeasurePerformance.class))) {
            return proceedingJoinPoint.proceed();
        }

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        Object methodExecutionResponse = proceedingJoinPoint.proceed();
        stopWatch.stop();

        logPerformanceOfMethod(targetMethod.getName(), stopWatch.getTime(TimeUnit.MILLISECONDS));

        return methodExecutionResponse;
    }

    public static void logPerformanceOfMethod(String methodName, long timeTakenInMillis) {
        List<Long> executions = performanceRecorder.computeIfAbsent(methodName, _ -> new java.util.ArrayList<>());
        executions.add(timeTakenInMillis);
        performanceRecorder.put(methodName, executions);
        computeAndDisplayStatistics(methodName);
    }

    private static void computeAndDisplayStatistics(String methodName) {
        List<Long> executions = performanceRecorder.get(methodName);

        if (executions != null && !executions.isEmpty()) {
            LongSummaryStatistics summaryStatistics = executions.stream()
                    .mapToLong(Long::longValue)
                    .summaryStatistics();

            log.info("Method [{}] :- {}.", methodName, summaryStatistics);
        }
    }
}
