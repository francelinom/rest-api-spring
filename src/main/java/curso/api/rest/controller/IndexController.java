package curso.api.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/usuario")
public class IndexController {

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity init() {
        return new ResponseEntity("Olá REST Spring Boot: Usuario", HttpStatus.OK);
    }
}
