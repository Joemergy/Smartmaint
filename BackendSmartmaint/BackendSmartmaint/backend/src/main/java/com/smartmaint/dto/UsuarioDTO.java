package com.smartmaint.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private String nombre;
    private String correo;
    private String contrasena;
    private int rol_id;
    private int empresa_id;
}
