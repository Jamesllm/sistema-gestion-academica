# INFORME TÉCNICO: HERRAMIENTAS DE INTEGRACIÓN CONTINUA (CI)

**Curso:** Herramientas de Desarrollo  
**Proyecto:** Sistema de Información para la Gestión Académica  
**Entregable:** Tarea Práctica Calificada  
**Calificación máxima:** 20 puntos  

---

## 1. Informe de Análisis del Problema y Objetivos

### 1.1. Situación Problemática Inicial (Desarrollo Tradicional)
En el contexto universitario planteado, el equipo de desarrollo trabaja de manera simultánea en diferentes funcionalidades (estudiantes, notas, cursos). Sin embargo, el esquema tradicional presentaba graves limitaciones operativas:
- **Ausencia de validación preventiva:** Los cambios no siempre se probaban antes de integrarse a la rama principal.
- **Conflictos y regresiones tardías:** Los errores surgían al mezclar código de diferentes programadores, descubriéndose cuando el sistema ya estaba en etapas avanzadas o en producción.
- **Alto costo temporal:** La ejecución manual de casos de prueba demandaba horas repetitivas y era propensa a omisiones humanas.
- **Falta de trazabilidad y visibilidad:** No existían notificaciones centralizadas ni registro automático del commit exacto que introdujo una falla.

### 1.2. Objetivos del Proyecto
1. **Implementar Integración Continua (CI):** Automatizar la construcción, descarga de dependencias y ejecución de pruebas en cada `push` y `pull_request`.
2. **Garantizar Calidad Temprana (Shift-Left Testing):** Detectar y aislar fallos en segundos mediante pruebas unitarias con Jest.
3. **Optimizar el Flujo de Trabajo Colaborativo:** Proteger la rama principal (`main`) obligando a que cualquier cambio pase por una verificación automatizada previa a su fusión.
4. **Notificación y Control:** Proveer retroalimentación inmediata sobre el estado (éxito/fracaso) del código integrado.

---

## 2. Repositorio y Arquitectura del Sistema

### 2.1. Estructura de Directorios
```text
sistema-gestion-academica/
├── .github/
│   └── workflows/
│       └── integracion-continua.yml   # Pipeline de CI (GitHub Actions)
├── src/
│   ├── estudiante.js                  # F01 y F02: Registro y validación de estudiantes
│   └── promedio.js                    # F03 y F04: Validación y cálculo de promedio
├── tests/
│   ├── estudiante.test.js             # Pruebas unitarias de registro
│   └── promedio.test.js               # Pruebas unitarias de cálculo de notas
├── .gitignore                         # Exclusión de artefactos y dependencias locales
├── package.json                       # Configuración y scripts npm
├── package-lock.json                  # Fijación exacta de dependencias (npm ci)
├── README.md                          # Guía de despliegue y uso
└── INFORME_PRACTICA_CALIFICADA.md     # Documento de entrega académica
```

### 2.2. Tecnologías Empleadas
- **Entorno de Ejecución:** Node.js (v22 en CI / v24 local)
- **Framework de Pruebas:** Jest (^30.0.0)
- **Control de Versiones:** Git & GitHub
- **Motor de CI:** GitHub Actions (Runner `ubuntu-latest`)

---

## 3. Código Fuente de las Funcionalidades

### 3.1. Módulo de Registro de Estudiantes (`src/estudiante.js`)
Implementa las funcionalidades **F01 (Registrar estudiantes)** y **F02 (Validar los datos del estudiante)**:

```javascript
function registrarEstudiante(nombre, codigo, correo) {
  if (!nombre || !codigo || !correo) {
    throw new Error("Todos los campos son obligatorios");
  }
  if (!correo.includes("@")) {
    throw new Error("Correo electrónico inválido");
  }
  return {
    nombre,
    codigo,
    correo,
    estado: "Registrado"
  };
}

module.exports = { registrarEstudiante };
```

### 3.2. Módulo de Cálculo de Promedio (`src/promedio.js`)
Implementa las funcionalidades **F03 (Registrar calificaciones)** y **F04 (Calcular el promedio académico)**:

```javascript
function calcularPromedio(nota1, nota2, nota3) {
  const notas = [nota1, nota2, nota3];
  if (
    !notas.every(
      (nota) =>
        typeof nota === "number" &&
        Number.isFinite(nota) &&
        nota >= 0 &&
        nota <= 20
    )
  ) {
    throw new Error("Las notas deben estar entre 0 y 20");
  }
  return (nota1 + nota2 + nota3) / 3;
}

module.exports = { calcularPromedio };
```

