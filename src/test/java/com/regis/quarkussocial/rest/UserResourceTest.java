package com.regis.quarkussocial.rest;

import com.regis.quarkussocial.dto.CreateUserRequest;
import com.regis.quarkussocial.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiUrl;

    @Test
    @Order(1)
    @DisplayName("should create an user successfuly")
    public void createUserTest() {
        var user = new CreateUserRequest();
        user.setName("Fulano");;
        user.setAge(30);

        Response response =
                given()
                    .contentType(ContentType.JSON)
                    .body(user)
                .when()
                    .post(apiUrl)
                .then()
                    .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @Order(2)
    @DisplayName("Should return error when json is not valid")
    public void createUserValidationErrorTest() {
        var user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);

        var response =
                given()
                    .contentType(ContentType.JSON)
                    .body(user)
                .when()
                    .post(apiUrl)
                .then()
                    .extract().response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
    }

    @Test
    @Order(3)
    @DisplayName("should list all users")
    public void listAllUsersTest() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(apiUrl)
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

}