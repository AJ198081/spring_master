package dev.aj.sdj_hibernate.aspects;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class PerformanceAspect {

    NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);

    @SneakyThrows
    @Around("@annotation(dev.aj.sdj_hibernate.aspects.LogExecutionTiming)")
    public Object timingAdvice(ProceedingJoinPoint proceedingJoinPoint) {

        MethodSignature interceptedMethodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        LogExecutionTiming logTimingAnnotation = interceptedMethodSignature
                .getMethod()
                .getAnnotation(LogExecutionTiming.class);

        if (Objects.isNull(logTimingAnnotation)) {
            return proceedingJoinPoint.proceed();
        } else {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            Object response = proceedingJoinPoint.proceed();

            stopWatch.stop();
            System.out.printf("Method %s.%s took %s %s in total.%n", interceptedMethodSignature.getMethod().getDeclaringClass().getName(), interceptedMethodSignature.getMethod().getName(), formatter.format(stopWatch.getTime(TimeUnit.MICROSECONDS)), TimeUnit.MICROSECONDS);

            return response;
        }
    }

    @Before(value = "execution(public void *.populateStudentTable())")
    public void logBeforeMethod() {
        log.info("Before method called");
    }

}
