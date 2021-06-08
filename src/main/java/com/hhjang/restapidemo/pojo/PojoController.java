package com.hhjang.restapidemo.pojo;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/temp/pojo", produces = MediaTypes.HAL_JSON_VALUE)
public class PojoController {

    @PostMapping
    public ResponseEntity<Pojo> create(@RequestBody Pojo pojo) {
        return new ResponseEntity<>(pojo, HttpStatus.CREATED);
    }
}
