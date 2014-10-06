package archaius.example;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/archaiusContext.xml" })
public class ApplicationConfigTest {

    @Autowired
    private ApplicationConfig appConfig;

    @Autowired
    private JdbcTemplate template;

    @Test
    public void shouldRetrieveThePropertyByKey() {
        String property = appConfig.getStringProperty("hello.world.message", "default message");

        assertThat(property, is("Hello Archaius World!"));
    }

    @Test
    public void shouldRetrieveDefaultValueWhenKeyIsNotPresent() {
        String property = appConfig.getStringProperty("some.key", "default message");

        assertThat(property, is("default message"));
    }

    @Test
    public void shouldReadCascadeConfigurationFiles() {
        String property = appConfig.getStringProperty("cascade.property", "not found");

        assertThat(property, is("cascade value"));
    }
    @Test
    public void shouldRetrievePropertyFromDB() {
        String property = appConfig.getStringProperty("db.property", "default message");

        assertThat(property, is("this is a db property"));
    }

    @Test
    public void shouldReadTheNewValueAfterTheSpecifiedDelay() throws InterruptedException {
        template.update("update properties set property_value = 'changed value' where property_key = 'db.property'");
        String property = appConfig.getStringProperty("db.property", "default message");

        //We updated the value on the DB but Archaius polls for changes every 1000 milliseconds so it still sees the old value
        assertThat(property, is("this is a db property"));

        Thread.sleep(1500);

        property = appConfig.getStringProperty("db.property", "default message");
        assertThat(property, is("changed value"));
    }
}
