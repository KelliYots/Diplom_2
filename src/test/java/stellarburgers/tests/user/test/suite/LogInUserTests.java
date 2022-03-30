package stellarburgers.tests.user.test.suite;

import io.qameta.allure.junit4.DisplayName;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.User;
import stellarburgers.UserRequestClient;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;


public class LogInUserTests {
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
    @DisplayName("Авторизация с данными существующего пользователя")
    @Description("Авторизация с данными существующего пользователя, возвращает 200 OK")
    public void loginUnderExistingUser() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.userLogIn();
        testRequestClient.getResponse()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным логином и паролем")
    @Description("Авторизация пользователя с неверным логином и паролем, возвращает 401 Unauthorized")
    public void loginWithIncorrectLoginAndPassword() {
        testRequestClient.userLogIn();
        testRequestClient.getResponse()
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }
}
