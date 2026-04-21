package com.smartcampus.smartcampusapi;  

import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    // ConcurrentHashMap is thread-safe (prevents race conditions the spec asks about)
    public static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    // Each sensor ID maps to a synchronized list of its readings
    public static final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();
}