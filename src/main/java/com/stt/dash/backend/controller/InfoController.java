package com.stt.dash.backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/info")
public class InfoController {

    @GetMapping
    public ResponseEntity<Info> getInfo() {
        Info info = new Info();
        info.setVersion("2023.0.0");
        info.getDescription().add("Pantalla Agenda y Usuario con conteo correcto");
        info.getDescription().add("Agregar nuevo icono");
        return new ResponseEntity<>(info, org.springframework.http.HttpStatus.OK);
    }

    public class Info {
        private String version;
        private List<String> description = new ArrayList<>();

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<String> getDescription() {
            return description;
        }

        public void setDescription(List<String> description) {
            this.description = description;
        }
    }
}
