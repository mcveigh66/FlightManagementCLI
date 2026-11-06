
package com.flightmanagement;

public class Main {

    public static void main(String[] args) {

        FlightApiClient apiClient = new FlightApiClient(
                "http://localhost:8080"
        );

        FlightMenu menu = new FlightMenu(apiClient);

        menu.start();
    }
}