---

## 4. Archivos de Pruebas Automatizadas (Jest)

### 4.1. Pruebas de Estudiantes (`tests/estudiante.test.js`)
Cubre casos de éxito, validación de datos incompletos y formato de correo:

```javascript
const { registrarEstudiante } = require("../src/estudiante");

describe("Pruebas del registro de estudiantes", () => {
  test("Debe registrar un estudiante correctamente", () => {
    const estudiante = registrarEstudiante(
      "Juan Perez",
      "2026001",
      "juan@gmail.com"
    );
    expect(estudiante.nombre).toBe("Juan Perez");
    expect(estudiante.codigo).toBe("2026001");
    expect(estudiante.estado).toBe("Registrado");
  });

  test("Debe rechazar campos vacíos", () => {
    expect(() => {
      registrarEstudiante("", "2026001", "juan@gmail.com");
    }).toThrow("Todos los campos son obligatorios");
  });

  test("Debe rechazar un correo inválido", () => {
    expect(() => {
      registrarEstudiante(
        "Juan Perez",
        "2026001",
        "juangmail.com"
      );
    }).toThrow("Correo electrónico inválido");
  });
});
```

### 4.2. Pruebas de Promedio (`tests/promedio.test.js`)
Cubre cálculo correcto, rechazo de notas > 20 y notas negativas:

```javascript
const { calcularPromedio } = require("../src/promedio");

describe("Pruebas del cálculo de promedio", () => {
  test("Debe calcular correctamente el promedio", () => {
    const resultado = calcularPromedio(14, 16, 18);
    expect(resultado).toBe(16);
  });

  test("Debe rechazar notas superiores a 20", () => {
    expect(() => {
      calcularPromedio(15, 21, 18);
    }).toThrow();
  });

  test("Debe rechazar notas negativas", () => {
    expect(() => {
      calcularPromedio(15, -2, 18);
    }).toThrow();
  });
});
```

---

## 5. Configuración de GitHub Actions (`.github/workflows/integracion-continua.yml`)

El pipeline automatizado define triggers claros y utiliza `npm ci` para garantizar builds reproducibles a partir de `package-lock.json`:

```yaml
name: Integracion Continua

on:
  push:
    branches:
      - main
      - 'feature/**'
  pull_request:
    branches:
      - main

jobs:
  pruebas:
    name: Ejecutar pruebas automatizadas
    runs-on: ubuntu-latest

    steps:
      - name: Descargar codigo fuente
        uses: actions/checkout@v4

      - name: Configurar Node.js
        uses: actions/setup-node@v4
        with:
          node-version: 22
          cache: npm

      - name: Instalar dependencias
        run: npm ci

      - name: Ejecutar pruebas
        run: npm test
```

---

## 6. Evidencias de Ejecución de Pruebas

### 6.1. Ejecución Local Satisfactoria (Paso 6)
```text
> sistema-gestion-academica@1.0.0 test
> jest --runInBand

PASS tests/estudiante.test.js
PASS tests/promedio.test.js

Test Suites: 2 passed, 2 total
Tests:       6 passed, 6 total
Snapshots:   0 total
Time:        2.189 s
Ran all test suites.
```

### 6.2. Escenario A: Ejecución Exitosa en GitHub Actions (SUCCESS)
- **Workflow:** `Integracion Continua`
- **Runner:** `ubuntu-latest`
- **Pasos:**
  - `actions/checkout@v4`: SUCCESS
  - `actions/setup-node@v4`: SUCCESS
  - `npm ci`: SUCCESS
  - `npm test`: SUCCESS (2 Suites pasadas, 6 tests aprobados)
- **Estado final:** ✅ Check en verde.

### 6.3. Escenario B: Simulación de Error Intencional (FAILURE)
- **Cambio introducido en `src/promedio.js`:**
  ```javascript
  return (nota1 + nota2 + nota3) / 2; // Error deliberado: división entre 2 en vez de 3
  ```
- **Resultado detectado por el runner:**
  ```text
  FAIL tests/promedio.test.js
  ● Pruebas del cálculo de promedio › Debe calcular correctamente el promedio
    Expected: 16
    Received: 24

  Test Suites: 1 failed, 1 passed, 2 total
  Tests:       1 failed, 5 passed, 6 total
  Process completed with exit code 1.
  ```
- **Resultado del Job:** ❌ FAILURE. Se bloquea la opción de merge en el Pull Request.

