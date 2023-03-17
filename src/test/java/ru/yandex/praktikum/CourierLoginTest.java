package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.CourierCredentials;
import ru.yandex.praktikum.model.CourierGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierLoginTest {
    private CourierClient courierClient;
    private int courierId;

    @BeforeClass
    public static void globalSetup() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @After
    public void clearData() {
        if(courierId != 0) courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Login courier with successfull credentials")
    @Description("Login success")
    public void courierCanLoginWithValidData() {
        Courier courier = CourierGenerator.getRandom();
        courierClient.create(courier);

        courierId = courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("id", notNullValue())
                .extract().path("id");
    }

    @Test
    @DisplayName("Courier authorization with an empty password field")
    @Description("password field is empty")
    public void courierNotLoginWithEmptyPasswordField() {
        Courier courier = CourierGenerator.getRandom();
        courierClient.create(courier);
        courier.setPassword("");
        courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Courier authorization with an empty login field")
    @Description("login field is empty")
    public void courierNotLoginWithEmptyLoginField() {
        Courier courier = CourierGenerator.getRandom();
        courierClient.create(courier);
        courier.setLogin("");
        courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat()
                .body("message", equalTo("Недостаточно данных для входа"));
    }
    @Test
    @DisplayName("Courier authorization with not valid data")
    @Description("login and password not valid")
    public void courierNotLoginWithNotValidData() {
        Courier courier = CourierGenerator.getRandom();

        courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .statusCode(SC_NOT_FOUND)
                .and()
                .assertThat()
                .body("message", equalTo("Учетная запись не найдена"));
    }

}
