package stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.OrdersAndIngredientsRequestClient;
import stellarburgers.User;
import stellarburgers.UserRequestClient;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderTests {
    UserRequestClient testRequestClient = new UserRequestClient();
    User testUser = new User();
    OrdersAndIngredientsRequestClient testOrdersAndIngredientsClient = new OrdersAndIngredientsRequestClient();

    @Before
    public void setUp() {
        testUser.createFuckerUser();
        testOrdersAndIngredientsClient.setIngredientsList();
    }

    @After
    public void tearDown() {
        testRequestClient.delete();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Создание заказа с авторизацией, возвращает 200 ОК")
    public void creatingOrderWithAuthorization() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.setAccessToken();
        testOrdersAndIngredientsClient.createOrderAuth(testRequestClient.getAccessToken());
        testOrdersAndIngredientsClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .and()
                .body("name", notNullValue());

    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Создание заказа без авторизации, возвращает 401 Unauthorized")
    public void creatingOrderUnauth() {
        testRequestClient.userUniqueRegistration();
        testOrdersAndIngredientsClient.createOrderUnauth();
        testOrdersAndIngredientsClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Создание заказа без ингредиентов, возвращает 400 Bad Request")
    public void orderCreationWithoutIngredient() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.setAccessToken();
        testOrdersAndIngredientsClient.createOrderNoIngredient(testRequestClient.getAccessToken());
        testOrdersAndIngredientsClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Создание заказа с неверным хешем ингредиентов, возвращает 500 Internal Server Error")
    public void orderCreationWithInvalidIngredientHash() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.setAccessToken();
        testOrdersAndIngredientsClient.createOrderWithInvalidIngredientHash(testRequestClient.getAccessToken());
        testOrdersAndIngredientsClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Получение заказов конкретного авторизованного пользователя, возвращает 200 ОК")
    public void getOrdersByAuthorizedUser() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.setAccessToken();
        testOrdersAndIngredientsClient.createOrderAuth(testRequestClient.getAccessToken());
        testOrdersAndIngredientsClient.getOrderAuth(testRequestClient.getAccessToken());
        testOrdersAndIngredientsClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .and()
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    @Description("Получение заказов конкретного неавторизованного пользователя, возвращает 401 Unauthorized")
    public void getOrdersByUnauthorizedUser() {
        testOrdersAndIngredientsClient.getOrderUnauth();
        testOrdersAndIngredientsClient.getOrderResponse()
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