### 6.4. Escenario C: Corrección y Nueva Verificación Exitosa
- **Restauración:** Se retorna la fórmula `(nota1 + nota2 + nota3) / 3` (o versión con `.toFixed(2)`).
- **Nuevo commit y push:**
  ```bash
  git commit -m "fix: corregir formula de calculo de promedio"
  git push
  ```
- **Resultado:** ✅ GitHub Actions vuelve a pasar todas las pruebas al 100%.

---

## 7. Configuración de Notificaciones y Pull Requests

### 7.1. Notificaciones
1. **GitHub Notifications:** Configurado en `Settings` -> `Notifications` para recibir alertas por correo electrónico ante fallos en GitHub Actions del repositorio.
2. **Pull Request Checks:** Cada Pull Request muestra en tiempo real el widget de GitHub Actions ("All checks have passed" o "Checks failed"), previniendo la integración de código defectuoso.

### 7.2. Reglas de Protección de Rama (`Branch Protection Rules`)
En GitHub:
1. `Settings` -> `Branches` -> `Add branch protection rule`.
2. Branch name pattern: `main`.
3. Activar:
   - *Require a pull request before merging*.
   - *Require status checks to pass before merging*.
   - Seleccionar el check: `Ejecutar pruebas automatizadas`.

---

## 8. Análisis de Resultados e Indicadores (Métricas de CI)

Con base en una batería de 5 ejecuciones simuladas durante el ciclo de vida:

| Métrica | Fórmula | Cálculo de Muestra | Valor Obtenido |
| :--- | :--- | :--- | :--- |
| **Porcentaje de pruebas aprobadas** | `(Pruebas aprobadas / Total pruebas) × 100` | `(6 / 6) × 100` | **100%** (en commit estable) |
| **Tasa de ejecuciones exitosas** | `(Ejecuciones exitosas / Total ejecuciones) × 100` | `(4 / 5) × 100` | **80%** |
| **Tasa de fallos** | `(Ejecuciones fallidas / Total ejecuciones) × 100` | `(1 / 5) × 100` | **20%** |
| **Tiempo promedio de ejecución** | `Tiempo total de ejecuciones / N.º ejecuciones` | `115s / 5` | **23 segundos** |

---

## 9. Cuadro Comparativo: Desarrollo Tradicional vs. Integración Continua

| Criterio | Desarrollo Tradicional | Desarrollo con Integración Continua |
| :--- | :--- | :--- |
| **Pruebas** | Manuales, esporádicas y dependientes del desarrollador. | Automatizadas, estandarizadas y ejecutadas en cada commit. |
| **Detección de errores** | Tardía (en fases de integración o producción). | Inmediata (a los pocos segundos o minutos de subir el código). |
| **Proceso de integración** | Manual y riesgoso ("integration hell"). | Automatizado y continuo mediante ramas y Pull Requests. |
| **Comunicación** | Informal y verbal. | Notificaciones automáticas por correo, chat o interfaz de GitHub. |
| **Trazabilidad** | Difícil saber qué cambio rompió el sistema. | Historial exacto por commit, autor y log de ejecución del runner. |
| **Riesgo en rama principal** | Alto riesgo de código roto o inestable. | Blindada mediante status checks obligatorios. |

---

## 10. Respuestas al Cuestionario de Evaluación Individual

#### 1. ¿Qué es la integración continua (CI) y cuál es su importancia en el desarrollo de software?
La Integración Continua (CI) es una práctica de desarrollo de software donde los desarrolladores integran su trabajo de manera frecuente (generalmente varias veces al día) en un repositorio compartido. Cada integración es verificada automáticamente por una compilación y conjunto de pruebas automatizadas. Su importancia radica en reducir drásticamente el tiempo de detección de bugs, evitar el "infierno de integración" y asegurar que el software siempre se mantenga en un estado funcional y confiable.

#### 2. ¿Qué diferencia existe entre integración continua (CI) y entrega continua (CD)?
- **Integración Continua (CI):** Se enfoca en la fase de desarrollo e integración de código; comprende la compilación automática y ejecución de pruebas unitarias/integración en cada commit o PR.
- **Entrega Continua (CD - Continuous Delivery):** Es la extensión de CI donde, una vez superadas las pruebas, el software se empaqueta y prepara de forma automatizada para que pueda ser desplegado en entornos de staging o producción en cualquier momento con un solo clic (o Continuous Deployment si el pase a producción es 100% sin intervención humana).

#### 3. ¿Qué función cumple GitHub Actions dentro de un proyecto colaborativo?
Actúa como la plataforma de automatización y orquestación de CI/CD nativa de GitHub. Proporciona máquinas virtuales (runners) que escuchan eventos del repositorio (como `push` o `pull_request`) y ejecutan flujos de trabajo (*workflows*) para compilar dependencias, correr linters, ejecutar pruebas automatizadas y generar reportes de calidad sin requerir servidores externos dedicados.

