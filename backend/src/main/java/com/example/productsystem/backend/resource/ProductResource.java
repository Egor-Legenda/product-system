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
import jakarta.ws.rs.sse.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/*
 * REST ресурс для управления сущностями Product.
 * Предоставляет CRUD операции и дополнительные endpoints для поиска, фильтрации и сортировки.
 */
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    private ProductService productService;


    /*
     * Создание нового продукта.
     * Принимает ProductDTO в теле запроса и возвращает созданный ProductDTO с HTTP статусом 201.
     * Обрабатывает ошибки валидации и внутренние ошибки сервера.
     */
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createErrorResponse("Internal server error", e.getMessage()))
                    .build();
        }
    }

    /*
     * Получение списка продуктов с поддержкой пагинации и сортировки.
     * Параметры запроса:
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
        boolean asc = !"desc".equalsIgnoreCase(order);
        List<ProductDTO> dtos = productService.list(page, size, sortField, asc)
                .stream().map(ProductMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    /*
     * Получение продукта по его ID.
     * Если продукт с указанным ID не найден, возвращается HTTP статус 404.
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        Product p = productService.find(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(ProductMapper.toDTO(p)).build();
    }

    /*
     * Обновление существующего продукта по его ID.
     * Принимает ProductDTO в теле запроса и возвращает обновленный ProductDTO.
     * Если продукт с указанным ID не найден, возвращается HTTP статус 404.
     */
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

    /*
     * Удаление продукта по его ID.
     * Если продукт с указанным ID не найден, возвращается HTTP статус 404.
     * При успешном удалении возвращается HTTP статус 204 No Content.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        productService.delete(id);
        return Response.noContent().build();
    }




    /*
     * Фильтрация продуктов по заданному полю и значению с поддержкой пагинации и сортировки.
     * Параметры запроса:
     * - field: имя поля для фильтрации (обязательно)
     * - value: значение для фильтрации (обязательно)
     * - page: номер страницы (по умолчанию 0)
     * - size: размер страницы (по умолчанию 20)
     * - sortField: поле для сортировки (необязательно)
     * - order: порядок сортировки (asc или desc, по умолчанию asc)
     * Возвращает список ProductDTO, соответствующих критериям фильтрации.
     */
    @GET
    @Path("/filter")
    public Response filterProducts(
            @QueryParam("field") String field,
            @QueryParam("value") String value,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sortField") String sortField,
            @QueryParam("order") @DefaultValue("asc") String order) {

        boolean asc = !"desc".equalsIgnoreCase(order);
        List<ProductDTO> dtos = productService.filterByField(field, value, page, size, sortField, asc)
                .stream().map(ProductMapper::toDTO).collect(Collectors.toList());

        return Response.ok(dtos).build();
    }

    /*
     * Сортировка продуктов по заданному полю с поддержкой порядка сортировки и пагинации.
     * Параметры запроса:
     * - field: имя поля для сортировки (обязательно)
     * - order: порядок сортировки (asc или desc, по умолчанию asc)
     * - page: номер страницы (по умолчанию 0)
     * - size: размер страницы (по умолчанию 20)
     * Возвращает список ProductDTO, отсортированных по указанному полю и порядку.
     */
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

    /*
     * Удаление продукта по его уникальному partNumber.
     * Если продукт с указанным partNumber не найден, возвращается HTTP статус 404.
     * При успешном удалении возвращается HTTP статус 204 No Content.
     */
    @DELETE
    @Path("/by-partnumber/{partNumber}")
    @Transactional
    public Response deleteByPartNumber(@PathParam("partNumber") String partNumber) {
        boolean removed = productService.deleteByPartNumber(partNumber);
        if (removed) return Response.noContent().build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /*
     * Получение списка продуктов с рейтингом больше заданного значения.
     * Параметр пути:
     * - rating: минимальный рейтинг (целое число)
     * Возвращает список ProductDTO с рейтингом больше указанного значения.
     */
    @GET
    @Path("/rating-greater-than/{rating}")
    public Response ratingGreaterThan(@PathParam("rating") int rating) {
        List<ProductDTO> dtos = productService.ratingGreaterThan(rating).stream().map(ProductMapper::toDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    /*
     * Получение списка уникальных partNumber всех продуктов.
     * Возвращает список строк, каждая из которых является уникальным partNumber.
     */
    @GET
    @Path("/unique-partnumbers")
    public Response uniquePartNumbers() {
        return Response.ok(productService.uniquePartNumbers()).build();
    }

    /*
     * Получение списка продуктов, цена которых находится в заданном диапазоне.
     * Параметры запроса:
     * - min: минимальная цена (обязательно)
     * - max: максимальная цена (обязательно)
     * Возвращает список ProductDTO, цена которых находится между min и max.
     */
    @GET
    @Path("/price-range")
    public Response priceRange(@QueryParam("min") Float min, @QueryParam("max") Float max) {
        return Response.ok(productService.priceRange(min, max).stream().map(ProductMapper::toDTO).collect(Collectors.toList())).build();
    }

    /*
     * Увеличение цены всех продуктов на заданный процент.
     * Параметр пути:
     * - percent: процент увеличения цены (целое число)
     * Возвращает количество обновленных записей.
     */
    @PUT
    @Path("/increase-price/{percent}")
    @Transactional
    public Response increasePrice(@PathParam("percent") int percent) {
        int updatedCount = productService.increasePricePercent(percent);
        return Response.ok(Collections.singletonMap("updated", updatedCount)).build();
    }

    /*
    * Endpoint для обработки HTTP OPTIONS запросов.
     */
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