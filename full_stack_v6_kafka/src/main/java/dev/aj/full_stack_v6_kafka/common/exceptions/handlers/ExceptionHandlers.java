package dev.aj.full_stack_v6_kafka.common.exceptions.handlers;

import dev.aj.full_stack_v6_kafka.common.exceptions.TransferProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlers {

    @ExceptionHandler(value = {TransferProcessingException.class})
    public ProblemDetail handleEntityNotFoundException(TransferProcessingException ex) {
        HttpStatus preconditionFailed = HttpStatus.PRECONDITION_FAILED;

        return ProblemDetail.forStatusAndDetail(
                preconditionFailed,
                ex.getMessage()
        );
    }

}
