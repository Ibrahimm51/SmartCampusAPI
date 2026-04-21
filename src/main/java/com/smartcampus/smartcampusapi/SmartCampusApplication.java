package com.smartcampus.smartcampusapi;  

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {
    public SmartCampusApplication() {
        // Tells Jersey to scan all these packages for resources, filters, mappers
        packages(
            "com.smartcampus.resources",
            "com.smartcampus.mappers",
            "com.smartcampus.filters"
        );
    }
}