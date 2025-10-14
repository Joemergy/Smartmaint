import React, { useState } from 'react';
import Login from './Login';
import RegistroEmpresa from './RegistroEmpresa';
import './App.css';

function App() {
  const [vista, setVista] = useState('login');

  return (
    <main className="app-container">
      <header className="topbar">
        <div className="logo">
          <img src="Smartmaintlogo.png" alt="Smartmaint Logo" />
        </div>
        <nav>
          <ul>
            {vista !== 'login' && (
              <li><button onClick={() => setVista('login')}>Iniciar sesión</button></li>
            )}
            {vista !== 'registro' && (
              <li><button onClick={() => setVista('registro')}>Registrar Empresa</button></li>
            )}
            <li><a href="equipos.html">Equipos</a></li>
            <li><button className="nav-button">Reportes</button></li>
            <li><button className="nav-button">Cerrar sesión</button></li>
          </ul>
        </nav>
      </header>

      <div
        className="contenido-fondo"
        style={{
          backgroundImage: "url('Smartmaintwallpaper.png')",
          backgroundSize: "cover",
          backgroundRepeat: "no-repeat",
          backgroundPosition: "center",
          minHeight: "100vh",
          padding: "60px 10%",
        }}
      >
        <section className="vista-wrapper">
          {vista === 'login' && <Login />}
          {vista === 'registro' && <RegistroEmpresa />}
        </section>
      </div>
    </main>
  );
}

export default App;
