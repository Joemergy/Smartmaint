package com.smartmaint.controller;

import com.smartmaint.model.Empresa;
import com.smartmaint.model.Usuario;
import com.smartmaint.model.Rol;
import com.smartmaint.repository.EmpresaRepository;
import com.smartmaint.repository.UsuarioRepository;
import com.smartmaint.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> registrarEmpresa(@RequestBody Map<String, String> datos) {
        try {
            System.out.println("📥 Datos recibidos desde frontend: " + datos);

            String nombre = datos.get("id_empresa");
            String correo = datos.get("correo_admin");
            String contrasena = datos.get("contrasena");
            String sector = datos.get("sector");
            String plan = datos.get("plan");

            if (nombre == null || correo == null || contrasena == null || sector == null || plan == null) {
                System.out.println("⚠️ Faltan campos obligatorios");
                return ResponseEntity.badRequest().body("❌ Faltan campos obligatorios");
            }

            if (empresaRepository.existsByNombre(nombre)) {
                System.out.println("⚠️ ID de empresa ya en uso: " + nombre);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("❌ El ID de empresa ya está en uso");
            }

            if (usuarioRepository.findByCorreo(correo).isPresent()) {
                System.out.println("⚠️ Correo ya registrado: " + correo);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("❌ El correo ya está registrado");
            }

            Empresa empresa = new Empresa();
            empresa.setNombre(nombre);
            empresa.setCorreo(correo);
            empresa.setSector(sector);
            empresa.setPlan(plan);
            empresaRepository.save(empresa);

            Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            Usuario usuario = new Usuario();
            usuario.setNombre("Administrador " + nombre);
            usuario.setCorreo(correo);
            usuario.setContrasena(passwordEncoder.encode(contrasena));
            usuario.setEmpresa(empresa);
            usuario.setRol(rolAdmin);
            usuario.setActivo(true);

            usuarioRepository.save(usuario);

            System.out.println("✅ Empresa y usuario administrador registrados");
            return ResponseEntity.ok("✅ Empresa y administrador registrados con éxito");

        } catch (Exception e) {
            System.out.println("❌ Error interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error interno en el servidor");
        }
    }

    @GetMapping("/validar-id/{id_empresa}")
    public ResponseEntity<Boolean> validarIdEmpresa(@PathVariable String id_empresa) {
        boolean disponible = !empresaRepository.existsByNombre(id_empresa);
        System.out.println("🔍 Validación de ID: " + id_empresa + " → disponible: " + disponible);
        return ResponseEntity.ok(disponible);
    }
}
