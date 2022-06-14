package net.flawe.practical.accounting.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ProductCountException extends RuntimeException {

    public ProductCountException() {
    }

    public ProductCountException(String message) {
        super(message);
    }

    public ProductCountException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductCountException(Throwable cause) {
        super(cause);
    }
}
