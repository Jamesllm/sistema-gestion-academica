# Sistema de Información para la Gestión Académica

Proyecto desarrollado para la asignatura de **Herramientas de Desarrollo**. Implementa funcionalidades para el registro de estudiantes y cálculo de promedios académicos con un flujo completo de **Integración Continua (CI)** mediante **GitHub Actions** y pruebas automatizadas con **Jest**.

---

## 📁 Estructura del Proyecto

```text
sistema-gestion-academica/
├── .github/
│   └── workflows/
│       └── integracion-continua.yml   # Configuración de GitHub Actions (CI)
├── src/
│   ├── estudiante.js                  # Lógica de registro y validación de estudiantes
│   └── promedio.js                    # Lógica de cálculo y validación de notas
├── tests/
│   ├── estudiante.test.js             # Pruebas unitarias para estudiante
│   └── promedio.test.js               # Pruebas unitarias para cálculo de promedios
├── .gitignore                         # Exclusión de node_modules y temporales
├── package.json                       # Metadatos del proyecto y scripts
├── package-lock.json                  # Bloqueo de versiones de dependencias (para npm ci)
└── README.md                          # Documentación del proyecto
```

---

## 🚀 Requisitos Previos

- [Node.js](https://nodejs.org/) (versión 18, 20 o 22 recomendada)
- [Git](https://git-scm.com/)

---

## 🛠️ Instalación y Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd sistema-gestion-academica
   ```

2. **Instalar dependencias:**
   ```bash
   npm install
   # O en entornos de CI:
   npm ci
   ```

---

## 🧪 Ejecución de Pruebas

Para ejecutar la suite de pruebas unitarias con Jest:

```bash
npm test
```

Para ejecutar las pruebas en modo interactivo (observador continuo):

```bash
npm run test:watch
```

---

## 🔄 Flujo de Integración Continua (GitHub Actions)

El workflow se encuentra en `.github/workflows/integracion-continua.yml` y se activa automáticamente con:
- `push` a la rama `main` y a cualquier rama `feature/**`.
- `pull_request` dirigidas a la rama `main`.

### Pasos ejecutados por el flujo de CI:
1. **Checkout:** Descarga el código del repositorio (`actions/checkout@v4`).
2. **Setup Node.js:** Prepara Node.js v22 con caché de dependencias (`actions/setup-node@v4`).
3. **Instalación:** Ejecuta `npm ci` para garantizar una instalación rápida y reproducible usando `package-lock.json`.
4. **Ejecución de Pruebas:** Ejecuta `npm test` verificando que todas las suites pasen sin fallos.

---

## 👥 Trabajo Colaborativo con Git y Ramas

1. Crear una rama independiente por funcionalidad:
   ```bash
   git checkout -b feature/registro-estudiantes
   # o
   git checkout -b feature/calculo-promedios
   ```

2. Registrar y subir los cambios:
   ```bash
   git add .
   git commit -m "feat: implementar funcionalidad"
   git push -u origin feature/<nombre-rama>
   ```

3. Crear un **Pull Request** hacia `main` en GitHub y esperar a que las verificaciones automáticas de CI concluyan con éxito antes de integrar los cambios.
