package com.example.productsystem.backend.resource;


import com.example.productsystem.backend.service.HistoryService;
import com.example.productsystem.backend.service.ProductImportService;
import com.example.productsystem.backend.service.ProductService;
import jakarta.validation.ValidationException;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.example.productsystem.response.ErrorResponse.createErrorResponse;

@Path("/files")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class ProductImportResource {

    @Inject
    private ProductImportService productImportService;

    @Inject
    private HistoryService historyService;

    @POST
    @Path("/import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importProducts(MultipartFormDataInput input) {
        int importedCount = 0;
        String fileName = "";
        try {
            Map<String, List<InputPart>> formData = input.getFormDataMap();
            List<InputPart> fileParts = formData.get("file");

            if (fileParts == null || fileParts.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(createErrorResponse("Empty file", null)).build();
            }

            InputPart filePart = fileParts.get(0);
            InputStream fileInputStream = filePart.getBody(InputStream.class, null);

            fileName = getFileName(filePart);

            importedCount = productImportService.importProduct(fileInputStream, fileName);

            historyService.recordSuccess("user", fileName, importedCount);

            return  Response.ok().build();
        } catch (ValidationException c) {
            historyService.recordFailure("user", fileName != null ? fileName : "unknown", c.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(createErrorResponse("Validation error", c.getMessage())).build();
        } catch (Exception e) {
            historyService.recordFailure("user", fileName != null ? fileName : "unknown", "Internal server error");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getFileName(InputPart part) {
        String[] contentDisposition = part.getHeaders().getFirst("Content-Disposition").split(";");
        for (String cd : contentDisposition) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replaceAll("\"", "");
                return fileName;
            }
        }
        return "unknown";
    }
}
