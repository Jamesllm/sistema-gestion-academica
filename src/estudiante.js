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

