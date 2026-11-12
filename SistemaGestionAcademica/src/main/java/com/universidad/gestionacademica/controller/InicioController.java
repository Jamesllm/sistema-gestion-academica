package com.universidad.gestionacademica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirige la raíz del sitio al listado de estudiantes.
 */
@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "redirect:/estudiantes";
    }
}
