import java.io.*;
    public class Turno implements Serializable {
        private String nombrepaciente;
        private String cedula;
        public Turno(String nombrepaciente, String cedula) {
            this.nombrepaciente = nombrepaciente;
            this.cedula = cedula;
        }
        public String getNombrepaciente() {
            return nombrepaciente;
        }
        
        public String getCedula() {
            return cedula;
        }         
        
    }

