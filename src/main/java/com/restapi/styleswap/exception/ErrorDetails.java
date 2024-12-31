package com.restapi.styleswap.exception;
import java.util.Date;

public record ErrorDetails(Date timestamp, String message, String details) {
}