package stellar_burgers_tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellar_burgers.OrdersAndIngredientsRequestClient;
import stellar_burgers.User;
import stellar_burgers.UserRequestClient;

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
    @DisplayName("Creating an order with authorization")
    @Description("Создание заказа с авторизацией, возвращает 200 ОК")
    public void creatingOrderWithAuthorization() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.setAccessToken();
        testOrdersAndIngredientsClient.createOrderAuth(testRequestClient.getAccessToken());
        testOrdersAndIngredientsClient.getOrderResponse()
                .then()
                .assertThat()
                .body("success", equalTo(true))
                .and()
                .body("name", notNullValue())
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Creating order without authorization")
    @Description("Создание заказа без авторизации, возвращает 401 Unauthorized")
    public void creatingOrderUnauth() {
        testRequestClient.userUniqueRegistration();
        testOrdersAndIngredientsClient.createOrderUnauth();
        testOrdersAndIngredientsClient.getOrderResponse()
                .then()
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Creating order without ingredients")
    @Description("Создание заказа без ингредиентов, возвращает 400 Bad Request")
    public void orderCreationWithoutIngredient() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.setAccessToken();
        testOrdersAndIngredientsClient.createOrderNoIngredient(testRequestClient.getAccessToken());
        testOrdersAndIngredientsClient.getOrderResponse()
                .then()
                .assertThat()
                .body("message", equalTo("Ingredient ids must be provided"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Creating an order with an incorrect hash of ingredients")
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
    @DisplayName("Receiving orders from a specific authorized user")
    @Description("Получение заказов конкретного авторизованного пользователя, возвращает 200 ОК")
    public void getOrdersByAuthorizedUser() {
        testRequestClient.userUniqueRegistration();
        testRequestClient.setAccessToken();
        testOrdersAndIngredientsClient.createOrderAuth(testRequestClient.getAccessToken());
        testOrdersAndIngredientsClient.getOrderAuth(testRequestClient.getAccessToken());
        testOrdersAndIngredientsClient.getOrderResponse()
                .then()
                .assertThat()
                .body("success", equalTo(true))
                .and()
                .body("orders", notNullValue())
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Receiving orders from a specific unauthorized user")
    @Description("Получение заказов конкретного неавторизованного пользователя, возвращает 401 Unauthorized")
    public void getOrdersByUnauthorizedUser() {
        testOrdersAndIngredientsClient.getOrderUnauth();
        testOrdersAndIngredientsClient.getOrderResponse()
                .then()
                .assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }
}
