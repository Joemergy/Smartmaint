# Smartmaint

**Sistema de gestión técnica para mantenimiento empresarial**  
_Evidencia GA7-220501096-AA4-EV01_

---

## Objetivo General

Desarrollar una solución web integral que permita gestionar, registrar y visualizar procesos de mantenimiento técnico en entornos empresariales, garantizando trazabilidad, eficiencia operativa y cumplimiento normativo.

---

## Estructura del Proyecto

Smartmaint/ 
├── Smartmaint-frontend/ # Interfaz de usuario en React JS ├
── BackendSmartmaint/ # Lógica de negocio y API REST en Spring Boot 
├── Smartmaint SQL/ # Scripts de base de datos en PostgreSQL 
 Evidencia.docx # Documento académico con capturas y justificación

---

## Tecnologías Utilizadas

| Módulo         | Tecnología principal       | Complementos                      |
|----------------|----------------------------|-----------------------------------|
| Frontend       | React JS                   | CSS, Bootstrap, Axios             |
| Backend        | Java + Spring Boot         | Spring Security, JPA, Hibernate   |
| Base de datos  | PostgreSQL                 | pgAdmin, SQL scripts              |
| Control de versiones | Git + GitHub         | Git Bash, GitHub Desktop          |

---

## Instrucciones de Ejecución

### 🔹 Frontend

```bash
cd Smartmaint-frontend
npm install
npm start

### Backend

cd BackendSmartmaint
mvn spring-boot:run

### Estructura Front-end

Smartmaint-frontend/
├── src/
│   ├── components/        # Componentes reutilizables
│   ├── pages/             # Vistas principales
│   ├── services/          # Conexión con el backend
│   └── App.js             # Componente raíz
├── public/
│   └── index.html         # Entrada principal
├── package.json
└── README.md

### Github repository

https://github.com/Joemergy/Smartmaint