#### 4. ¿Qué sucede cuando un desarrollador realiza un `git push`?
El comando `git push` transfiere los commits locales a la rama correspondiente en el repositorio remoto (GitHub). Si el repositorio tiene configurado un archivo de workflow en `.github/workflows/` cuyos eventos (`on: push`) coinciden con la rama afectada, GitHub Actions captura el evento, aprovisiona un entorno virtual, clona el repositorio en ese estado exacto y ejecuta la secuencia de pasos definida (instalación de librerías, pruebas, etc.).

#### 5. ¿Cuál es la importancia de utilizar pruebas automatizadas?
Permiten verificar el comportamiento esperado del sistema de forma repetible, rápida y determinista. Eliminan el error humano propio de las pruebas manuales, garantizan que las nuevas funcionalidades no introduzcan regresiones en código existente y brindan la confianza necesaria para refactorizar y desplegar continuamente.

#### 6. ¿Qué diferencia existe entre una prueba unitaria y una prueba de integración?
- **Prueba unitaria:** Evalúa de manera aislada la unidad más pequeña de código testeable (como una función individual o método, e.g., `calcularPromedio`), simulando o prescindiendo de dependencias externas.
- **Prueba de integración:** Verifica cómo interactúan y se comunican entre sí dos o más módulos, subsistemas o dependencias reales (por ejemplo, interacción entre un controlador, un servicio y la base de datos).

#### 7. ¿Cómo identifica GitHub Actions que una prueba ha fallado?
GitHub Actions monitorea el código de salida (*exit code*) del proceso ejecutado en cada paso. Por convención POSIX, un comando que termina con éxito devuelve `0`, mientras que un fallo devuelve un código distinto de cero (e.g., `exit code 1`). Frameworks como Jest retornan un código de salida `1` cuando falla al menos una aserción, lo cual hace que el runner marque inmediatamente el paso como `FAILURE` e interrumpa el job.

#### 8. ¿Qué ventajas ofrecen las notificaciones automáticas?
Permiten un bucle de retroalimentación inmediato (*fast feedback loop*). El desarrollador responsable se entera al instante si su cambio introdujo un problema sin tener que consultar periódicamente la plataforma, evitando que otros miembros del equipo descarguen o basen su trabajo en código roto.

#### 9. ¿Por qué es recomendable ejecutar pruebas antes de aceptar una Pull Request?
Porque actúa como una aduana de calidad automatizada (puerta de calidad o *quality gate*). Asegura que el código propuesto cumpla con todos los estándares y pruebas vigentes antes de ser incorporado a la rama principal (`main`), protegiendo la estabilidad del producto y ahorrando tiempo a los revisores humanos, quienes solo revisan código que ya ha demostrado funcionar.

#### 10. ¿Qué consecuencias puede ocasionar integrar código que no ha superado las pruebas?
- Ruptura del entorno de desarrollo de todos los miembros del equipo.
- Bloqueo de lanzamientos a producción.
- Incremento exponencial del tiempo y costo de depuración (es mucho más difícil encontrar el origen del bug cuando se acumulan múltiples cambios).
- Pérdida de confianza en la suite del proyecto e introducción de fallos críticos para los usuarios finales.

---

## 11. Conclusiones y Recomendaciones

### Conclusiones
1. La integración de GitHub Actions junto con Jest permitió transformar un proceso manual y propenso a errores en un flujo de trabajo confiable, estandarizado y desatendido.
2. El uso de `npm ci` garantiza que todos los entornos (local y en los runners de GitHub) compartan exactamente las mismas versiones de paquetes definidas en `package-lock.json`, eliminando inconsistencias por dependencias flotantes.
3. El esquema de Pull Requests con verificación obligatoria reduce a cero la posibilidad de introducir errores evidentes en la rama de producción `main`.

### Recomendaciones
1. **Configurar métricas de cobertura de código:** Añadir `--coverage` al script de Jest para establecer un umbral mínimo de cobertura (e.g., 85% o 90%).
2. **Incorporar Linters (ESLint / Prettier):** Añadir una fase previa de análisis estático de código en el workflow de CI para asegurar coherencia en estilos y buenas prácticas.
3. **Fijar reglas de protección de rama:** Obligar a que ningún desarrollador pueda hacer push directo a `main` sin pasar por un Pull Request aprobado y con las pruebas en verde.

