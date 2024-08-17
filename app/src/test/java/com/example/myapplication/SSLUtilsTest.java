package com.example.myapplication;

import static org.junit.Assert.*;
import org.junit.Test;

import javax.net.ssl.SSLContext;

public class SSLUtilsTest {

    @Test
    public void testGetUnsafeSSLContext() {
        // Call the method
        SSLContext sslContext = SSLUtils.getUnsafeSSLContext();
        assertNotNull(sslContext);
    }
}
