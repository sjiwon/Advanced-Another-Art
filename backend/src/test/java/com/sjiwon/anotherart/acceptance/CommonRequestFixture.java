package com.sjiwon.anotherart.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.MULTIPART;

public class CommonRequestFixture {
    public static ValidatableResponse getRequest(final String path) {
        return request(given -> given
                .contentType(JSON)
                .when()
                .get(path)
        );
    }

    public static ValidatableResponse getRequest(final String accessToken, final String path) {
        return request(given -> given
                .contentType(JSON)
                .auth().oauth2(accessToken)
                .when()
                .get(path)
        );
    }

    public static ValidatableResponse postRequest(final String path) {
        return request(given -> given
                .contentType(JSON)
                .when()
                .post(path)
        );
    }

    public static ValidatableResponse postRequest(final Object body, final String path) {
        return request(given -> given
                .contentType(JSON)
                .body(body)
                .when()
                .post(path)
        );
    }

    public static ValidatableResponse postRequest(final String accessToken, final String path) {
        return request(given -> given
                .contentType(JSON)
                .auth().oauth2(accessToken)
                .when()
                .post(path)
        );
    }

    public static ValidatableResponse postRequest(final String accessToken, final Object body, final String path) {
        return request(given -> given
                .contentType(JSON)
                .auth().oauth2(accessToken)
                .body(body)
                .when()
                .post(path)
        );
    }

    public static ValidatableResponse multipartRequest(final String fileName, final String path) {
        return request(given -> given
                .contentType(MULTIPART)
                .multiPart("file", getFile(fileName))
                .when()
                .post(path)
        );
    }

    public static ValidatableResponse multipartRequest(final Map<String, String> params, final String path) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .request();
        params.keySet().forEach(paramKey -> request.multiPart(paramKey, params.get(paramKey)));

        return request.post(path)
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(
            final String fileName,
            final Map<String, String> params,
            final String path
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .multiPart("file", getFile(fileName))
                .request();
        params.keySet().forEach(paramKey -> request.multiPart(paramKey, params.get(paramKey)));

        return request.post(path)
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(
            final String fileName,
            final String accessToken,
            final String path
    ) {
        return request(given -> given
                .contentType(MULTIPART)
                .auth().oauth2(accessToken)
                .multiPart("file", getFile(fileName))
                .when()
                .post(path)
        );
    }

    public static ValidatableResponse multipartRequest(
            final Map<String, String> params,
            final String accessToken,
            final String path
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .auth().oauth2(accessToken)
                .request();
        params.keySet().forEach(paramKey -> request.multiPart(paramKey, params.get(paramKey)));

        return request.post(path)
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(
            final String fileName,
            final Map<String, String> params,
            final String accessToken,
            final String path
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .auth().oauth2(accessToken)
                .multiPart("file", getFile(fileName))
                .request();
        params.keySet().forEach(paramKey -> request.multiPart(paramKey, params.get(paramKey)));

        return request.post(path)
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(final List<String> fileNames, final String path) {
        final RequestSpecification request = RestAssured.given().log().all()
                .request();
        fileNames.forEach(fileName -> request.multiPart("files", getFile(fileName)));

        return request.post(path)
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(
            final List<String> fileNames,
            final Map<String, String> params,
            final String path
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .request();
        fileNames.forEach(fileName -> request.multiPart("files", getFile(fileName)));
        params.keySet().forEach(paramKey -> request.multiPart(paramKey, params.get(paramKey)));

        return request.post(path)
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(
            final List<String> fileNames,
            final String accessToken,
            final String path
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .auth().oauth2(accessToken)
                .request();
        fileNames.forEach(fileName -> request.multiPart("files", getFile(fileName)));

        return request.post(path)
                .then().log().all();
    }

    public static ValidatableResponse multipartRequest(
            final List<String> fileNames,
            final Map<String, String> params,
            final String accessToken,
            final String path
    ) {
        final RequestSpecification request = RestAssured.given().log().all()
                .contentType(MULTIPART)
                .auth().oauth2(accessToken)
                .request();
        fileNames.forEach(fileName -> request.multiPart("files", getFile(fileName)));
        params.keySet().forEach(paramKey -> request.multiPart(paramKey, params.get(paramKey)));

        return request.post(path)
                .then().log().all();
    }

    public static ValidatableResponse patchRequest(final String path) {
        return request(given -> given
                .contentType(JSON)
                .when()
                .patch(path)
        );
    }

    public static ValidatableResponse patchRequest(final String accessToken, final String path) {
        return request(given -> given
                .contentType(JSON)
                .auth().oauth2(accessToken)
                .when()
                .patch(path)
        );
    }

    public static ValidatableResponse patchRequest(final String accessToken, final Object body, final String path) {
        return request(given -> given
                .contentType(JSON)
                .auth().oauth2(accessToken)
                .body(body)
                .when()
                .patch(path)
        );
    }

    public static ValidatableResponse deleteRequest(final String path) {
        return request(given -> given
                .contentType(JSON)
                .when()
                .delete(path)
        );
    }

    public static ValidatableResponse deleteRequest(final String accessToken, final String path) {
        return request(given -> given
                .contentType(JSON)
                .auth().oauth2(accessToken)
                .when()
                .delete(path)
        );
    }

    private static ValidatableResponse request(final Function<RequestSpecification, Response> function) {
        final RequestSpecification given = RestAssured.given().log().all();
        final Response response = function.apply(given);
        return response.then().log().all();
    }

    private static File getFile(final String fileName) {
        final String BASE_PATH = "arts/";
        try {
            return new ClassPathResource(BASE_PATH + fileName).getFile();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
