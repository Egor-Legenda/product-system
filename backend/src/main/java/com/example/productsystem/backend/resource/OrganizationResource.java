package com.example.productsystem.backend.resource;

import com.example.productsystem.backend.entity.Address;
import com.example.productsystem.backend.entity.Organization;
import com.example.productsystem.backend.mapper.AddressMapper;
import com.example.productsystem.backend.mapper.OrganizationMapper;
import com.example.productsystem.backend.service.OrganizationService;
import com.example.productsystem.common.AddressDTO;
import com.example.productsystem.common.OrganizationDTO;
import com.example.productsystem.common.OrganizationType;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * REST ресурс для управления организациями.
 * Предоставляет endpoints для поиска, фильтрации, сортировки и CRUD операций.
 */
@Path("/organizations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrganizationResource {

    @Inject
    private OrganizationService organizationService;

    /*
     * Поиск организаций по части имени (регистронезависимо).
     * Возвращает список DTO организаций, соответствующих критерию поиска.
     */
    @GET
    @Path("/search/by-name/{name}")
    public Response searchByName(@PathParam("name") String name) {
        try {
            List<OrganizationDTO> dtos = organizationService.findByName(name)
                    .stream()
                    .map(OrganizationMapper::toDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Search error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Фильтрация организаций по типу.
     * Возвращает список DTO организаций, соответствующих заданному типу.
     */
    @GET
    @Path("/filter/by-type/{type}")
    public Response filterByType(@PathParam("type") OrganizationType type) {
        try {
            List<OrganizationDTO> dtos = organizationService.findByType(type)
                    .stream()
                    .map(OrganizationMapper::toDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Filter error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Сортировка организаций по годовому обороту.
     * Поддерживает параметры пагинации и порядок сортировки (возрастание/убывание).
     */
    @GET
    @Path("/sort/by-turnover")
    public Response sortByTurnover(@QueryParam("page") @DefaultValue("0") int page,
                                   @QueryParam("size") @DefaultValue("20") int size,
                                   @QueryParam("order") @DefaultValue("asc") String order) {
        try {
            boolean ascending = !"desc".equalsIgnoreCase(order);
            List<OrganizationDTO> dtos = organizationService.sortByTurnover(page, size, ascending)
                    .stream()
                    .map(OrganizationMapper::toDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Sorting error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Получение официального адреса организации по её ID.
     * Возвращает DTO адреса или ошибку, если организация или адрес не найдены.
     */
    @GET
    @Path("/{id}/address")
    public Response getAddress(@PathParam("id") Integer id) {
        try {
            Address address = organizationService.findAddressByOrganizationId(id);
            if (address == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Not found", "Organization or address not found"))
                        .build();
            }
            return Response.ok(AddressMapper.toDTO(address)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving address", e.getMessage()))
                    .build();
        }
    }

    /*
     * Обновление официального адреса организации по её ID.
     * Принимает DTO адреса, возвращает статус операции.
     */
    @PUT
    @Path("/{id}/address")
    @Transactional
    public Response updateAddress(@PathParam("id") Integer id, AddressDTO addressDTO) {
        try {
            Address address = AddressMapper.toEntity(addressDTO);
            organizationService.updateAddress(id, address);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error updating address", e.getMessage()))
                    .build();
        }
    }


    /*
     * Создание новой организации.
     * Принимает DTO организации, возвращает созданный объект с URI.
     */
    @POST
    @Transactional
    public Response create(OrganizationDTO dto, @Context UriInfo uriInfo) {
        try {
            Organization org = OrganizationMapper.toEntity(dto);
            org = organizationService.create(org);
            OrganizationDTO out = OrganizationMapper.toDTO(org);
            UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(String.valueOf(org.getId()));
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

    /*
     * Получение организации по её ID.
     * Возвращает DTO организации или 404, если не найдена.
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Integer id) {
        Organization org = organizationService.find(id);
        if (org == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(OrganizationMapper.toDTO(org)).build();
    }

    /*
     * Обновление существующей организации по её ID.
     * Принимает DTO организации, возвращает обновлённый объект или 404, если не найдена.
     */
    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Integer id, OrganizationDTO dto) {
        Organization existing = organizationService.find(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Organization updated = OrganizationMapper.toEntity(dto);
        updated.setId(id);
        organizationService.update(updated);
        return Response.ok(OrganizationMapper.toDTO(updated)).build();
    }

    /*
     * Удаление организации по её ID.
     * Возвращает статус 204 No Content.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        organizationService.delete(id);
        return Response.noContent().build();
    }

    /*
     * Получение списка всех организаций с поддержкой пагинации и сортировки.
     * Параметры: page (номер страницы), size (размер страницы), field (поле сортировки), order (порядок).
     */
    @GET
    public Response list(@QueryParam("page") @DefaultValue("0") int page,
                         @QueryParam("size") @DefaultValue("20") int size,
                         @QueryParam("field") String sortField,
                         @QueryParam("order") @DefaultValue("asc") String order) {
        boolean asc = !"desc".equalsIgnoreCase(order);
        List<OrganizationDTO> dtos = organizationService.list(page, size, sortField, asc)
                .stream()
                .map(OrganizationMapper::toDTO)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    /*
     * Обработка HTTP OPTIONS запроса.
     * Возвращает поддерживаемые методы для ресурса.
     */
    @OPTIONS
    public Response options() {
        return Response.ok()
                .header("Allow", "GET, POST, PUT, DELETE, OPTIONS")
                .build();
    }

    /* Вспомогательный метод для создания стандартизированного ответа об ошибке */
    private Object createErrorResponse(String error, String message) {
        return Map.of(
                "error", error,
                "message", message,
                "timestamp", LocalDateTime.now()
        );
    }
}