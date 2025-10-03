package com.example.productsystem.backend.resource;

import com.example.productsystem.backend.entity.Product;
import com.example.productsystem.backend.mapper.ProductMapper;
import com.example.productsystem.backend.service.ProductService;
import com.example.productsystem.common.ProductDTO;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    private ProductService productService;

    private List<SseEventSink> clients = new CopyOnWriteArrayList<>();


    @POST
    @Transactional
    public Response create(ProductDTO dto, @Context UriInfo uriInfo) {
        try {
            Product p = ProductMapper.toEntity(dto);
            p = productService.create(p);
            ProductDTO out = ProductMapper.toDTO(p);
            UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(String.valueOf(p.getId()));
            return Response.created(ub.build()).entity(out).build();

        } catch (PersistenceException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse("Validation error", e.getMessage()))
                    .build();

        } catch (Exception e) {
            // Все остальные ошибки (серверные)
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
        List<ProductDTO> dtos = productService.list(page, size, sortField, asc)
                .stream().map(ProductMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        Product p = productService.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(ProductMapper.toDTO(p)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, ProductDTO dto) {
        Product existing = productService.find(id);
        if (existing == null) return Response.status(Response.Status.NOT_FOUND).build();
        Product updated = ProductMapper.toEntity(dto);
        updated.setId(id);
        productService.update(updated);
        return Response.ok(ProductMapper.toDTO(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        productService.delete(id);
        return Response.noContent().build();
    }

    // Фильтрация
    @GET
    @Path("/filter")
    public Response filter(@QueryParam("name") String name,
                           @QueryParam("priceMin") Float priceMin,
                           @QueryParam("priceMax") Float priceMax,
                           @QueryParam("page") @DefaultValue("0") int page,
                           @QueryParam("size") @DefaultValue("20") int size) {
        List<ProductDTO> dtos = productService.filter(name, priceMin, priceMax, page, size)
                .stream().map(ProductMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    // Сортировка (если нужна отдельная route)
    @GET
    @Path("/sort")
    public Response sort(@QueryParam("field") String field,
                         @QueryParam("order") @DefaultValue("asc") String order,
                         @QueryParam("page") @DefaultValue("0") int page,
                         @QueryParam("size") @DefaultValue("20") int size) {
        boolean asc = !"desc".equalsIgnoreCase(order);
        List<ProductDTO> dtos = productService.list(page, size, field, asc).stream().map(ProductMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    // delete by partNumber
    @DELETE
    @Path("/by-partnumber/{partNumber}")
    @Transactional
    public Response deleteByPartNumber(@PathParam("partNumber") String partNumber) {
        boolean removed = productService.deleteByPartNumber(partNumber);
        if (removed) return Response.noContent().build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/rating-greater-than/{rating}")
    public Response ratingGreaterThan(@PathParam("rating") int rating) {
        List<ProductDTO> dtos = productService.ratingGreaterThan(rating).stream().map(ProductMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/unique-partnumbers")
    public Response uniquePartNumbers() {
        return Response.ok(productService.uniquePartNumbers()).build();
    }

    @GET
    @Path("/price-range")
    public Response priceRange(@QueryParam("min") Float min, @QueryParam("max") Float max) {
        return Response.ok(productService.priceRange(min, max).stream().map(ProductMapper::toDTO).collect(Collectors.toList())).build();
    }

    @PUT
    @Path("/increase-price/{percent}")
    @Transactional
    public Response increasePrice(@PathParam("percent") int percent) {
        int updatedCount = productService.increasePricePercent(percent);
        return Response.ok(Collections.singletonMap("updated", updatedCount)).build();
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