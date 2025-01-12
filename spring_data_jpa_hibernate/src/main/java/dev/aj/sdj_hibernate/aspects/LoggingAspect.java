package dev.aj.sdj_hibernate.aspects;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@Order(2)
public class LoggingAspect {

    private final ObjectMapper objectMapper;


    @SneakyThrows
//    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object adviseModelCreation(ProceedingJoinPoint proceedingJoinPoint) {
        if (proceedingJoinPoint.getArgs().length > 0) {
            log.info("Model received:%n%s".formatted(objectMapper.writeValueAsString(proceedingJoinPoint.getArgs()[0])));
        }

        Object response = proceedingJoinPoint.proceed();

        if (response instanceof ResponseEntity<?> responseEntity) {
            log.info("Model persisted:%n%s".formatted(responseEntity.getBody()));
        }

        return response;
    }

    @SneakyThrows
//    @Around("within(dev.aj.sdj_hibernate.domain.services.startup..*)")
    public Object adviseFetchingAModel(ProceedingJoinPoint proceedingJoinPoint) {
        if (proceedingJoinPoint.getArgs().length > 0) {
            Object identifier = proceedingJoinPoint.getArgs()[0];
            log.info("Fetching Model for Id:[%s]".formatted(objectMapper.writeValueAsString(identifier)));
        }

        Object response = proceedingJoinPoint.proceed();

        if (response instanceof ResponseEntity<?> responseEntity) {
            log.info("Model fetched:%n%s".formatted(responseEntity.getBody()));
        }

        return response;
    }
}