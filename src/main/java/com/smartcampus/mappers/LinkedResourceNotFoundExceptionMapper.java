package com.smartcampus.mappers;

import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "422 Unprocessable Entity");
        error.put("message", exception.getMessage());
        return Response.status(422)
                       .entity(error)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}