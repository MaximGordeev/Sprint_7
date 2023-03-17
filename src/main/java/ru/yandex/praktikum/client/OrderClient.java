package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.client.base.ScooterRestClient;
import ru.yandex.praktikum.model.OrderCreate;

import static io.restassured.RestAssured.given;

public class OrderClient extends ScooterRestClient {
    private static final String ORDER = BASE_URI + "orders/";
    private static final String CANCEL_ORDER = BASE_URI + "orders/cancel";

    @Step("Create order")
    public ValidatableResponse createNewOrder(OrderCreate orderCreate) {
        return given()
                .spec(getBaseReqSpec())
                .body(orderCreate)
                .when()
                .post(ORDER)
                .then();
    }
    @Step("Get orders list")
    public ValidatableResponse getOrdersList() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(ORDER)
                .then();
    }

    @Step("Cancel order")
    public ValidatableResponse cancelOrder(Object track) {
        return given()
                .spec(getBaseReqSpec())
                .body(track)
                .when()
                .put(CANCEL_ORDER)
                .then();
    }

}
