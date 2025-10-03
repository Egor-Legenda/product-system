package com.example.productsystem.backend.resource;


import com.example.productsystem.backend.entity.Coordinates;
import com.example.productsystem.backend.mapper.CoordinatesMapper;
import com.example.productsystem.backend.service.CoordinatesService;
import com.example.productsystem.common.CoordinatesDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/coordinates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class CoordinatesResource {

    @Inject
    private CoordinatesService coordinatesService;

    @POST
    @Transactional
    public Response create(CoordinatesDTO dto, @Context UriInfo uriInfo) {
        try {
            Coordinates coordinates = CoordinatesMapper.toEntity(dto);
            coordinates = coordinatesService.create(coordinates);
            CoordinatesDTO out = CoordinatesMapper.toDTO(coordinates);

            UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(String.valueOf(coordinates.getId()));
            return Response.created(ub.build()).entity(out).build();

        } catch (PersistenceException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse("Validation error", e.getMessage()))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Internal server error", e.getMessage()))
                    .build();
        }
    }

    @GET
    public Response list(@QueryParam("page") @DefaultValue("0") int page,
                         @QueryParam("size") @DefaultValue("20") int size,
                         @QueryParam("field") String sortField,
                         @QueryParam("order") @DefaultValue("asc") String order) {
        boolean asc = !"desc".equalsIgnoreCase(order);
        List<CoordinatesDTO> dtos = coordinatesService.list(page, size, sortField, asc)
                .stream().map(CoordinatesMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        Coordinates coordinates = coordinatesService.find(id);
        if (coordinates == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse("Not found", "Coordinates with id " + id + " not found"))
                    .build();
        }
        return Response.ok(CoordinatesMapper.toDTO(coordinates)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, CoordinatesDTO dto) {
        Coordinates existing = coordinatesService.find(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse("Not found", "Coordinates with id " + id + " not found"))
                    .build();
        }

        Coordinates updated = CoordinatesMapper.toEntity(dto);
        updated.setId(id);
        coordinatesService.update(updated);
        return Response.ok(CoordinatesMapper.toDTO(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Coordinates coordinates = coordinatesService.find(id);
        if (coordinates == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse("Not found", "Coordinates with id " + id + " not found"))
                    .build();
        }

        coordinatesService.delete(id);
        return Response.noContent().build();
    }

    // Фильтрация по диапазону значений X
    @GET
    @Path("/filter/by-x")
    public Response filterByX(@QueryParam("xMin") Integer xMin,
                              @QueryParam("xMax") Integer xMax,
                              @QueryParam("page") @DefaultValue("0") int page,
                              @QueryParam("size") @DefaultValue("20") int size) {
        List<CoordinatesDTO> dtos = coordinatesService.filterByX(xMin, xMax, page, size)
                .stream().map(CoordinatesMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    // Фильтрация по диапазону значений Y
    @GET
    @Path("/filter/by-y")
    public Response filterByY(@QueryParam("yMin") Double yMin,
                              @QueryParam("yMax") Double yMax,
                              @QueryParam("page") @DefaultValue("0") int page,
                              @QueryParam("size") @DefaultValue("20") int size) {
        List<CoordinatesDTO> dtos = coordinatesService.filterByY(yMin, yMax, page, size)
                .stream().map(CoordinatesMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    // Поиск координат с X больше указанного значения
    @GET
    @Path("/x-greater-than/{x}")
    public Response xGreaterThan(@PathParam("x") Integer x) {
        List<CoordinatesDTO> dtos = coordinatesService.findByXGreaterThan(x)
                .stream().map(CoordinatesMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    // Поиск координат с Y меньше указанного значения
    @GET
    @Path("/y-less-than/{y}")
    public Response yLessThan(@PathParam("y") Double y) {
        List<CoordinatesDTO> dtos = coordinatesService.findByYLessThan(y)
                .stream().map(CoordinatesMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    // Получение уникальных значений X
    @GET
    @Path("/unique-x")
    public Response uniqueXValues() {
        return Response.ok(coordinatesService.findUniqueXValues()).build();
    }

    // Получение статистики по координатам
    @GET
    @Path("/stats")
    public Response getStats() {
        return Response.ok(coordinatesService.getCoordinatesStats()).build();
    }

    @OPTIONS
    public Response options() {
        return Response.ok()
                .header("Allow", "GET, POST, PUT, DELETE, OPTIONS")
                .build();
    }

    private Object createErrorResponse(String error, String message) {
        return Map.of(
                "error", error,
                "message", message,
                "timestamp", LocalDateTime.now()
        );
    }
}