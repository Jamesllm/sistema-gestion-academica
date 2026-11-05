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

