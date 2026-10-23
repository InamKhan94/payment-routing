package uk.jpmorgan.example.paymentrouting.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleRouteNotFound(
            RouteNotFoundException exception,
            HttpServletRequest request) {

        ProblemDetail problem = createProblem(
                HttpStatus.NOT_FOUND,
                "Route Not Found",
                exception.getMessage(),
                request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ProblemDetail> handleValidation(
            Exception exception,
            HttpServletRequest request) {

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Invalid request. Please provide valid origin and destination branches.",
                request
        );

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request) {

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Invalid request. Please provide valid origin and destination branches.",
                request
        );

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request) {

        log.error(
                "Unexpected error while processing request: {} {}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

        ProblemDetail problem = createProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problem);
    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest request) {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(status, detail);

        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;
    }
}
