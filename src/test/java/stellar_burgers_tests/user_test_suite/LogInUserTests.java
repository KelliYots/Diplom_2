package stellar_burgers_tests.user_test_suite;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellar_burgers.User;
import stellar_burgers.UserRequestClient;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;


public class LogInUserTests {
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
    @DisplayName("Login under existing user")
    @Description("Авторизация с данными существующего пользователя, возвращает 200 OK")
    public void loginUnderExistingUser() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.userLogIn();
        testRequestClient.getResponse()
                .then()
                .assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Login with an invalid username and password")
    @Description("Авторизация пользователя с неверным логином и паролем, возвращает 401 Unauthorized")
    public void loginWithIncorrectLoginAndPassword() {
        testRequestClient.userLogIn();
        testRequestClient.getResponse()
                .then()
                .assertThat()
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }
}
