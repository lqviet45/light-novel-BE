package com.lqviet.authservice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserServiceException extends RuntimeException {
  private final HttpStatus httpStatus;

  public UserServiceException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public UserServiceException(String message, HttpStatus httpStatus, Throwable cause) {
    super(message, cause);
    this.httpStatus = httpStatus;
  }
}