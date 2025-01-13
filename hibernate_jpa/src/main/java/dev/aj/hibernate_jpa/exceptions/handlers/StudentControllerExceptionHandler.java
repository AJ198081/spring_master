package dev.aj.hibernate_jpa.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;

@RestControllerAdvice
public class StudentControllerExceptionHandler {


    @ExceptionHandler(exception = {IllegalArgumentException.class})
    public ResponseEntity<ProblemDetail> handleIllegalStateException(IllegalArgumentException exception, WebRequest webRequest) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setProperty("Id", exception.getMessage().substring(exception.getMessage().indexOf("id: ") + 4));
        problemDetail.setTitle("Invalid Id");
        problemDetail.setInstance(URI.create(((ServletWebRequest) webRequest).getRequest().getRequestURI()));

        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }


}
