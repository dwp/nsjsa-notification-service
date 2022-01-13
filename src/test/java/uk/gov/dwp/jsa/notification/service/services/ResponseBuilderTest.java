package uk.gov.dwp.jsa.notification.service.services;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.jsa.adaptors.http.api.ApiError;
import uk.gov.dwp.jsa.adaptors.http.api.ApiResponse;
import uk.gov.dwp.jsa.adaptors.http.api.ApiSuccess;
import uk.gov.dwp.jsa.adaptors.services.ResponseBuilder;

import java.net.URI;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ResponseBuilderTest {

    @Test
    public void with_status_should_build() {
        ResponseBuilder builder = new ResponseBuilder();
        builder.withStatus(HttpStatus.OK);
        ResponseEntity<ApiResponse> build = builder.build();
        assertEquals(HttpStatus.OK, build.getStatusCode());
    }

    @Test(expected = NullPointerException.class)
    public void without_status_build_should_fail() {
        ResponseBuilder builder = new ResponseBuilder();
        builder.build();
    }

    @Test
    public void with_api_error_should_build_and_contain_expected_body() {
        ResponseBuilder<String> builder = new ResponseBuilder<>();
        builder.withStatus(HttpStatus.OK)
                .withApiError(new ApiError("code", "message"));

        ResponseEntity<ApiResponse<String>> build = builder.build();

        assertEquals("message", build.getBody().getError().getMessage());
        assertEquals("code", build.getBody().getError().getCode());
    }

    @Test
    public void with_simplified_constructor_api_error_should_build_and_contain_expected_body() {
        ResponseBuilder<String> builder = new ResponseBuilder<>();
        builder.withStatus(HttpStatus.OK).withApiError("code", "message");

        ResponseEntity<ApiResponse<String>> build = builder.build();

        assertEquals("message", build.getBody().getError().getMessage());
        assertEquals("code", build.getBody().getError().getCode());
    }

    @Test
    public void with_success_data_should_build_and_contain_expected_body() {
        ResponseBuilder<String> builder = new ResponseBuilder<>();
        builder.withStatus(HttpStatus.OK)
                .withSuccessData(new ApiSuccess<>(URI.create("http://test"), "data"));

        ResponseEntity<ApiResponse<String>> build = builder.build();

        assertEquals("data", build.getBody().getSuccess().get(0).getData());
        assertEquals(URI.create("http://test"), build.getBody().getSuccess().get(0).getPath());
    }

    @Test
    public void with_simplified_constructor_success_data_should_build_and_contain_expected_body() {
        ResponseBuilder<String> builder = new ResponseBuilder<>();
        builder.withStatus(HttpStatus.OK)
                .withSuccessData(URI.create("http://test"), "data");

        ResponseEntity<ApiResponse<String>> build = builder.build();

        assertEquals("data", build.getBody().getSuccess().get(0).getData());
        assertEquals(URI.create("http://test"), build.getBody().getSuccess().get(0).getPath());
    }

    @Test
    public void with_list_constructor_success_data_should_build_and_contain_expected_body() {
        ResponseBuilder<String> builder = new ResponseBuilder<>();
        builder.withStatus(HttpStatus.OK)
                .withSuccessData(Arrays.asList(
                        new ApiSuccess<>(URI.create("http://test1"), "data1"),
                        new ApiSuccess<>(URI.create("http://test2"), "data2")
                )).build();

        ResponseEntity<ApiResponse<String>> build = builder.build();

        assertEquals("data1", build.getBody().getSuccess().get(0).getData());
        assertEquals(URI.create("http://test1"), build.getBody().getSuccess().get(0).getPath());
        assertEquals("data2", build.getBody().getSuccess().get(1).getData());
        assertEquals(URI.create("http://test2"), build.getBody().getSuccess().get(1).getPath());

    }

}
