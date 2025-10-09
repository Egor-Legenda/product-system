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

    /*
     * Создание новой сущности Coordinates.
     * Принимает CoordinatesDTO, валидирует и сохраняет в базе.
     * Возвращает созданный объект с HTTP статусом 201 или ошибку.
     */
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

    /*
     * Получение списка всех сущностей Coordinates с поддержкой пагинации и сортировки.
     * Параметры:
     * - page: номер страницы (по умолчанию 0)
     * - size: размер страницы (по умолчанию 20)
     * - field: поле для сортировки (необязательно)
     * - order: порядок сортировки (asc или desc, по умолчанию asc)
     * Возвращает список CoordinatesDTO.
     */
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

    /*
     * Поиск сущностей Coordinates по значениям X и Y.
     * Не работает!!!!
     */
    @GET
    @Path("/coordinates/search")
    public Response searchCoordinates(@QueryParam("x") Double x,
                                      @QueryParam("y") Double y) {
//        Coordinates coordinates = coordinatesService.find()
        return Response.ok().build();
    }

    /*
     * Получение сущности Coordinates по её ID.
     * Параметр:
     * - id: идентификатор Coordinates
     * Возвращает CoordinatesDTO или ошибку 404, если не найдено.
     */
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

    /*
     * Обновление существующей сущности Coordinates по её ID.
     * Параметры:
     * - id: идентификатор Coordinates
     * - dto: обновленные данные в виде CoordinatesDTO
     * Возвращает обновленный CoordinatesDTO или ошибку 404, если не найдено.
     */
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

    /*
     * Удаление сущности Coordinates по её ID.
     * Параметр:
     * - id: идентификатор Coordinates
     * Возвращает статус 204 при успешном удалении или ошибку 404, если не найдено.
     */
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


    /*     * Фильтрация сущностей Coordinates по диапазонам значений X и Y.
     * Параметры:
     * - xMin, xMax: диапазон для X (необязательно)
     * - yMin, yMax: диапазон для Y (необязательно)
     * - page: номер страницы (по умолчанию 0)
     * - size: размер страницы (по умолчанию 20)
     * Возвращает список отфильтрованных CoordinatesDTO.
     */
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

    /*
     * Фильтрация сущностей Coordinates по диапазонам значений Y.
     * Параметры:
     * - yMin, yMax: диапазон для Y (необязательно)
     * - page: номер страницы (по умолчанию 0)
     * - size: размер страницы (по умолчанию 20)
     * Возвращает список отфильтрованных CoordinatesDTO.
     */
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


    /*
    * Поиск координат с X больше указанного значения.
    * Параметр:
    * - x: значение для сравнения
    * Возвращает список CoordinatesDTO с X > заданного значения.
     */
    @GET
    @Path("/x-greater-than/{x}")
    public Response xGreaterThan(@PathParam("x") Integer x) {
        List<CoordinatesDTO> dtos = coordinatesService.findByXGreaterThan(x)
                .stream().map(CoordinatesMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    /*
     * Поиск координат с Y меньше указанного значения.
     * Параметр:
     * - y: значение для сравнения
     * Возвращает список CoordinatesDTO с Y < заданного значения.
     */
    @GET
    @Path("/y-less-than/{y}")
    public Response yLessThan(@PathParam("y") Double y) {
        List<CoordinatesDTO> dtos = coordinatesService.findByYLessThan(y)
                .stream().map(CoordinatesMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    /*
    * Получение уникальных значений X из всех сущностей Coordinates.
    * Возвращает список уникальных Integer значений X.
     */
    @GET
    @Path("/unique-x")
    public Response uniqueXValues() {
        return Response.ok(coordinatesService.findUniqueXValues()).build();
    }


    /*
     * Получение статистики по сущностям Coordinates.
     * Возвращает объект с общей статистикой (количество, среднее X и Y, мин/макс X и Y).
     */
    @GET
    @Path("/stats")
    public Response getStats() {
        return Response.ok(coordinatesService.getCoordinatesStats()).build();
    }

    /*
     * Обработка HTTP OPTIONS запроса для поддержки CORS и информирования о доступных методах.
     * Возвращает заголовок Allow с перечнем поддерживаемых методов.
     */
    @OPTIONS
    public Response options() {
        return Response.ok()
                .header("Allow", "GET, POST, PUT, DELETE, OPTIONS")
                .build();
    }

    /*
    Вспомогательный метод для создания стандартизированного объекта ошибки.
    */
    private Object createErrorResponse(String error, String message) {
        return Map.of(
                "error", error,
                "message", message,
                "timestamp", LocalDateTime.now()
        );
    }
}