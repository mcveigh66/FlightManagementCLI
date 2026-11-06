
package com.flightmanagement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class FlightMenuTest {

    @Mock
    private FlightApiClient apiClient;

    private final ObjectMapper mapper = new ObjectMapper();

    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    @AfterEach
    @SuppressWarnings("unused")
    void restoreSystem() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testAllFourMenuOptions() throws Exception {

        JsonNode cities = mapper.readTree(
            "[{\"id\":1,\"name\":\"St. John's\",\"airports\":[{" +
            "\"name\":\"St. John's International Airport\",\"code\":\"YYT\"}]}]"
        );

        JsonNode passengers = mapper.readTree(
            "[{\"firstName\":\"John\",\"lastName\":\"Smith\",\"aircrafts\":[{" +
            "\"type\":\"Boeing 737\",\"airlineName\":\"Air Canada\",\"airports\":[{" +
            "\"name\":\"St. John's International Airport\",\"code\":\"YYT\"}]}]}]"
        );

        JsonNode aircraft = mapper.readTree(
            "[{\"type\":\"Boeing 737\",\"airports\":[{" +
            "\"name\":\"St. John's International Airport\",\"code\":\"YYT\"}]}]"
        );

        when(apiClient.getCities()).thenReturn(cities);
        when(apiClient.getPassengers()).thenReturn(passengers);
        when(apiClient.getAircraft()).thenReturn(aircraft);

        System.setIn(new ByteArrayInputStream(
            "1\n2\n3\n4\n5\n".getBytes(StandardCharsets.UTF_8)
        ));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        FlightMenu menu = new FlightMenu(apiClient);
        menu.start();

        String result = output.toString();

        assertTrue(result.contains("St. John's International Airport"));
        assertTrue(result.contains("John Smith"));
        assertTrue(result.contains("Boeing 737"));
        assertTrue(result.contains("AIRPORTS USED BY PASSENGERS"));

        verify(apiClient, times(1)).getCities();
        verify(apiClient, times(2)).getPassengers();
        verify(apiClient, times(1)).getAircraft();

        verifyNoMoreInteractions(apiClient);
    }
}

