package com.smartcampus.smartcampusapi;  

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import java.io.IOException;
import java.net.URI;

public class SmartCampusApi {

    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static HttpServer startServer() {
        final SmartCampusApplication app = new SmartCampusApplication();
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), app);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println("==============================================");
        System.out.println("Smart Campus API is running!");
        System.out.println("Access it at: " + BASE_URI);
        System.out.println("Press ENTER to stop the server.");
        System.out.println("==============================================");
        System.in.read();
        server.stop();
        System.out.println("Server stopped.");
    }
}