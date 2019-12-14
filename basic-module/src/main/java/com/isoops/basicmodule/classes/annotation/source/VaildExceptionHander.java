package com.isoops.basicmodule.classes.annotation.source;

import com.isoops.basicmodule.classes.basicmodel.Response;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
public class VaildExceptionHander {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public Response processValidationError(MethodArgumentNotValidException ex) {
        String msg = "validation error";
        BindingResult result = ex.getBindingResult();
        if (result.hasErrors()) {
            List list = result.getAllErrors();
            FieldError error = (FieldError) list.get(0);
            msg = "[" + error.getObjectName() + "." + error.getField() + "] " + error.getDefaultMessage();
        }
        Response model = new Response<>();
        model.setStateCode(HttpStatus.NOT_ACCEPTABLE.value());
        model.setState(false);
        model.setMsg(msg);
        return model;
    }



}
