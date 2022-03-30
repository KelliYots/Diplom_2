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

public class ChangingUserDataTest {
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
    @DisplayName("Изменение данных авторизованного пользователя")
    @Description("Изменение данных авторизованного пользователя, возвращает 200 ОК")
    public void changingAuthorizedUserData() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.setAccessToken();
        testUser.createFuckerUser();
        testRequestClient.userDataRefresh(testRequestClient.getAccessToken());
        testRequestClient.getUserData();
        testRequestClient.getResponse()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение данных неавторизованного пользователя")
    @Description("Изменение данных неавторизованного пользователя, возвращает 401 Unauthorized")
    public void changingUnauthorizedUserData() {
        testRequestClient.userDataRefreshUnauthorised();
        testRequestClient.getResponse()
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false));
    }
}
