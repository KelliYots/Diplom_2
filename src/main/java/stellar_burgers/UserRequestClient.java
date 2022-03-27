package stellar_burgers;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;


public class UserRequestClient {
    User userData = new User();
    private String accessToken;
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken() {
        accessToken = response.then().extract().path("accessToken");
    }

    @Step("Регистрация нового уникального пользователя")
    public void userUniqueRegistration() {
        response = given()
                .spec(BaseSpec.getBaseSpec())
                .log().all()
                .body(userData.createFuckerUser())
                .when()
                .post("auth/register");
    }

    @Step("Вход в учетную запись пользователя по email  и паролю")
    public void userLogIn() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("email", userData.getEmail());
        dataMap.put("password", userData.getPassword());
        response = given()
                .spec(BaseSpec.getBaseSpec())
                .body(dataMap)
                .when()
                .log().all()
                .post("auth/login");
    }

    @Step("Получение информации о пользователе")
    public void getUserData() {
        response = given()
                .spec(BaseSpec.getBaseSpec())
                .headers("Authorization", accessToken)
                .when()
                .get("auth/user");
    }

    @Step("Обновление данных авторизованного пользователя - смена пароля")
    public void userDataRefresh(String accessToken) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("password", "stopWarInUkraine");
        response = given()
                .spec(BaseSpec.getBaseSpec())
                .headers("Authorization", accessToken)
                .body(dataMap)
                .when()
                .log().all()
                .patch("auth/user");
    }

    @Step("Обновление данных пользователя без авторизации")
    public void userDataRefreshUnauthorised() {
        response = given()
                .spec(BaseSpec.getBaseSpec())
                .body(userData.createFuckerUser())
                .when()
                .patch("auth/user");
    }

    @Step("Удаление пользователя")
    public void delete() {
        if (getAccessToken() == null) return;
        given()
                .spec(BaseSpec.getBaseSpec())
                .headers("Authorization", accessToken)
                .when()
                .delete("auth/user")
                .then()
                .statusCode(SC_ACCEPTED);
        System.out.println(getAccessToken());
    }
}
