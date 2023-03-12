package steps;

import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.junit.Assert;

import static hooks.ApiHooks.property;
import static io.restassured.RestAssured.given;

public class RickAndMortyApiSteps {
    private static RequestSpecification rickSpecification = new RequestSpecBuilder().setBaseUri(property.getProperty("rick.url")).build();
    public static String info;
    public static String lastInfo;
    public static String charId;
    public static int lastEpisode;

    @Когда("получаем информацию персоонажа по номеру \"([^\"]*)\" из properties$")
    public static void getCharacter(String propertyName) {
        Response gettingInfo = given()
                .spec(rickSpecification)
                .when()
                .get("/character/" + property.getProperty(propertyName))
                .then()
                .extract()
                .response();
        info = gettingInfo.getBody().asString();
        charId = new JSONObject(info).get("id").toString();
    }

    @И("получаем последний эпизод в котором участвовал персонаж")
    public static void getLastEpisode() {
        Response gettingLastEpisode = given()
                .spec(rickSpecification)
                .when()
                .get("/character/" + charId)
                .then()
                .extract()
                .response();
        lastEpisode = new JSONObject(gettingLastEpisode.getBody().asString()).getJSONArray("episode").length();
    }

    @И("получаем информацию последнего персоонажа из последнего эпизода")
    public static void getLastCharacter() {
        Response gettingLastCharacter = given()
                .spec(rickSpecification)
                .when()
                .get("/episode/" + lastEpisode)
                .then()
                .extract()
                .response();
        int index = new JSONObject(gettingLastCharacter.getBody().asString()).getJSONArray("characters").length() - 1;
        String characterNum = new JSONObject(gettingLastCharacter.getBody().asString()).getJSONArray("characters").get(index).toString();
        characterNum = characterNum.replaceAll(property.getProperty("rick.url") + "/character/","");

        Response gettingInfo = given()
                .spec(rickSpecification)
                .when()
                .get("/character/" + characterNum)
                .then()
                .extract()
                .response();

        lastInfo = gettingInfo.getBody().asString();
    }

    @Тогда("сравниваем рассы и локации у персоонажа и послежнего персоонажа")
    public static void checkSpeciesAndLocation() {
        String speciesLastCharacter = new JSONObject(lastInfo).get("species").toString();
        String speciesMorty = new JSONObject(info).get("species").toString();

        String locationLastCharacter = new JSONObject(lastInfo).getJSONObject("location").get("name").toString();
        String locationMorty = new JSONObject(info).getJSONObject("location").get("name").toString();

        Assert.assertEquals("Разные расы", speciesLastCharacter, speciesMorty);
        Assert.assertEquals("Разные локации", locationLastCharacter, locationMorty);
    }
}
