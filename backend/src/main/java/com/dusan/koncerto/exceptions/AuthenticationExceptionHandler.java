package com.dusan.koncerto.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentials(BadCredentialsException ex) {
        return createResponse("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ProblemDetail> handleInternalAuthException(InternalAuthenticationServiceException ex) {
        String message = (ex.getCause() != null) ?
                ex.getCause().getMessage() : "Authentication failed";
        return createResponse(message, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ProblemDetail> handleRegistrationException(RegistrationException ex) {
        return createResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    private ResponseEntity<ProblemDetail> createResponse(String detail, HttpStatus status) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        return new ResponseEntity<>(problemDetail, status);
    }
}