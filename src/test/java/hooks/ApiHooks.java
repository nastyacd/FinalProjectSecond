package hooks;

import io.cucumber.java.Before;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApiHooks {
    public static Properties property = new Properties();

    @Before
    public void before() {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/application.properties");
            property.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RestAssured.filters(new AllureRestAssured()); //  к каждому шагу добавляет текст запроса и какой пришел ответ
    }
}
