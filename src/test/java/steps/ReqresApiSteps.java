package steps;

import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static hooks.ApiHooks.property;
import static io.restassured.RestAssured.given;

public class ReqresApiSteps {
    private static final String JSON_PATH = "src/test/resources/json/";
    private static RequestSpecification reqresSpecification = new RequestSpecBuilder().setBaseUri(property.getProperty("reqres.url")).setContentType("application/json").build();

    private static String createClientFile;
    private static String createClientBody;
    private static String createClientId;

    @Когда("^создаем клиена с информацией из файла \"([^\"]*)\"$")
    public static void postClient(String file) {
        try {
            createClientFile = new String(Files.readAllBytes(Paths.get(JSON_PATH + property.getProperty(file))));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Response clientInfo = given()
                .spec(reqresSpecification)
                .body(createClientFile)
                .when()
                .post("/api/users")
                .then()
                .extract()
                .response();
        Assert.assertEquals("Ошибка статус-кода", 201, clientInfo.getStatusCode());
        createClientBody = clientInfo.getBody().asString();
        createClientId = new JSONObject(createClientBody).get("id").toString();
    }

    @И("проверка клиента")
    public static void checkClient() {
        String clientName = new JSONObject(createClientBody).get("name").toString();
        String clientJob = new JSONObject(createClientBody).get("job").toString();

        String clientNameFile = new JSONObject(createClientFile).get("name").toString();
        String clientJobFile = new JSONObject(createClientFile).get("job").toString();
        Assert.assertEquals("Разные названия", clientName, clientNameFile);
        Assert.assertEquals("Разное действие", clientJob, clientJobFile);
    }

    @Тогда("^изменяем клиента с информацией из файла \"([^\"]*)\"$")
    public static void putClient(String file) {
        try {
            createClientFile = new String(Files.readAllBytes(Paths.get(JSON_PATH + property.getProperty(file))));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Response clientInfo = given()
                .spec(reqresSpecification)
                .body(createClientFile)
                .when()
                .put("/api/users/" + createClientId)
                .then()
                .extract()
                .response();
        Assert.assertEquals("Ошибка статус-кода", 200, clientInfo.getStatusCode());
        createClientBody = clientInfo.getBody().asString();
    }
}
