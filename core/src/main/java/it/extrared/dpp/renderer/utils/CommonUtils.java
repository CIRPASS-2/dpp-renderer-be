/*
 * Copyright 2024-2027 CIRPASS-2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.extrared.dpp.renderer.utils;

import jakarta.ws.rs.core.MediaType;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import org.jboss.logging.Logger;

/** Utils class with common usefull methods. */
public class CommonUtils {

    public static final String JSON_LD_MIME = "application/ld+json";

    public static final String CONTENT_TYPE = "Content-Type";
    private static final Pattern JSON_LD_PATTERN = Pattern.compile("\"@context\"\\s*:");

    public static void debug(Supplier<String> message, Logger logger) {
        if (logger.isDebugEnabled()) logger.debug(message.get());
    }

    public static void error(Supplier<String> message, Throwable t, Logger logger) {
        logger.error(message.get(), t);
    }

    public static boolean isJsonContentType(String contentType) {
        return contentType != null
                && (contentType.contains(MediaType.APPLICATION_JSON)
                        || contentType.contains("text/json"));
    }

    public static boolean isJsonLd(String payload) {
        return JSON_LD_PATTERN.matcher(payload).find();
    }

    public static String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return "";
        }
        int semicolon = contentType.indexOf(';');
        if (semicolon > 0) {
            contentType = contentType.substring(0, semicolon);
        }

        return contentType.trim().toLowerCase();
    }

    public static String unwrapWhiteSpace(String value) {
        int length = value.length();
        if (!hasText(value)) return value;
        while (value.lastIndexOf(' ') == length - 1) {
            value = value.substring(0, length - 1);
            length = value.length();
        }
        while (value.indexOf(' ') == 0) {
            value = value.substring(1);
        }
        return value;
    }

    public static boolean hasText(String astring) {
        return astring != null && !astring.trim().isEmpty();
    }

    public static String sanitizeVarNameForSparQL(String name) {
        String sanitized = name.replaceAll("[^a-zA-Z0-9_]", "_");
        // SPARQL var non può iniziare con numero
        if (Character.isDigit(sanitized.charAt(0))) sanitized = "_" + sanitized;
        return sanitized;
    }
}
