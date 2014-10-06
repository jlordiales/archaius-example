package archaius.example;

import static com.netflix.config.ConfigurationManager.isConfigurationInstalled;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.PolledConfigurationSource;
import com.netflix.config.jmx.ConfigJMXManager;
import com.netflix.config.sources.JDBCConfigurationSource;

@Component
@Qualifier("applicationConfig")
public class ApplicationConfig extends Thread {
    static {
        System.setProperty("archaius.configurationSource.defaultFileName", "customConfig.properties");
    }

    private final DataSource dataSource;

    @Autowired
    public ApplicationConfig(DataSource dataSource) {
        this.dataSource = dataSource;
        cascadeDefaultConfiguration();
        DynamicConfiguration jdbcSource = installJdbcSource();
        registerMBean(jdbcSource);
    }

    public String getStringProperty(String key, String defaultValue) {
        final DynamicStringProperty property = DynamicPropertyFactory.getInstance().getStringProperty(key,
            defaultValue);
        return property.get();
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void registerMBean(DynamicConfiguration jdbcSource) {
        setDaemon(false);
        ConfigJMXManager.registerConfigMbean(jdbcSource);
    }

    private void cascadeDefaultConfiguration() {
        try {
            ConfigurationManager.loadCascadedPropertiesFromResources("customConfig");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DynamicConfiguration installJdbcSource() {
        if (!isConfigurationInstalled()) {
            PolledConfigurationSource source = new JDBCConfigurationSource(dataSource,
                "select distinct property_key, property_value from properties", "property_key", "property_value");
            DynamicConfiguration configuration = new DynamicConfiguration(source,
                new FixedDelayPollingScheduler(100, 1000, true));

            ConfigurationManager.install(configuration);
            return configuration;
        }
        return null;
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("archaiusContext.xml");
        ApplicationConfig applicationConfig = (ApplicationConfig) applicationContext.getBean("applicationConfig");

        applicationConfig.start();

        while (true) {
            try {
                System.out.println(applicationConfig.getStringProperty("hello.world.message", "default message"));
                System.out.println(applicationConfig.getStringProperty("cascade.property", "default message"));
                System.out.println(applicationConfig.getStringProperty("db.property", "default message"));
                sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
