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

