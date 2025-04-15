package med.voll.api.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hello")
@SecurityRequirement(name = "bearer-key")
public class HelloController {

    @GetMapping
    public Map<String, String> olaMundo() {
        Map<String, String> map = new HashMap<>();
        map.put("message", "sucess");

        return map;
    }
}
