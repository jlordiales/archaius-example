package archaius.example;

import static com.netflix.config.ConfigurationManager.isConfigurationInstalled;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.PolledConfigurationSource;
import com.netflix.config.sources.JDBCConfigurationSource;

@Component
public class ApplicationConfig {
    static {
        System.setProperty("archaius.configurationSource.defaultFileName", "customConfig.properties");
    }

    private final DataSource dataSource;

    @Autowired
    public ApplicationConfig(DataSource dataSource) {
        this.dataSource = dataSource;
        cascadeDefaultConfiguration();
        installJdbcSource();
    }

    public String getStringProperty(String key, String defaultValue) {
        final DynamicStringProperty property = DynamicPropertyFactory.getInstance().getStringProperty(key,
            defaultValue);
        return property.get();
    }

    private void cascadeDefaultConfiguration() {
        try {
            ConfigurationManager.loadCascadedPropertiesFromResources("customConfig");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void installJdbcSource() {
        if (!isConfigurationInstalled()) {
            PolledConfigurationSource source = new JDBCConfigurationSource(dataSource,
                "select distinct property_key, property_value from properties", "property_key", "property_value");
            DynamicConfiguration configuration = new DynamicConfiguration(source,
                new FixedDelayPollingScheduler(100, 1000, true));

            ConfigurationManager.install(configuration);
        }
    }
}
