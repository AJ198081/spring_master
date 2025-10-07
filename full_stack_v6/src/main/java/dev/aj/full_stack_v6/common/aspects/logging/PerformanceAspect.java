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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class PerformanceAspect {

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
        log.warn("Method {}.{} took {} {} in total.%n",
                targetMethod.getDeclaringClass().getName(),
                targetMethod.getName(),
                stopWatch.getTime(TimeUnit.MICROSECONDS),
                TimeUnit.MICROSECONDS);

        return response;
    }
}
