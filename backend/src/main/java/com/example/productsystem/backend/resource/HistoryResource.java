package com.example.productsystem.backend.resource;

import com.example.productsystem.backend.entity.History;
import com.example.productsystem.backend.repository.HistoryRepository;
import com.example.productsystem.backend.service.HistoryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/history")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HistoryResource {

    @Inject
    private HistoryService historyService;


    @GET
    public Response list() {
        List<History> historyList = historyService.findAll();
        return Response.ok(historyList).build();
    }

}
