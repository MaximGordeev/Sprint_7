package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.OrderCreate;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderParamTest {
    private OrderClient orderClient;
    private final String[] color;
    private Object track;

    public OrderParamTest(String[] color) {
        this.color = color;
    }

    @BeforeClass
    public static void globalSetup() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Parameterized.Parameters(name = "Scooter color: {0}")
    public static Object[][] getColor() {
        return new Object[][] {
                {new String[]{"BLACK"}},
                {new String[]{"GRAY"}},
                {new String[]{"BLACK", "GRAY"}},
                {new String[]{}}
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
