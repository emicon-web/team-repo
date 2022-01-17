package com.bezkoder.springjwt.errors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionController {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionController.class);

    @ExceptionHandler(GenericCustomException.class)
    public ResponseEntity<ExceptionResponse> customHandleNotFound(Exception ex, WebRequest request) {

        logger.error("GlobalExceptionController -> Generic custom exception: ", ex);
        ExceptionResponse errors = new ExceptionResponse();
        errors.setDateTime(new Date());
        errors.setMessage(ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllException(Exception ex) {

        logger.error("GlobalExceptionController -> Exception: ", ex);
        ExceptionResponse errors = new ExceptionResponse();
        errors.setDateTime(new Date());
        errors.setMessage("General Error");
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        logger.error("GlobalExceptionController -> MaxUploadSizeExceededException: ", ex);
        ExceptionResponse errors = new ExceptionResponse();
        errors.setDateTime(new Date());
        errors.setMessage("File too large, file size can not exceed 10MB");
        return new ResponseEntity<>(errors, HttpStatus.EXPECTATION_FAILED);
    }

}