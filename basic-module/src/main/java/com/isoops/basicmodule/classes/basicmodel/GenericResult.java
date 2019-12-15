package com.isoops.basicmodule.classes.basicmodel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;

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

    public static ResponseEntity export(File file,HttpHeaders httpHeaders,MediaType mediaType) {
        if (file == null){
            return fail(new Response<>(GenericEnum.SYSTEM_ERROR));
        }
        GenericResult.BodyBuilder bodyBuilder = GenericResult.ok()
                .headers(httpHeaders)
                .contentLength(file.length())
                .contentType(mediaType);
        return bodyBuilder.body(new FileSystemResource(file));
    }

    public static ResponseEntity export(byte[] bytes, HttpHeaders httpHeaders) {
        if (bytes == null){
            return fail(new Response<>(GenericEnum.SYSTEM_ERROR));
        }
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);

    }

}