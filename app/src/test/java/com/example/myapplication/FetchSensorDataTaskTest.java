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

import com.example.myapplication.FetchSensorDataTask;

public class FetchSensorDataTaskTest {

    @Mock
    HttpURLConnection mockConnection;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFetchSensorData_Success() throws Exception {
        // Mock the InputStream response
        String jsonResponse = "1";
        InputStream inputStream = new ByteArrayInputStream(jsonResponse.getBytes());

        // Mock HttpURLConnection response
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenReturn(inputStream);

        // Create FetchSensorDataTask instance
        FetchSensorDataTask fetchSensorDataTask = new FetchSensorDataTask("");
        // Set the mock connection
        fetchSensorDataTask.setUrlConnection(mockConnection);

        // Execute doInBackground directly
        String result = fetchSensorDataTask.doInBackground();

        // Verify that the result is correctly set
        assertEquals(jsonResponse, result);
    }
}
