package com.universidad.gestionacademica.controller;

import com.universidad.gestionacademica.exception.DatoDuplicadoException;
import com.universidad.gestionacademica.exception.EstudianteNoEncontradoException;
import com.universidad.gestionacademica.model.Estudiante;
import com.universidad.gestionacademica.service.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador web (vistas Thymeleaf) para la gestión de estudiantes.
 */
@Controller
@RequestMapping("/estudiantes")
public class EstudianteController {

    static final List<String> CARRERAS = List.of(
            "Ingeniería de Sistemas",
            "Ingeniería de Software",
            "Ingeniería Industrial",
            "Ingeniería Civil",
            "Administración",
            "Contabilidad",
            "Derecho",
            "Psicología");

    private final EstudianteService servicio;

    public EstudianteController(EstudianteService servicio) {
        this.servicio = servicio;
    }

    @ModelAttribute("carreras")
    public List<String> carreras() {
        return CARRERAS;
    }

    /** Listar y buscar estudiantes. */
    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String q, Model model) {
        List<Estudiante> estudiantes = servicio.buscar(q);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("q", q);
        return "estudiantes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        model.addAttribute("modoEdicion", false);
        return "estudiantes/formulario";
    }

    /** Registrar estudiante. */
    @PostMapping
    public String registrar(@Valid @ModelAttribute("estudiante") Estudiante estudiante,
                            BindingResult resultado, Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            model.addAttribute("modoEdicion", false);
            model.addAttribute("error", "Revise los datos ingresados.");
            return "estudiantes/formulario";
        }
        try {
            Estudiante guardado = servicio.registrar(estudiante);
            redirect.addFlashAttribute("exito",
                    "Estudiante " + guardado.getNombreCompleto() + " registrado correctamente.");
            return "redirect:/estudiantes";
        } catch (DatoDuplicadoException ex) {
            resultado.rejectValue(ex.getCampo(), "duplicado", ex.getMessage());
            model.addAttribute("modoEdicion", false);
            model.addAttribute("error", ex.getMessage());
            return "estudiantes/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("estudiante", servicio.obtenerPorId(id));
            model.addAttribute("modoEdicion", true);
            return "estudiantes/formulario";
        } catch (EstudianteNoEncontradoException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/estudiantes";
        }
    }

    /** Actualizar estudiante. */
    @PostMapping("/{id}")
    public String actualizar(@PathVariable Integer id,
                             @Valid @ModelAttribute("estudiante") Estudiante estudiante,
                             BindingResult resultado, Model model, RedirectAttributes redirect) {
        estudiante.setId(id);
        if (resultado.hasErrors()) {
            model.addAttribute("modoEdicion", true);
            model.addAttribute("error", "Revise los datos ingresados.");
            return "estudiantes/formulario";
        }
        try {
            Estudiante actualizado = servicio.actualizar(id, estudiante);
            redirect.addFlashAttribute("exito",
                    "Estudiante " + actualizado.getNombreCompleto() + " actualizado correctamente.");
            return "redirect:/estudiantes";
        } catch (DatoDuplicadoException ex) {
            resultado.rejectValue(ex.getCampo(), "duplicado", ex.getMessage());
            model.addAttribute("modoEdicion", true);
            model.addAttribute("error", ex.getMessage());
            return "estudiantes/formulario";
        } catch (EstudianteNoEncontradoException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/estudiantes";
        }
    }

    /** Eliminar estudiante. */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            servicio.eliminar(id);
            redirect.addFlashAttribute("exito", "Estudiante eliminado correctamente.");
        } catch (EstudianteNoEncontradoException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/estudiantes";
    }
}
