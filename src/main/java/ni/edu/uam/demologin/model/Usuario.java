package ni.edu.uam.demologin.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.UUID;

public class Usuario extends BaseEntity {

    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty apellido = new SimpleStringProperty();
    private final StringProperty correo = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty usuario = new SimpleStringProperty();
    private final StringProperty rutafoto = new SimpleStringProperty();

    public Usuario(String nombre, String apellido
            , String email, String usuario
            , String password, String rutafoto) {
        super();
        setNombre(nombre);
        setApellido(apellido);
        setCorreo(email);
        setUsuario(usuario);
        setPassword(password);
        setRutafoto(rutafoto);
    }

    public Usuario() {
        super();
    }

    public String getUsuario() {
        return usuario.get();
    }

    public void setUsuario(String usuario) {
        this.usuario.set(usuario);
    }

    public StringProperty usuarioProperty() {
        return usuario;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getApellido() {
        return apellido.get();
    }

    public void setApellido(String apellido) {
        this.apellido.set(apellido);
    }

    public StringProperty apellidoProperty() {
        return apellido;
    }

    public String getCorreo() {
        return correo.get();
    }

    public void setCorreo(String correo) {
        this.correo.set(correo);
    }

    public StringProperty correoProperty() {
        return correo;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getRutafoto() {
        return rutafoto.get();
    }

    public void setRutafoto(String rutafoto) {
        this.rutafoto.set(rutafoto);
    }

    public StringProperty rutafotoProperty() {
        return rutafoto;
    }
}
