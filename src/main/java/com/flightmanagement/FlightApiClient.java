package com.flightmanagement;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FlightApiClient {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FlightApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // Question 1: Get cities and their airports
    public JsonNode getCities() throws Exception {
        return sendGetRequest("/api/cities");
    }

    // Question 2: Get passengers and their aircraft
    public JsonNode getPassengers() throws Exception {
        return sendGetRequest("/api/passengers");
    }

    // Question 3: Get aircraft and their airports
    public JsonNode getAircraft() throws Exception {
        return sendGetRequest("/api/aircraft");
    }

    // Question 4: Get airports and their passengers
    public JsonNode getAirports() throws Exception {
        return sendGetRequest("/api/airports");
    }

    // Sends an HTTP GET request to the API
    private JsonNode sendGetRequest(String endpoint) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(baseUrl + endpoint))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Request failed with status code: "
                    + response.statusCode()
            );
        }

        return objectMapper.readTree(response.body());
    }
}
