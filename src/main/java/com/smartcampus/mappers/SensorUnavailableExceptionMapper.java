package com.smartcampus.mappers;

import com.smartcampus.exceptions.SensorUnavailableException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "403 Forbidden");
        error.put("message", exception.getMessage());
        return Response.status(Response.Status.FORBIDDEN)
                       .entity(error)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}