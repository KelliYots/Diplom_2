package stellarburgers.tests.user.test.suite;

import io.qameta.allure.junit4.DisplayName;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.BaseSpec;
import stellarburgers.User;
import stellarburgers.UserRequestClient;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTests {
    UserRequestClient testRequestClient = new UserRequestClient();
    User testUser = new User();

    @Before
    public void setUp() {
        testUser.createFuckerUser();
    }

    @After
    public void tearDown() {
        testRequestClient.delete();
    }

    @Test
    @DisplayName("Регистрация нового уникального пользователя")
    @Description("Регистрация нового уникального пользователя, возвращает 200 OK")
    public void userCreationIsPossible() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.getResponse()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Регистрация пользователя-допельгангера")
    @Description("Регистрация двух пользователей с одинаковыми данными, возвращает 403 Forbidden")
    public void registerDoppelgangerUser() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.userUniqueRegistration();
        testRequestClient.getResponse()
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("")
    @Description("Регистрация пользователя без email невозможгна, возвращает 403 Forbidden")
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
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("")
    @Description("Регистрация пользователя без пароля невозможна, возвращает 403 Forbidden")
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
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация пользователя без имени")
    @Description("Регистрация пользователя без имени невозможна, возвращает 403 Forbidden")
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
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
