package com.example.productsystem.backend.resource;

import com.example.productsystem.backend.entity.Address;
import com.example.productsystem.backend.mapper.AddressMapper;
import com.example.productsystem.backend.service.AddressService;
import com.example.productsystem.common.AddressDTO;
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

@Path("/addresses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AddressResource {

    @Inject
    private AddressService addressService;

    @POST
    @Transactional
    public Response create(AddressDTO dto, @Context UriInfo uriInfo) {
        try {
            Address address = AddressMapper.toEntity(dto);
            address = addressService.create(address);
            AddressDTO out = AddressMapper.toDTO(address);

            UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(String.valueOf(address.getId()));
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
        List<AddressDTO> dtos = addressService.list(page, size, sortField, asc)
                .stream().map(AddressMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        Address address = addressService.find(id);
        if (address == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse("Not found", "Address with id " + id + " not found"))
                    .build();
        }
        return Response.ok(AddressMapper.toDTO(address)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, AddressDTO dto) {
        Address existing = addressService.find(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse("Not found", "Address with id " + id + " not found"))
                    .build();
        }

        Address updated = AddressMapper.toEntity(dto);
        updated.setId(id);
        addressService.update(updated);
        return Response.ok(AddressMapper.toDTO(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Address address = addressService.find(id);
        if (address == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(createErrorResponse("Not found", "Address with id " + id + " not found"))
                    .build();
        }

        addressService.delete(id);
        return Response.noContent().build();
    }

    // Дополнительные endpoints для поиска и фильтрации

    @GET
    @Path("/search/by-zipcode/{zipCode}")
    public Response findByZipCode(@PathParam("zipCode") String zipCode) {
        List<AddressDTO> dtos = addressService.findByZipCode(zipCode)
                .stream().map(AddressMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/filter/by-town-name/{townName}")
    public Response findByTownName(@PathParam("townName") String townName,
                                   @QueryParam("page") @DefaultValue("0") int page,
                                   @QueryParam("size") @DefaultValue("20") int size) {
        List<AddressDTO> dtos = addressService.findByTownName(townName, page, size)
                .stream().map(AddressMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/unique-zipcodes")
    public Response getUniqueZipCodes() {
        return Response.ok(addressService.findUniqueZipCodes()).build();
    }

    @GET
    @Path("/count-by-town")
    public Response getCountByTown() {
        return Response.ok(addressService.getAddressCountByTown()).build();
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
