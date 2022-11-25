package sweng.penelope.xml;

import java.util.Objects;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.lang.Nullable;

/**
 * This is a mock spring Environment implementation.
 * The only functioning method is getProperty(String key) when key =
 * "storage.base-folder".
 * The function returns "./build/xml_tests" which is cleared once all the
 * tests are completed.
 */
public class TestEnvironment implements Environment {
    @Override
    public boolean containsProperty(String key) {
        return false;
    }

    @Override
    @Nullable
    public String getProperty(String key) {
        if (Objects.equals(key, "storage.base-folder"))
            return "./build/xml_tests";
        return null;
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return null;
    }

    @Override
    @Nullable
    public <T> T getProperty(String key, Class<T> targetType) {
        return null;
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return null;
    }

    @Override
    public String getRequiredProperty(String key) throws IllegalStateException {
        return null;
    }

    @Override
    public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        return null;
    }

    @Override
    public String resolvePlaceholders(String text) {
        return null;
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        return null;
    }

    @Override
    public String[] getActiveProfiles() {
        return null;
    }

    @Override
    public String[] getDefaultProfiles() {
        return null;
    }

    @Override
    public boolean acceptsProfiles(String... profiles) {
        return false;
    }

    @Override
    public boolean acceptsProfiles(Profiles profiles) {
        return false;
    }

}
