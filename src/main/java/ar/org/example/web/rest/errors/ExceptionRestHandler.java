package ar.org.example.web.rest.errors;

import ar.org.example.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class ExceptionRestHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    @ExceptionHandler({Exception.class})
    public final ResponseEntity<FieldError> handleAllExceptions(Exception ex, WebRequest request) {
        FieldError fieldError = new FieldError(new Date(), ex.getMessage(), request.getDescription(false));
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(fieldError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MultipartException.class})
    public final ResponseEntity<FieldError> handlerMultipartException(MultipartException ex, WebRequest request) {
        FieldError fieldError = new FieldError(new Date(), ex.getMessage(), request.getDescription(false));
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(fieldError, HttpStatus.NOT_FOUND);
    }
}
