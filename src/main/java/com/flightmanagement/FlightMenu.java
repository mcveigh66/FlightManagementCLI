
package com.flightmanagement;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

public class FlightMenu {

    private final FlightApiClient apiClient;
    private final Scanner scanner;

    public FlightMenu(FlightApiClient apiClient) {
        this.apiClient = apiClient;
        this.scanner = new Scanner(System.in);
    }

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println("\n================================");
            System.out.println("       FLIGHT MANAGEMENT");
            System.out.println("================================");
            System.out.println("1. Airports in each city");
            System.out.println("2. Aircraft flown by each passenger");
            System.out.println("3. Airports used by each aircraft");
            System.out.println("4. Airports used by passengers");
            System.out.println("5. Exit");
            System.out.print("\nChoose an option: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {

                    case "1" -> displayCityAirports(apiClient.getCities());

                    case "2" -> displayPassengerAircraft(apiClient.getPassengers());

                    case "3" -> displayAircraftAirports(apiClient.getAircraft());

                    case "4" -> displayPassengerAirports(apiClient.getPassengers());

                    case "5" -> {
                        running = false;
                        System.out.println("Goodbye!");
                    }

                    default -> System.out.println("Invalid choice. Try again.");
                }

            } catch (Exception e) {
                System.out.println("Error connecting to API: " + e.getMessage());
            }
        }
    }

    // Handles both regular JSON arrays and paginated API responses.
    private JsonNode getRecords(JsonNode response) {

        if (response.has("content")) {
            return response.get("content");
        }

        return response;
    }

    // Question 1: What airports are in each city?
    private void displayCityAirports(JsonNode response) {

        JsonNode cities = getRecords(response);

        System.out.println("\nAIRPORTS IN EACH CITY");

        if (!cities.elements().hasNext()) {
            System.out.println("No cities found.");
            return;
        }

        for (JsonNode city : cities) {

            System.out.println("\nCity: " + city.path("name").asText());

            JsonNode airports = city.path("airports");

            if (!airports.elements().hasNext()) {
                System.out.println("  No airports assigned.");
            }

            for (JsonNode airport : airports) {
                System.out.println("  - " + airport.path("name").asText()
                        + " (" + airport.path("code").asText() + ")");
            }
        }
    }

    // Question 2: What aircraft has each passenger flown on?
    private void displayPassengerAircraft(JsonNode response) {

        JsonNode passengers = getRecords(response);

        System.out.println("\nAIRCRAFT FLOWN BY EACH PASSENGER");

        if (!passengers.elements().hasNext()) {
            System.out.println("No passengers found.");
            return;
        }

        for (JsonNode passenger : passengers) {

            String name = passenger.path("firstName").asText()
                    + " " + passenger.path("lastName").asText();

            System.out.println("\nPassenger: " + name);

            JsonNode aircrafts = passenger.path("aircrafts");

            if (!aircrafts.elements().hasNext()) {
                System.out.println("  No aircraft records found.");
            }

            for (JsonNode aircraft : aircrafts) {
                System.out.println("  - " + aircraft.path("type").asText()
                        + " (" + aircraft.path("airlineName").asText() + ")");
            }
        }
    }

    // Question 3: What airports does each aircraft use?
    private void displayAircraftAirports(JsonNode response) {

        JsonNode aircrafts = getRecords(response);

        System.out.println("\nAIRPORTS USED BY EACH AIRCRAFT");

        if (!aircrafts.elements().hasNext()) {
            System.out.println("No aircraft found.");
            return;
        }

        for (JsonNode aircraft : aircrafts) {

            System.out.println("\nAircraft: " + aircraft.path("type").asText());

            JsonNode airports = aircraft.path("airports");

            if (!airports.elements().hasNext()) {
                System.out.println("  No airports assigned.");
            }

            for (JsonNode airport : airports) {
                System.out.println("  - " + airport.path("name").asText()
                        + " (" + airport.path("code").asText() + ")");
            }
        }
    }

    // Question 4: What airports have passengers used?
    private void displayPassengerAirports(JsonNode response) {

        JsonNode passengers = getRecords(response);

        Map<String, Set<String>> airportPassengers = new LinkedHashMap<>();

        for (JsonNode passenger : passengers) {

            String passengerName = passenger.path("firstName").asText()
                    + " " + passenger.path("lastName").asText();

            JsonNode aircrafts = passenger.path("aircrafts");

            for (JsonNode aircraft : aircrafts) {

                JsonNode airports = aircraft.path("airports");

                for (JsonNode airport : airports) {

                    String airportName = airport.path("name").asText()
                            + " (" + airport.path("code").asText() + ")";

                    airportPassengers
                            .computeIfAbsent(airportName, key -> new LinkedHashSet<>())
                            .add(passengerName);
                }
            }
        }

        System.out.println("\nAIRPORTS USED BY PASSENGERS");

        if (airportPassengers.isEmpty()) {
            System.out.println("No passenger airport records found.");
            return;
        }

        for (Map.Entry<String, Set<String>> entry : airportPassengers.entrySet()) {

            System.out.println("\nAirport: " + entry.getKey());

            for (String passenger : entry.getValue()) {
                System.out.println("  - " + passenger);
            }
        }
    }
}
