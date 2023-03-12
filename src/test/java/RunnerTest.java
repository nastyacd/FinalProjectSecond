import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        tags = "@TEST",
        features = "src/test/resources/features",
        glue = {"steps", "hooks"},
        plugin = {"io.qameta.allure.cucumber6jvm.AllureCucumber6Jvm"} // ???????? ??????
)

public class RunnerTest {
}
