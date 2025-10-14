package com.smartmaint.controller;

import com.smartmaint.dto.EmpresaDTO;
import com.smartmaint.dto.UsuarioDTO;
import com.smartmaint.model.Empresa;
import com.smartmaint.model.Rol;
import com.smartmaint.model.Usuario;
import com.smartmaint.repository.EmpresaRepository;
import com.smartmaint.repository.RolRepository;
import com.smartmaint.repository.UsuarioRepository;
import com.smartmaint.service.AuthService;
import com.smartmaint.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private DataSource dataSource;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDTO loginRequest) {
        String correo = loginRequest.getCorreo().trim().toLowerCase();
        String contrasena = loginRequest.getContrasena();

        System.out.println("📥 Login recibido: " + correo);

        try {
            Usuario usuario = authService.validarCredenciales(correo, contrasena);

            if (usuario == null) {
                System.out.println("❌ Credenciales inválidas");
                return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
            }

            if (usuario.getActivo() != null && !usuario.getActivo()) {
                System.out.println("⛔ Usuario inactivo: " + usuario.getCorreo());
                return ResponseEntity.status(403).body(Map.of("error", "Usuario inactivo"));
            }

            Rol rol = usuario.getRol();
            Empresa empresa = usuario.getEmpresa();

            if (rol == null || rol.getNombre() == null) {
                System.out.println("⚠️ Usuario sin rol o rol sin nombre");
                return ResponseEntity.status(500).body(Map.of("error", "Usuario sin rol asignado"));
            }

            if (empresa == null || empresa.getNombre() == null) {
                System.out.println("⚠️ Usuario sin empresa o empresa sin nombre");
                return ResponseEntity.status(500).body(Map.of("error", "Usuario sin empresa asignada"));
            }

            String token = jwtUtil.generarToken(usuario.getCorreo());
            System.out.println("🎫 Token generado: " + token);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("token", token);
            respuesta.put("nombre", usuario.getNombre());
            respuesta.put("correo", usuario.getCorreo());
            respuesta.put("rol", rol.getNombre());
            respuesta.put("empresa", empresa.getNombre());
            respuesta.put("plan", empresa.getPlan());

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            System.out.println("💥 Error en login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error interno en login"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioDTO dto) {
        System.out.println("🚀 Entrando a register()");
        try {
            if (usuarioRepository.findByCorreo(dto.getCorreo().trim().toLowerCase()).isPresent()) {
                return ResponseEntity.status(400).body(Map.of("error", "El correo ya está registrado"));
            }

            Rol rol = rolRepository.findById(dto.getRol_id()).orElse(null);
            Empresa empresa = empresaRepository.findById(dto.getEmpresa_id()).orElse(null);
            if (rol == null || empresa == null) {
                return ResponseEntity.status(400).body(Map.of("error", "Rol o empresa inválida"));
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(dto.getNombre());
            nuevoUsuario.setCorreo(dto.getCorreo().trim().toLowerCase());
            nuevoUsuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
            nuevoUsuario.setRol(rol);
            nuevoUsuario.setEmpresa(empresa);

            usuarioRepository.saveAndFlush(nuevoUsuario);
            return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado exitosamente"));

        } catch (Exception e) {
            System.out.println("💥 Error en register(): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error interno al registrar usuario"));
        }
    }

    @PostMapping("/empresa")
    @Transactional
    public ResponseEntity<?> registrarEmpresa(@RequestBody EmpresaDTO dto) {
        System.out.println("🚀 Entrando a registrarEmpresa()");
        try {
            if (dto.getId_empresa() == null || dto.getCorreo_admin() == null || dto.getSector() == null) {
                return ResponseEntity.status(400).body(Map.of("error", "Todos los campos son obligatorios"));
            }

            String correoAdmin = dto.getCorreo_admin().trim().toLowerCase();

            if (empresaRepository.findByCorreo(correoAdmin).isPresent() ||
                usuarioRepository.findByCorreo(correoAdmin).isPresent()) {
                return ResponseEntity.status(400).body(Map.of("error", "Ya existe una empresa o usuario con ese correo"));
            }

            Empresa empresa = new Empresa();
            empresa.setNombre(dto.getId_empresa());
            empresa.setCorreo(correoAdmin);
            empresa.setSector(dto.getSector());
            empresa.setPlan(dto.getPlan());
            empresaRepository.saveAndFlush(empresa);

            Rol rolAdmin = rolRepository.findById(1).orElse(null);
            if (rolAdmin == null) {
                return ResponseEntity.status(500).body(Map.of("error", "Rol ADMIN no encontrado"));
            }

            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setCorreo(correoAdmin);
            admin.setContrasena(passwordEncoder.encode(dto.getContrasena()));
            admin.setEmpresa(empresa);
            admin.setRol(rolAdmin);
            usuarioRepository.saveAndFlush(admin);

            String token = jwtUtil.generarToken(admin.getCorreo());
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("token", token);
            respuesta.put("empresa", empresa.getNombre());
            respuesta.put("admin", admin.getCorreo());

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            System.out.println("💥 Error al registrar empresa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error interno al registrar empresa"));
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(Authentication authentication) {
        String correo = authentication.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/test-db")
    public ResponseEntity<?> testDbAccess() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM smartmaint.empresas LIMIT 1")) {

            while (rs.next()) {
                System.out.println("✅ Acceso confirmado: " + rs.getString("nombre"));
            }

            return ResponseEntity.ok(Map.of("mensaje", "Acceso a empresas confirmado"));

        } catch (Exception e) {
            System.out.println("💥 Error al acceder a empresas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error al acceder a empresas"));
        }
    }
}
