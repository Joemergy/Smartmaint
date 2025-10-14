package com.smartmaint.controller;

import com.smartmaint.model.Equipo;
import com.smartmaint.model.Usuario;
import com.smartmaint.repository.EquipoRepository;
import com.smartmaint.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> crearEquipo(@RequestBody Equipo equipo, Authentication authentication) {
        String correo = authentication.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null || usuario.getEmpresa() == null) {
            return ResponseEntity.status(401).body("Usuario no autenticado o sin empresa asignada");
        }

        if (equipo.getNombre() == null || equipo.getNombre().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre del equipo es obligatorio");
        }

        try {
            equipo.setEmpresa(usuario.getEmpresa());
            Equipo guardado = equipoRepository.save(equipo);
            System.out.println("✅ Equipo registrado con ID: " + guardado.getId());
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            System.out.println("💥 Error al registrar equipo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno al registrar equipo");
        }
    }

    @GetMapping
    public ResponseEntity<?> listarEquipos(Authentication authentication) {
        String correo = authentication.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario == null || usuario.getEmpresa() == null) {
            return ResponseEntity.status(401).body("Usuario no autenticado o sin empresa asignada");
        }

        List<Equipo> equipos = equipoRepository.findByEmpresaId(usuario.getEmpresa().getId());
        System.out.println("📦 Listando " + equipos.size() + " equipos para empresa ID " + usuario.getEmpresa().getId());
        return ResponseEntity.ok(equipos);
    }
}
