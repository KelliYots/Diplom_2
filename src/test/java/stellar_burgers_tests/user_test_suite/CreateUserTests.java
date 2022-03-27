package stellar_burgers_tests.user_test_suite;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellar_burgers.BaseSpec;
import stellar_burgers.User;
import stellar_burgers.UserRequestClient;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTests {
    UserRequestClient testRequestClient = new UserRequestClient();
    User testUser = new User();

    @Step
    @Before
    public void setUp() {
        testUser.createFuckerUser();
    }

    @Step
    @After
    public void tearDown() {
        testRequestClient.delete();
    }

    @Test
    @DisplayName("Registration a unique user")
    @Description("Register a new unique profile returns 200 OK")
    public void userCreationIsPossible() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.getResponse()
                .then()
                .assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Register doppelganger user")
    @Description("Register two users with the same data")
    public void registerDoppelgangerUser() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.userUniqueRegistration();
        testRequestClient.getResponse()
                .then()
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("")
    @Description("")
    public void userCreationWithoutEmailNotPossible() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("password", testUser.getPassword());
        dataMap.put("name", testUser.getName());
        testRequestClient.setResponse(given()
                .spec(BaseSpec.getBaseSpec())
                .body(dataMap)
                .when()
                .post("auth/register"));
        testRequestClient.getResponse()
                .then().assertThat()
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("")
    @Description("")
    public void userCreationWithoutPasswordNotPossible() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("password", testUser.getEmail());
        dataMap.put("name", testUser.getName());
        testRequestClient.setResponse(given()
                .spec(BaseSpec.getBaseSpec())
                .body(dataMap)
                .when()
                .post("auth/register"));
        testRequestClient.getResponse()
                .then().assertThat()
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("")
    @Description("")
    public void userCreationWithoutNameNotPossible() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("password", testUser.getEmail());
        dataMap.put("name", testUser.getPassword());
        testRequestClient.setResponse(given()
                .spec(BaseSpec.getBaseSpec())
                .body(dataMap)
                .when()
                .post("auth/register"));
        testRequestClient.getResponse()
                .then().assertThat()
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(SC_FORBIDDEN);
    }
}
