package com.restapi.styleswap.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resource;
    private final String fieldName;
    private final Object value;

    public ResourceNotFoundException(String resource, String fieldName, Object value) {
        super(resource+" not found with "+fieldName+" = "+ value);
        this.resource = resource;
        this.fieldName = fieldName;
        this.value = value;
    }

    public ResourceNotFoundException(String resource, String fieldName) {
        super(resource+" not found with name "+fieldName);
        this.resource = resource;
        this.fieldName = fieldName;
        this.value = null;
    }
}
