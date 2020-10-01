package com.hhjang.restapidemo.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

// serializer를 object mapper에 등록할 수 있는 annotation
@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

    @Override
    public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        errors.getFieldErrors().forEach(fieldError -> {
            try {
                // field errors
                gen.writeStartObject();
                gen.writeStringField("fieldName", fieldError.getField());
                gen.writeStringField("objectName", fieldError.getObjectName());
                gen.writeStringField("code", fieldError.getCode());
                gen.writeStringField("defaultMessage", fieldError.getDefaultMessage());
                Object rejectedValue = fieldError.getRejectedValue();
                if(rejectedValue != null) gen.writeStringField("rejectedValue", rejectedValue.toString());
                gen.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        errors.getGlobalErrors().forEach(objectError -> {
            try {
                // global errors
                gen.writeStartObject();
                gen.writeStringField("objectName", objectError.getObjectName());
                gen.writeStringField("code", objectError.getCode());
                gen.writeStringField("defaultMessage", objectError.getDefaultMessage());
                gen.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        gen.writeEndArray();
    }
}
