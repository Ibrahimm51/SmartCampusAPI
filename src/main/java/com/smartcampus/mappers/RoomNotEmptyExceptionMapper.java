package com.smartcampus.mappers;

import com.smartcampus.exceptions.RoomNotEmptyException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "409 Conflict");
        error.put("message", exception.getMessage());
        return Response.status(Response.Status.CONFLICT)
                       .entity(error)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}