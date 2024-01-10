package com.example.demo.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.util.Constants;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleContentNotAllowedException(Exception ex) {
		return new ResponseEntity<>(Map.of(Constants.MESSAGE, ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

}