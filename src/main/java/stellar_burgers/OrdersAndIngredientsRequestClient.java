package stellar_burgers;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class OrdersAndIngredientsRequestClient extends BaseSpec {
    private List<String> ingredientsList;
    private Response orderResponse;

    @Step("Извлечение тела ответа")
    public Response getOrderResponse() {
        return orderResponse;
    }

    @Step("Получение списка ингредиентов")
    public void setIngredientsList() {
        ingredientsList = given()
                .spec(BaseSpec.getBaseSpec())
                .get("ingredients")
                .then()
                .extract()
                .path("data._id");
    }

    @Step("Создание заказа с токеном авторизации")
    public void createOrderAuth(String accessToken) {
        Random random = new Random();
        String randomIngredientFromList = ingredientsList.get(random.nextInt(ingredientsList.size()));
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("ingredients", randomIngredientFromList);
        orderResponse = given()
                .spec(BaseSpec.getBaseSpec())
                .headers("authorization", accessToken)
                .body(dataMap)
                .when()
                .post("orders");
    }

    @Step("Создание заказа без токена авторизации")
    public void createOrderUnauth() {
        Random random = new Random();
        String randomIngredientFromList = ingredientsList.get(random.nextInt(ingredientsList.size()));
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("ingredients", randomIngredientFromList);
        orderResponse = given()
                .spec(BaseSpec.getBaseSpec())
                .body(dataMap)
                .when()
                .post("orders");
    }

    @Step("Создание заказа без ингредиентов")
    public void createOrderNoIngredient(String accessToken) {
        orderResponse = given()
                .spec(BaseSpec.getBaseSpec())
                .headers("authorization", accessToken)
                .when()
                .post("orders");
    }

    @Step("Создание заказа с неверным хэшэм ингредиентов")
    public void createOrderWithInvalidIngredientHash(String accessToken) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("ingredients", "invalidHash");
        orderResponse = given()
                .spec(BaseSpec.getBaseSpec())
                .headers("authorization", accessToken)
                .body(dataMap)
                .when()
                .post("orders");
    }

    @Step("Получение заказов конкретного авторизированного пользователя")
    public void getOrderAuth(String accessToken) {
        orderResponse = given()
                .spec(BaseSpec.getBaseSpec())
                .headers("authorization", accessToken)
                .when()
                .get("orders");
    }

    @Step("Получение заказов неавторизованного пользователя")
    public void getOrderUnauth() {
        orderResponse = given()
                .spec(BaseSpec.getBaseSpec())
                .when()
                .get("orders");
    }
}
