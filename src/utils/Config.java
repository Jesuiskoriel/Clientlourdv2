package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lit la configuration depuis les variables d'environnement
 * avec repli sur un fichier .env à la racine du projet.
 */
public final class Config {

    private static final Map<String, String> FILE_ENV = loadDotEnv();

    private Config() {
    }

    public static String get(String key, String fallback) {
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        String fileValue = FILE_ENV.get(key);
        if (fileValue != null && !fileValue.isBlank()) {
            return fileValue;
        }
        return fallback;
    }

    private static Map<String, String> loadDotEnv() {
        Map<String, String> values = new HashMap<>();
        Path path = Path.of(".env");
        if (!Files.isRegularFile(path)) {
            return values;
        }
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int sep = line.indexOf('=');
                if (sep <= 0) {
                    continue;
                }
                String key = line.substring(0, sep).trim();
                String value = stripQuotes(line.substring(sep + 1).trim());
                values.put(key, value);
            }
        } catch (IOException ignored) {
            // En cas d'échec de lecture, on garde uniquement l'environnement système.
        }
        return values;
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
