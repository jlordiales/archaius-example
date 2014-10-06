package archaius.example;

import java.io.IOException;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

public class ApplicationConfig {
    static {
        System.setProperty("archaius.configurationSource.defaultFileName", "customConfig.properties");
    }

    public ApplicationConfig() {
        cascadeDefaultConfiguration();
    }

    private void cascadeDefaultConfiguration() {
        try {
            ConfigurationManager.loadCascadedPropertiesFromResources("customConfig");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStringProperty(String key, String defaultValue) {
        final DynamicStringProperty property = DynamicPropertyFactory.getInstance().getStringProperty(key,
            defaultValue);
        return property.get();
    }
}
