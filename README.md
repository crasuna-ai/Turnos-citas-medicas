import javax.swing.*;
import java.io.*;
import java.util.*;

public class TurnoManager {
    private Queue<Turno> colaTurnos;
    private List<Turno> turnosAtendidos;

    private final String ARCHIVO_COLA = "cola_turnos.dat";
    private final String ARCHIVO_ATENDIDOS = "turnos_atendidos.dat";

    public TurnoManager() {
        colaTurnos = cargarCola();
        turnosAtendidos = cargarLista();
    }

    public void iniciarSistema() {
        String[] opciones = {
            "Agregar turno",
            "Atender turno",
            "Mostrar turnos atendidos",
            "Mostrar turnos no atendidos",
            "Cancelar turno",
            "Salir"
        };

        int opcion;
        do {
            opcion = JOptionPane.showOptionDialog(
                null,
                "Seleccione una opción:",
                "Gestión de Turnos Médicos",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
            );

            switch (opcion) {
                case 0 -> agregarTurno();
                case 1 -> atenderTurno();
                case 2 -> mostrarTurnos(true);
                case 3 -> mostrarTurnos(false);
                case 4 -> cancelarTurno();
            }
        } while (opcion != 5);
    }

    public void agregarTurno() {
        String nombre = JOptionPane.showInputDialog("Ingrese el nombre del paciente:");
        String cedula = JOptionPane.showInputDialog("Ingrese la cédula del paciente:");

        if (nombre == null || cedula == null || nombre.trim().isEmpty() || cedula.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Datos inválidos. No se agregó el turno.");
            return;
        }

        Turno nuevoTurno = new Turno(nombre.trim(), cedula.trim());
        colaTurnos.offer(nuevoTurno);
        guardarCola();
        JOptionPane.showMessageDialog(null, "Turno agregado exitosamente.");
    }

    public void atenderTurno() {
        if (colaTurnos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay turnos para atender.");
            return;
        }

        Turno turnoAtendido = colaTurnos.poll();
        turnosAtendidos.add(turnoAtendido);
        guardarCola();
        guardarLista();
        JOptionPane.showMessageDialog(null, "Turno atendido:\n" + turnoAtendido);
    }

    public void mostrarTurnos(boolean atendidos) {
        StringBuilder mensaje = new StringBuilder();
        if (atendidos) {
            if (turnosAtendidos.isEmpty()) {
                mensaje.append("No hay turnos atendidos.");
            } else {
                for (Turno t : turnosAtendidos) {
                    mensaje.append(t).append("\n");
                }
            }
        } else {
            if (colaTurnos.isEmpty()) {
                mensaje.append("No hay turnos pendientes.");
            } else {
                for (Turno t : colaTurnos) {
                    mensaje.append(t).append("\n");
                }
            }
        }
        JOptionPane.showMessageDialog(null, mensaje.toString());
    }

    public void cancelarTurno() {
        if (colaTurnos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay turnos para cancelar.");
            return;
        }

        String cedula = JOptionPane.showInputDialog("Ingrese la cédula del paciente a cancelar:");
        if (cedula == null || cedula.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Cédula inválida.");
            return;
        }

        boolean encontrado = false;
        Queue<Turno> nuevaCola = new LinkedList<>();

        while (!colaTurnos.isEmpty()) {
            Turno t = colaTurnos.poll();
            if (!t.getCedula().equals(cedula.trim())) {
                nuevaCola.offer(t);
            } else {
                encontrado = true;
            }
        }

        colaTurnos = nuevaCola;
        guardarCola();

        if (encontrado) {
            JOptionPane.showMessageDialog(null, "Turno cancelado con éxito.");
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró un turno con esa cédula.");
        }
    }

    private void guardarCola() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_COLA))) {
            oos.writeObject(new LinkedList<>(colaTurnos));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error guardando la cola de turnos.");
        }
    }

    private void guardarLista() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_ATENDIDOS))) {
            oos.writeObject(turnosAtendidos);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error guardando turnos atendidos.");
        }
    }

    @SuppressWarnings("unchecked")
    private Queue<Turno> cargarCola() {
        File archivo = new File(ARCHIVO_COLA);
        if (!archivo.exists()) return new LinkedList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Queue<Turno>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new LinkedList<>();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Turno> cargarLista() {
        File archivo = new File(ARCHIVO_ATENDIDOS);
        if (!archivo.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Turno>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}
