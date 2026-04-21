package com.smartcampus.resources;

import com.smartcampus.smartcampusapi.DataStore;
import com.smartcampus.exceptions.SensorUnavailableException;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        if (!DataStore.sensors.containsKey(sensorId)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "404 Not Found");
            error.put("message", "Sensor not found: " + sensorId);
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
        List<SensorReading> readings = DataStore.sensorReadings.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "404 Not Found");
            error.put("message", "Sensor not found: " + sensorId);
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is in MAINTENANCE mode and cannot accept readings."
            );
        }
        SensorReading newReading = new SensorReading(reading.getValue());
        DataStore.sensorReadings
            .computeIfAbsent(sensorId, k -> Collections.synchronizedList(new ArrayList<>()))
            .add(newReading);
        sensor.setCurrentValue(newReading.getValue());
        return Response.status(Response.Status.CREATED).entity(newReading).build();
    }
}