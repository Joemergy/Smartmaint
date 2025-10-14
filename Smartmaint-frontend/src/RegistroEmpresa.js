import React, { useState } from 'react';
import './RegistroEmpresa.css';

const RegistroEmpresa = () => {
  const [id_empresa, setIdEmpresa] = useState('');
  const [idDisponible, setIdDisponible] = useState(null);
  const [sector, setSector] = useState('');
  const [correo_admin, setCorreoAdmin] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [plan, setPlan] = useState('');

  const validarIdEmpresa = async (id) => {
    setIdEmpresa(id);
    if (!id) {
      setIdDisponible(null);
      return;
    }

    try {
      const res = await fetch(`http://localhost:8080/api/empresa/validar-id/${id}`);
      const disponible = await res.json();
      setIdDisponible(disponible);
    } catch (err) {
      console.error("Error al validar ID:", err);
      setIdDisponible(null);
    }
  };

  const handleRegistro = async (e) => {
    e.preventDefault();

    if (!id_empresa || idDisponible === false || !sector || !correo_admin || !contrasena || !plan) {
      alert("⚠️ Todos los campos son obligatorios y el ID debe estar disponible");
      return;
    }

    try {
      const res = await fetch("http://localhost:8080/api/empresa", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ id_empresa, sector, correo_admin, contrasena, plan })
      });

      const text = await res.text();
      if (res.ok) {
        alert("✅ Empresa registrada con éxito");
        setIdEmpresa('');
        setIdDisponible(null);
        setSector('');
        setCorreoAdmin('');
        setContrasena('');
        setPlan('');
      } else {
        alert("⚠️ " + text);
      }
    } catch (err) {
      alert("❌ Error de conexión con el servidor");
      console.error(err);
    }
  };

  return (
    <section className="register-section" id="registrar-empresa">
      <div className="register-container" data-aos="fade-up">
        <h2>Registrar Empresa</h2>
        <form className="register-form" onSubmit={handleRegistro}>
          <label htmlFor="id_empresa">ID de Empresa</label>
          <input
            type="text"
            id="id_empresa"
            name="id_empresa"
            placeholder="Ej: EMP001"
            value={id_empresa}
            onChange={(e) => validarIdEmpresa(e.target.value)}
            required
          />
          {idDisponible === false && (
            <p className="error-text">⚠️ Este ID ya está en uso. Elige otro.</p>
          )}

          <label htmlFor="sector">Sector</label>
          <input
            type="text"
            id="sector"
            name="sector"
            placeholder="Ej: Manufactura, Salud, Logística"
            value={sector}
            onChange={(e) => setSector(e.target.value)}
            required
          />

          <label htmlFor="correo_admin">Correo del Administrador</label>
          <input
            type="email"
            id="correo_admin"
            name="correo_admin"
            placeholder="admin@empresa.com"
            value={correo_admin}
            onChange={(e) => setCorreoAdmin(e.target.value)}
            required
          />

          <label htmlFor="contrasena">Contraseña</label>
          <input
            type="password"
            id="contrasena"
            name="contrasena"
            placeholder="Crea una contraseña segura"
            value={contrasena}
            onChange={(e) => setContrasena(e.target.value)}
            required
          />

          <label htmlFor="plan">Plan</label>
          <input
            type="text"
            id="plan"
            name="plan"
            placeholder="Ej: Básico, Premium, Corporativo"
            value={plan}
            onChange={(e) => setPlan(e.target.value)}
            required
          />

          <button type="submit" disabled={idDisponible === false}>
            Registrar Empresa
          </button>

          <p className="helper-text">
            ¿Ya tienes empresa registrada? <a href="registro.html">Registrar empleados</a>
          </p>
        </form>
      </div>
    </section>
  );
};

export default RegistroEmpresa;
