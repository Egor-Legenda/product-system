package com.example.productsystem.backend.resource;

import com.example.productsystem.backend.entity.Location;
import com.example.productsystem.backend.entity.Person;
import com.example.productsystem.backend.mapper.LocationMapper;
import com.example.productsystem.backend.mapper.PersonMapper;
import com.example.productsystem.backend.service.PersonService;
import com.example.productsystem.common.Color;
import com.example.productsystem.common.Country;
import com.example.productsystem.common.LocationDTO;
import com.example.productsystem.common.PersonDTO;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * REST ресурс для управления сущностями Person.
 * Предоставляет CRUD операции и дополнительные endpoints для поиска и фильтрации.
 */
@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {

    @Inject
    private PersonService personService;

    /*
     * Получение списка всех Person с поддержкой пагинации и сортировки.
     * Параметры:
     * - page: номер страницы (по умолчанию 0)
     * - size: размер страницы (по умолчанию 20)
     * - field: поле для сортировки (необязательно)
     * - order: порядок сортировки (asc или desc, по умолчанию asc)
     */
    @GET
    public Response list(@QueryParam("page") @DefaultValue("0") int page,
                         @QueryParam("size") @DefaultValue("20") int size,
                         @QueryParam("field") String sortField,
                         @QueryParam("order") @DefaultValue("asc") String order) {
        try {
            boolean asc = !"desc".equalsIgnoreCase(order);
            List<PersonDTO> dtos = personService.list(page, size, sortField, asc)
                    .stream()
                    .map(PersonMapper::toDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("List error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Получение Person по ID.
     * Если Person с указанным ID не найден, возвращается 404.
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        try {
            Person person = personService.find(id);
            if (person == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(PersonMapper.toDTO(person)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Get error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Создание нового Person.
     * Принимает PersonDTO в теле запроса и возвращает созданный PersonDTO с 201 статусом.
     * В случае ошибок валидации возвращается 400, при других ошибках - 500.
     */
    @POST
    @Transactional
    public Response create(PersonDTO dto, @Context UriInfo uriInfo) {
        try {
            Person person = PersonMapper.toEntity(dto);
            person = personService.create(person);
            PersonDTO out = PersonMapper.toDTO(person);
            UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(String.valueOf(person.getId()));
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
     * Обновление существующего Person по ID.
     * Если Person с указанным ID не найден, возвращается 404.
     * В случае ошибок валидации возвращается 400, при других ошибках - 500.
     */
    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, PersonDTO dto) {
        try {
            Person existing = personService.find(id);
            if (existing == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Person updated = PersonMapper.toEntity(dto);
            updated.setId(id);
            personService.update(updated);
            return Response.ok(PersonMapper.toDTO(updated)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Update error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Удаление Person по ID.
     * Если Person с указанным ID не найден, возвращается 404.
     * В случае ошибок возвращается 500.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        try {
            personService.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Delete error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Поиск Person по passportID.
     * Если Person с указанным passportID не найден, возвращается 404.
     * В случае ошибок возвращается 500.
     */
    @GET
    @Path("/search/by-passport/{passportID}")
    public Response searchByPassport(@PathParam("passportID") String passportID) {
        try {
            Optional<Person> person = personService.findByPassportID(passportID);
            if (person.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(PersonMapper.toDTO(person.get())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Search error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Фильтрация Person по национальности.
     * Возвращает список PersonDTO, соответствующих заданной национальности.
     * В случае ошибок возвращается 500.
     */
    @GET
    @Path("/filter/by-nationality/{country}")
    public Response filterByNationality(@PathParam("country") Country country) {
        try {
            List<PersonDTO> dtos = personService.findByNationality(country)
                    .stream()
                    .map(PersonMapper::toDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Filter error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Фильтрация Person по цвету глаз.
     * Возвращает список PersonDTO, соответствующих заданному цвету глаз.
     * В случае ошибок возвращается 500.
     */
    @GET
    @Path("/filter/by-eye-color/{color}")
    public Response filterByEyeColor(@PathParam("color") Color eyeColor) {
        try {
            List<PersonDTO> dtos = personService.findByEyeColor(eyeColor)
                    .stream()
                    .map(PersonMapper::toDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Filter error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Фильтрация Person по цвету волос.
     * Возвращает список PersonDTO, соответствующих заданному цвету волос.
     * В случае ошибок возвращается 500.
     */
    @GET
    @Path("/filter/by-hair-color/{color}")
    public Response filterByHairColor(@PathParam("color") Color hairColor) {
        try {
            List<PersonDTO> dtos = personService.findByHairColor(hairColor)
                    .stream()
                    .map(PersonMapper::toDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Filter error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Поиск Person по имени.
     * Возвращает список PersonDTO, соответствующих заданному имени.
     * В случае ошибок возвращается 500.
     */
    @GET
    @Path("/search/by-name/{name}")
    public Response searchByName(@PathParam("name") String name) {
        try {
            List<PersonDTO> dtos = personService.findByName(name)
                    .stream()
                    .map(PersonMapper::toDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Search error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Получение Location, связанного с Person по ID Person.
     * Если Person или Location не найдены, возвращается 404.
     * В случае ошибок возвращается 500.
     */
    @GET
    @Path("/{id}/location")
    public Response getLocation(@PathParam("id") Long id) {
        try {
            Location location = personService.findLocationByPersonId(id);
            if (location == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(createErrorResponse("Not found", "Person or location not found"))
                        .build();
            }
            return Response.ok(LocationMapper.toDTO(location)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error retrieving location", e.getMessage()))
                    .build();
        }
    }

    /*
     * Обновление Location, связанного с Person по ID Person.
     * Принимает LocationDTO в теле запроса.
     * Если Person не найден, возвращается 404.
     * В случае ошибок возвращается 500.
     */
    @PUT
    @Path("/{id}/location")
    @Transactional
    public Response updateLocation(@PathParam("id") Long id, LocationDTO locationDTO) {
        try {
            Location location = LocationMapper.toEntity(locationDTO);
            personService.updateLocation(id, location);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Error updating location", e.getMessage()))
                    .build();
        }
    }


    /*
    * Фильтрация Person по нескольким критериям: цвет глаз, цвет волос, национальность.
    * Параметры запроса: eyeColor, hairColor
    * Возвращает список PersonDTO, соответствующих заданным критериям.
     */
    @GET
    @Path("/filter")
    public Response filterByMultiple(@QueryParam("eyeColor") Color eyeColor,
                                     @QueryParam("hairColor") Color hairColor,
                                     @QueryParam("nationality") Country nationality,
                                     @QueryParam("page") @DefaultValue("0") int page,
                                     @QueryParam("size") @DefaultValue("20") int size) {
        try {
            List<PersonDTO> dtos = personService.filterByMultipleCriteria(eyeColor, hairColor, nationality, page, size)
                    .stream()
                    .map(PersonMapper::toDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Filter error", e.getMessage()))
                    .build();
        }
    }



    /*
     * Удаление Person по passportID.
     * Если Person с указанным passportID не найден, возвращается 404.
     * В случае ошибок возвращается 500.
     */
    @DELETE
    @Path("/by-passport/{passportID}")
    @Transactional
    public Response deleteByPassport(@PathParam("passportID") String passportID) {
        try {
            boolean removed = personService.deleteByPassportID(passportID);
            if (removed) {
                return Response.noContent().build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Delete error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Обработка HTTP OPTIONS запроса.
     * Возвращает поддерживаемые методы для ресурса Person.
     */
    @OPTIONS
    public Response options() {
        return Response.ok()
                .header("Allow", "GET, POST, PUT, DELETE, OPTIONS")
                .build();
    }

    /*
     * Вспомогательный метод для создания стандартизированного объекта ошибки.
     */
    private Object createErrorResponse(String error, String message) {
        return Map.of(
                "error", error,
                "message", message,
                "timestamp", LocalDateTime.now()
        );
    }
}
