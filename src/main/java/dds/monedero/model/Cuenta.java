package dds.monedero.model;

import dds.monedero.exceptions.CuentaException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {

    verificarCuanto(cuanto);
    verificarLimiteDeposito();

    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

  public void sacar(double cuanto) {

    verificarCuanto(cuanto);
    verificarSaldoSuficiente(cuanto);
    verificarLimiteExtraccion(cuanto);

    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
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
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new CuentaException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  public void agregarMovimiento(Movimiento movimiento) {

    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
