package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.OrderCreate;

import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderParamTest {
    private OrderClient orderClient;
    private final List<String> color;
    private Object track;

    public OrderParamTest(List<String> color) {
        this.color = color;
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Parameterized.Parameters(name = "Scooter color: {0}")
    public static Object[][] getColor() {
        return new Object[][] {
                {List.of("BLACK")},
                {List.of("GRAY")},
                {Arrays.asList("BLACK", "GRAY")},
                {List.of("")}
        };
    }

    @Test
    @DisplayName("Create order with different color")
    @Description("with Black or Grey, Black and Grey, without color")
    public void orderWithDifferentColorOrWithoutIt() {
        OrderCreate orderCreate = new OrderCreate(color);
        ValidatableResponse responseCreateOrder = orderClient.createNewOrder(orderCreate);
        track = responseCreateOrder.extract().path("track");
        responseCreateOrder.assertThat()
                .statusCode(SC_CREATED)
                .body("track", notNullValue());
    }

    @After
    @Step("Cancel order")
    public void cancelOrder() {
        orderClient.cancelOrder(track);
    }
}
