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

    @RestControllerAdvice
    public class ApiExceptionHandler {

        @ExceptionHandler(RouteNotFoundException.class)
        public ProblemDetail handleRouteNotFound(
                RouteNotFoundException exception,
                HttpServletRequest request) {

            return createProblem(
                    HttpStatus.NOT_FOUND,
                    "Route Not Found",
                    exception.getMessage(),
                    request
            );
        }

        @ExceptionHandler({
                ConstraintViolationException.class,
                MissingServletRequestParameterException.class
        })
        public ProblemDetail handleValidation(
                Exception exception,
                HttpServletRequest request) {

            return createProblem(
                    HttpStatus.BAD_REQUEST,
                    "Validation Failed",
                    "Invalid request. Please provide valid origin and destination branches.",
                    request
            );
        }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(
            IllegalArgumentException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage() != null
                        ? exception.getMessage()
                        : "Invalid request."
        );

        problem.setTitle("Bad Request");

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(
            Exception exception) {

        log.error("Unexpected error while processing payment routing",
                exception);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later."
        );

        problem.setTitle("Internal Server Error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problem);
    }

        private ProblemDetail createProblem(
                HttpStatus status,
                String title,
                String detail,
                HttpServletRequest request) {

            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    status, detail);

            problem.setTitle(title);
            problem.setInstance(URI.create(request.getRequestURI()));

            return problem;
        }
    }
}