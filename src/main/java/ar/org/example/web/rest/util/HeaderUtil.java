package ar.org.example.web.rest.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private HeaderUtil() {
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-MSStorage-alert", message);
        headers.add("X-MSStorage-params", param);
        return headers;
    }

    public static HttpHeaders updateFileCreationAlert(String storageName, String param) {
        return createAlert("A new file was uploaded to the storage: " + storageName + " with name " + param, param);
    }

    public static HttpHeaders downloadFileCreationAlert(String storageName, String param) {
        return createAlert("A file was downloaded from the storage" + storageName + " with name " + param, param);
    }

    public static HttpHeaders removeFileCreationAlert(String storageName, String param) {
        return createAlert("A file was removed from the storage" + storageName + " with name " + param, param);
    }
}
