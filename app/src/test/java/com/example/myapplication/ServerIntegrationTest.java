package com.example.myapplication;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;

public class ServerIntegrationTest {

    private MockWebServer mockWebServer;

    @Before
    public void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testServerIntegration() throws IOException {
        // Set up the MockWebServer to respond to requests
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"latitude\": 35.247526, \"longitude\": 33.024703}"));

        // Replace the base URL with the URL of your server endpoint
        String baseUrl = mockWebServer.url("/").toString();

        // Make a sample HTTP request to the server
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl + "gps")
                .build();

        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());

        String expectedResponseBody = "{\"latitude\": 35.247526, \"longitude\": 33.024703}";
        String responseBody = response.body().string();
        assertEquals(expectedResponseBody, responseBody);
    }
}
