package uk.gov.dwp.jsa.notification.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import uk.gov.dwp.jsa.adaptors.http.api.ApiResponse;
import uk.gov.dwp.jsa.adaptors.services.ResponseBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Class responsible for rendering of unhandled errors or application-container wise exceptions.
 */
@Controller
public class CustomErrorController implements ErrorController {

    private ErrorAttributes errorAttributes;

    @Autowired
    public CustomErrorController(final ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @ResponseBody
    @GetMapping(value = "/error", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ApiResponse<String>> handleError(final HttpServletRequest request) {
        final Integer status = readHttpStatus(request);
        final String description = HttpStatus.valueOf(status).getReasonPhrase();
        return new ResponseBuilder<String>()
                .withStatus(HttpStatus.valueOf(status))
                .withApiError(String.valueOf(status), description)
                .build();
    }

    private Integer readHttpStatus(final HttpServletRequest request) {
        final Map<String, Object> attributes = errorAttributes.getErrorAttributes(
                new ServletWebRequest(request), Boolean.FALSE
        );
        return (Integer) attributes.get("status");
    }

    /**
     * @return the default spring error path
     */
    @Override
    public String getErrorPath() {
        return "/error";
    }

}
