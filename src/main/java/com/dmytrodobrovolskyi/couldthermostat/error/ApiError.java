package com.dmytrodobrovolskyi.couldthermostat.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Standardized error response that supposed to be returned to a client in case if any exception.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
  private HttpStatus httpStatus;
  private String reason;

  /**
   * Application-specific error code. Optional.
   */
  private String errorCode;

  public ResponseEntity<ApiError> asResponseEntity() {
    return new ResponseEntity<>(this, httpStatus);
  }
}
