package ni.edu.uam.demologin.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ni.edu.uam.demologin.model.Usuario;
import ni.edu.uam.demologin.service.NavegationService;
import ni.edu.uam.demologin.service.SesionService;
import ni.edu.uam.demologin.service.UsuarioService;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    private final NavegationService
            navegationService = new NavegationService();

    private final UsuarioService service;

    public LoginController() {
        this.service = new UsuarioService();
    }

    @FXML
    private void iniciarSesion(ActionEvent actionEvent) throws Exception {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();
        if (usuario.isBlank() || password.isBlank()) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Validacion",
                    "Debe de ingresar usuario y contraseña"
            );
            return;
        }
        else {
            try {
                Usuario u = service.findByLogin(usuario,password)
                        .orElse(null);
                if (u != null) {
                    SesionService.iniciarSesion(u);
                    navegationService.cambiarVista(

                            "usuario-table.fxml", txtUsuario);
                }
                else {
                    UtilController.mostrarMensaje(
                            Alert.AlertType.WARNING,
                            "Login",
                            "Usuario o contrasena incorrectos"
                    );
                }
            } catch (Exception e) {
                UtilController.mostrarException(e);
            }
        }
    }

    @FXML
    private void abrirRegistro(ActionEvent actionEvent){
        try {
            navegationService.cambiarVista("registro.fxml",txtUsuario);
        }catch (Exception e){
            UtilController.mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Validacion",
                    "Error al cargar el registro de usuario"
            );
        }
    }

}
