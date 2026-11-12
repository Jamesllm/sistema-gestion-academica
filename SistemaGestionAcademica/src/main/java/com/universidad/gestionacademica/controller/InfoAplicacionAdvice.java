package com.universidad.gestionacademica.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Agrega a todas las vistas la versión desplegada y el nombre del host.
 * Dentro de Kubernetes el host es el nombre del Pod, lo que permite
 * comprobar qué réplica atendió cada petición.
 */
@ControllerAdvice(assignableTypes = EstudianteController.class)
public class InfoAplicacionAdvice {

    private final String version;
    private final String host;

    public InfoAplicacionAdvice(@Value("${app.version:dev}") String version) {
        this.version = version;
        this.host = resolverHost();
    }

    @ModelAttribute("appVersion")
    public String appVersion() {
        return version;
    }

    @ModelAttribute("appHost")
    public String appHost() {
        return host;
    }

    private static String resolverHost() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname != null && !hostname.isBlank()) {
            return hostname;
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "desconocido";
        }
    }
}
