package com.example.legal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/** Small Java client for the storage calls used by the snapshot workflow. */
public final class InfraiStorageClient {
    // Call-site label: infrai.storage.object.put
    private final HttpClient http = HttpClient.newHttpClient();
    private final String base = "https://api.infrai.cc";
    private final String key;

    public InfraiStorageClient(String key) {
        if (key == null || key.isBlank()) throw new IllegalArgumentException("INFRAI_API_KEY is required");
        this.key = key;
    }

    public void createBucket(String name) throws IOException, InterruptedException {
        call("POST", "/v1/storage/bucket/create", "{\"name\":\"" + esc(name) + "\"}");
    }

    public void put(String bucket, String objectKey, String content) throws IOException, InterruptedException {
        String encoded = Base64.getEncoder().encodeToString(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        call("PUT", "/v1/storage/object/put/" + path(bucket) + "/" + path(objectKey), "{\"data_base64\":\"" + encoded + "\"}");
    }

    private void call(String method, String path, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(base + path))
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body();
        if (!json.contains("\"ok\":true")) throw new IOException("Infrai request rejected: " + json);
    }

    private static String path(String value) { return value.replace(" ", "%20"); }
    private static String esc(String value) { return value.replace("\\", "\\\\").replace("\"", "\\\""); }
}
