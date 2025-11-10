package highwaystar.utils;

import java.io.*;
import java.util.Properties;

public class Config {
    private static Config instance;
    private Properties properties;
    private static final String CONFIG_FILE = "app.properties";

    private Config() {
        properties = new Properties();
        loadDefaults();
        loadFromFile();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private void loadDefaults() {
        properties.setProperty("firebase.database.url", "https://highway-star-a0d94.firebaseio.com");
        properties.setProperty("firebase.credentials.path", "/serviceAccountKey.json");
        properties.setProperty("app.name", "Highway Star");
        properties.setProperty("app.version", "2.0.0");
        properties.setProperty("default.latitude", "1.3521");
        properties.setProperty("default.longitude", "103.8198");
        properties.setProperty("default.zoom", "12");
        properties.setProperty("default.step.goal", "10000");
        properties.setProperty("achievement.first.activity", "FIRST_ACTIVITY");
        properties.setProperty("achievement.ten.k.steps", "TEN_K_STEPS");
        properties.setProperty("achievement.hundred.k.steps", "HUNDRED_K_STEPS");
        properties.setProperty("achievement.seven.day.streak", "SEVEN_DAY_STREAK");
        properties.setProperty("achievement.thirty.day.streak", "THIRTY_DAY_STREAK");
        properties.setProperty("achievement.marathon.distance", "MARATHON_DISTANCE");
    }

    private void loadFromFile() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Could not load config file, using defaults: " + e.getMessage());
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public void save() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Highway Star Configuration");
        } catch (IOException e) {
            System.err.println("Could not save config file: " + e.getMessage());
        }
    }
}
