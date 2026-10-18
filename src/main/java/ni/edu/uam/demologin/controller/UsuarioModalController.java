package ni.edu.uam.demologin.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import ni.edu.uam.demologin.model.Usuario;
import ni.edu.uam.demologin.service.ArchivoService;
import ni.edu.uam.demologin.service.VentanaService;

import java.io.File;

public class UsuarioModalController {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtPassword;
    @FXML
    private ImageView imageFoto;

    private final VentanaService ventanaService = new VentanaService();
    private final ArchivoService archivoService = new ArchivoService();

    private Usuario usuario;
    private boolean guardado = false;

    // Copia de los valores originales para poder revertir si se cancela
    private String nombreOriginal;
    private String apellidoOriginal;
    private String correoOriginal;
    private String usuarioOriginal;
    private String passwordOriginal;
    private String fotoOriginal;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        nombreOriginal = usuario.getNombre();
        apellidoOriginal = usuario.getApellido();
        correoOriginal = usuario.getCorreo();
        usuarioOriginal = usuario.getUsuario();
        passwordOriginal = usuario.getPassword();
        fotoOriginal = usuario.getRutafoto();

        txtNombre.setText(nombreOriginal);
        txtApellido.setText(apellidoOriginal);
        txtCorreo.setText(correoOriginal);
        txtUsuario.setText(usuarioOriginal);
        txtPassword.setText(passwordOriginal);
        mostrarImagen(fotoOriginal);
    }

    private void aplicarCambios() {
        usuario.setNombre(txtNombre.getText());
        usuario.setApellido(txtApellido.getText());
        usuario.setCorreo(txtCorreo.getText());
        usuario.setUsuario(txtUsuario.getText());
        usuario.setPassword(txtPassword.getText());
    }

    private void revertirCambios() {
        usuario.setNombre(nombreOriginal);
        usuario.setApellido(apellidoOriginal);
        usuario.setCorreo(correoOriginal);
        usuario.setUsuario(usuarioOriginal);
        usuario.setPassword(passwordOriginal);
        usuario.setRutafoto(fotoOriginal);
    }

    public boolean isGuardado() {
        return guardado;
    }

    @FXML
    private void cambiarFoto(ActionEvent event) {
        if (usuario == null) {
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar archivo");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Imagenes", "*.jpg", "*.png", "*.jpeg"));
        File archivo = fc.showOpenDialog(
                imageFoto.getScene().getWindow());
        if (archivo == null) {
            return;
        }
        try {
            String ruta = archivoService.guardarArchivo(archivo);
            usuario.setRutafoto(ruta);
            mostrarImagen(ruta);
        } catch (Exception e) {
            UtilController.mostrarException(e);
        }
    }

    private void mostrarImagen(String rutaFoto) {
        if (rutaFoto == null || rutaFoto.isBlank()) {
            return;
        }
        File archivo = new File(rutaFoto);
        if (archivo.exists()) {
            Image image = new Image(archivo.toURI().toString());
            imageFoto.setImage(image);
        }
    }

    @FXML
    private void guardar(ActionEvent event) {
        if (txtNombre.getText().isBlank()
                || txtApellido.getText().isBlank()
                || txtCorreo.getText().isBlank()
                || txtUsuario.getText().isBlank()
                || txtPassword.getText().isBlank()) {
            UtilController.mostrarMensaje(
                    javafx.scene.control.Alert.AlertType.WARNING,
                    "Validacion",
                    "Todos los campos son obligatorios");
            return;
        }
        aplicarCambios();
        guardado = true;
        ventanaService.cerrarVentana(event);
    }
    @FXML
    private void cancelar(ActionEvent event) {
        revertirCambios();
        guardado = false;
        ventanaService.cerrarVentana(event);
    }
}
