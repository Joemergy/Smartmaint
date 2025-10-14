package com.smartmaint.service;

import com.smartmaint.model.Usuario;
import com.smartmaint.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Usuario validarCredenciales(String correo, String contrasena) {
        System.out.println("🔍 Validando credenciales para: " + correo);

        if (correo == null || contrasena == null || correo.isBlank() || contrasena.isBlank()) {
            System.out.println("⚠️ Datos incompletos");
            return null;
        }

        String correoNormalizado = correo.trim().toLowerCase();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correoNormalizado);

        if (usuarioOpt.isEmpty()) {
            System.out.println("❌ Usuario no encontrado");
            return null;
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
            System.out.println("⚠️ Usuario sin contraseña registrada");
            return null;
        }

        if (usuario.getActivo() != null && !usuario.getActivo()) {
            System.out.println("⛔ Usuario inactivo: " + usuario.getCorreo());
            return null;
        }

        boolean coincide = false;
        try {
            coincide = passwordEncoder.matches(contrasena, usuario.getContrasena());
        } catch (Exception e) {
            System.out.println("💥 Error al comparar contraseñas: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        if (coincide) {
            System.out.println("🟢 Credenciales válidas para: " + usuario.getCorreo());
            return usuario;
        } else {
            System.out.println("🔴 Contraseña incorrecta");
            return null;
        }
    }

    public Usuario registrarUsuario(Usuario usuario) {
        System.out.println("🆕 Registrando usuario: " + usuario.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }
}
