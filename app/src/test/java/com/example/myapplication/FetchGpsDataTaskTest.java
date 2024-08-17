package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class FetchGpsDataTaskTest {

    @Mock
    HttpURLConnection mockConnection;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFetchGPSData_Success() throws Exception {
        // Mock the InputStream response
        String jsonResponse = "{\"latitude\":35.247526,\"longitude\":33.024703}";
        InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes());

        // Mock HttpURLConnection response
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenReturn(inputStream);

        // Create FetchGPSDataTask instance
        FetchGpsDataTask fetchGPSDataTask = new FetchGpsDataTask("");
        // Set the mock connection
        fetchGPSDataTask.setUrlConnection(mockConnection);

        // Execute doInBackground directly
        String result = fetchGPSDataTask.doInBackground();

        // Verify that the result is correctly set
        assertEquals(jsonResponse, result);
    }
}
