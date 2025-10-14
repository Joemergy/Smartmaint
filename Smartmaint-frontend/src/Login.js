import React, { useState } from 'react';
import axios from 'axios';
import './Login.css';

const Login = ({ onLoginSuccess }) => {
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [error, setError] = useState('');
  const [mensaje, setMensaje] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setMensaje('');

    const payload = {
      correo: correo.trim().toLowerCase(),
      contrasena: contrasena
    };

    try {
      const response = await axios.post('http://localhost:8080/api/login', payload);
      const data = response.data;

      console.log('✅ Login exitoso:', data);

      localStorage.setItem('token', data.token);
      localStorage.setItem('usuario', data.usuario || data.nombre);
      localStorage.setItem('correo', data.correo);
      localStorage.setItem('rol', data.rol);
      localStorage.setItem('empresa', data.empresa);
      localStorage.setItem('plan', data.plan);

      setMensaje('✅ Inicio de sesión exitoso');

      if (onLoginSuccess) {
        onLoginSuccess(data);
      }

    } catch (err) {
      console.error('❌ Error en login:', err);
      if (err.response) {
        console.error('🔴 Código:', err.response.status);
        console.error('📩 Mensaje:', err.response.data);
        setError(err.response.data.error || `Error ${err.response.status}`);
      } else {
        setError('Error de conexión con el servidor');
      }
    }
  };

  return (
    <section className="login-section">
      <div className="login-container">
        <h2>Iniciar sesión</h2>
        <form className="login-form" onSubmit={handleLogin}>
          <label htmlFor="correo">Correo electrónico</label>
          <input
            type="email"
            id="correo"
            placeholder="Ej: admin@empresa.com"
            value={correo}
            onChange={(e) => setCorreo(e.target.value)}
            required
          />

          <label htmlFor="contrasena">Contraseña</label>
          <input
            type="password"
            id="contrasena"
            placeholder="Ingresa tu contraseña"
            value={contrasena}
            onChange={(e) => setContrasena(e.target.value)}
            required
          />

          <button type="submit">Ingresar</button>
        </form>

        {error && <p className="error-message">{error}</p>}
        {mensaje && <p className="success-message">{mensaje}</p>}
      </div>
    </section>
  );
};

export default Login;
