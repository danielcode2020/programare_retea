package com.example.programareretea.rest;

import com.example.programareretea.service.Lab1Service;
import com.example.programareretea.service.dto.PortHostDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/lab1")
public class Lab1Resource {
    private final Logger log = LoggerFactory.getLogger(Lab1Resource.class);
    private final Lab1Service lab1Service;

    public Lab1Resource(Lab1Service lab1Service) {
        this.lab1Service = lab1Service;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(@RequestParam String host, @RequestParam Integer port) throws IOException,
            InterruptedException {

        PortHostDto dto = new PortHostDto(host,port);
        lab1Service.request(dto);
        log.info("successfully executed !");
        return ResponseEntity.ok().body("Check images folder");
    }
}

