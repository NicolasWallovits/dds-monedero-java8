package dds.monedero.model;

import dds.monedero.exceptions.CuentaException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos;

  public Cuenta() {
    saldo = 0;
    movimientos = new ArrayList<>();
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
    movimientos = new ArrayList<>();
  }

  public void poner(double cuanto) {
    verificarCuanto(cuanto);
    verificarLimiteDeposito();

    agregarMovimiento(new Deposito(LocalDate.now(), cuanto));
  }

  public void sacar(double cuanto) {
    verificarCuanto(cuanto);
    verificarSaldoSuficiente(cuanto);
    verificarLimiteExtraccion(cuanto);

    agregarMovimiento(new Extraccion(LocalDate.now(), cuanto));
  }

  private void verificarCuanto(double cuanto) {
    if (cuanto <= 0) {
      throw new CuentaException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  private void verificarSaldoSuficiente(double cuanto) {
    if (getSaldo() - cuanto < 0) {
      throw new CuentaException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  private void verificarLimiteExtraccion(double cuanto) {
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new CuentaException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }

  private void verificarLimiteDeposito() {
    if (getMovimientos().stream().filter(movimiento -> movimiento.getMonto() > 0).count() >= 3) {
      throw new CuentaException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  private void agregarMovimiento(Movimiento movimiento) {
    saldo += movimiento.getMonto();
    movimientos.add(movimiento);
  }

  private double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.getMonto() < 0 && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }
}
