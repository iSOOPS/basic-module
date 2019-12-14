package com.isoops.basicmodule.classes.basicmodel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Slf4j
public class GenericResult <T> extends ResponseEntity<Response<T>> {

    private GenericResult(Response<T> body, HttpStatus status) {
        super(body, status);
    }

    private static <T> GenericResult<T> success(Response<T> response) {
        return new GenericResult<>(response, HttpStatus.OK);
    }


    private static <T> GenericResult<T> fail(Response<T> response) {
        return new GenericResult<>(response, HttpStatus.BAD_REQUEST);
    }

    public static <T> GenericResult<T> recome(Response<T> response) {
        if (!response.getState()){
            return fail(response);
        }
        return success(response);
    }

    public static ResponseEntity export(File file) {
        if (file == null){
            return fail(new Response<>(GenericEnum.SYSTEM_ERROR));
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        GenericResult.BodyBuilder bodyBuilder = GenericResult.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"));
        return bodyBuilder.body(new FileSystemResource(file));
    }

    public static ResponseEntity export(byte[] bytes, String fileName) {
        if (bytes == null){
            return fail(new Response<>(GenericEnum.SYSTEM_ERROR));
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}