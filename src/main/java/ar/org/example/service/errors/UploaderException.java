package ar.org.example.service.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UploaderException extends RuntimeException {

    private String objectName;

    private String errorKey;

    public UploaderException(String exception) {
        super(exception);
    }

    public UploaderException(String exception, String objectName, String errorKey) {
        super(exception);
        this.objectName = objectName;
        this.errorKey = errorKey;
    }

    public String getObjectNamee() {
        return objectName;
    }

    public String getErrorKey() {
        return errorKey;
    }

}
