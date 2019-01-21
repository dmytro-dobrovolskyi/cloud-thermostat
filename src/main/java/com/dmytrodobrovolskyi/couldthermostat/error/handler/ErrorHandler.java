package com.dmytrodobrovolskyi.couldthermostat.error.handler;

import com.dmytrodobrovolskyi.couldthermostat.error.ApiError;
import com.dmytrodobrovolskyi.couldthermostat.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {
  private static final String ERROR_DELIMITER = "; ";

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex) {
    return new ApiError()
        .setHttpStatus(HttpStatus.NOT_FOUND)
        .setReason(ex.getMessage())
        .asResponseEntity();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnknownException(Exception ex) {
    log.error("Unexpected exception occurred: {}", ex);

    return new ApiError()
        .setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        .setReason("Something happened on our side. Please contact support team")
        .asResponseEntity();
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return handleBindingResult(ex.getBindingResult());
  }

  private ResponseEntity<Object> handleBindingResult(BindingResult bindingResult) {
    Stream<String> fieldErrors = bindingResult.getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage());
    Stream<String> objectErrors = bindingResult.getGlobalErrors()
        .stream()
        .map(error -> error.getObjectName() + ": " + error.getDefaultMessage());

    String reason = Stream.of(fieldErrors, objectErrors)
        .flatMap(Function.identity())
        .reduce((prev, next) -> prev + ERROR_DELIMITER + next)
        .orElse("Reason unknown");

    return new ResponseEntity<>(
        new ApiError()
            .setHttpStatus(HttpStatus.BAD_REQUEST)
            .setReason(reason),
        HttpStatus.BAD_REQUEST
    );
  }
}
