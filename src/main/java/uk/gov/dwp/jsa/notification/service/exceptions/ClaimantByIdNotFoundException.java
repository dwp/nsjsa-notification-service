package uk.gov.dwp.jsa.notification.service.exceptions;

import org.springframework.http.HttpStatus;

public class ClaimantByIdNotFoundException extends RuntimeException {
    static final String CODE = HttpStatus.NOT_FOUND.toString();
    static final String MESSAGE = "Could not find a claimant by id";
}
