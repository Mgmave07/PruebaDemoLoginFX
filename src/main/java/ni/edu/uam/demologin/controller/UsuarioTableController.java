package ni.edu.uam.demologin.controller;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ni.edu.uam.demologin.Main;
import ni.edu.uam.demologin.controller.UsuarioModalController;
import ni.edu.uam.demologin.controller.UtilController;
import ni.edu.uam.demologin.model.Usuario;
import ni.edu.uam.demologin.service.NavegationService;
import ni.edu.uam.demologin.service.UsuarioService;

import java.io.IOException;
import java.util.Optional;

public class UsuarioTableController {

    @FXML
    private TableView<Usuario> tableUsuario;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colApellido;
    @FXML
    private TableColumn<Usuario, String> colCorreo;
    @FXML
    private TableColumn<Usuario, String> colUsuario;
    @FXML
    private TextField txtBuscar;

    private final ObservableList<Usuario> listaUsuarios =
            FXCollections.observableArrayList();
    private FilteredList<Usuario> listaFiltrada;

    private final UsuarioService usuarioService = new UsuarioService();
    private final NavegationService navegationService =
            new NavegationService();

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getNombre()));
        colApellido.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getApellido()));
        colCorreo.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getCorreo()));
        colUsuario.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(c.getValue().getUsuario()));

        listaFiltrada = new FilteredList<>(listaUsuarios, u -> true);
        tableUsuario.setItems(listaFiltrada);

        txtBuscar.textProperty().addListener((obs, oldV, nuevo) -> {
            String filtro = nuevo == null ? "" : nuevo.trim().toLowerCase();
            listaFiltrada.setPredicate(usuario -> {
                if (filtro.isEmpty()) {
                    return true;
                }
                return contains(usuario.getNombre(), filtro)
                        || contains(usuario.getApellido(), filtro)
                        || contains(usuario.getCorreo(), filtro)
                        || contains(usuario.getUsuario(), filtro);
            });
        });

        cargarUsuarios();
    }

    private boolean contains(String valor, String filtro) {
        return valor != null && valor.toLowerCase().contains(filtro);
    }

    public void cargarUsuarios() {
        try {
            listaUsuarios.setAll(usuarioService.findAll());
        } catch (Exception e) {
            UtilController.mostrarException(e);
        }
    }

    @FXML
    private void actualizar(ActionEvent event) {
        cargarUsuarios();
    }

    @FXML
    private void nuevoRegistro(ActionEvent event) {
        abrirVentanaRegistro("No se pudo abrir el formulario de registro");
    }

    private void abrirVentanaRegistro(String mensajeError) {
        try {
            // Ruta relativa al paquete de Main (ni/edu/uam/demologin),
            // igual que login.fxml. Si se pone el paquete completo,
            // getResource() devuelve null y FXMLLoader lanza
            // IllegalStateException: Location is not set.
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("registro.fxml")
            );
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nuevo Usuario");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.ERROR, "Validacion", mensajeError);
        }
        // Al cerrar el formulario, refrescar la tabla con los registros
        // actuales (INSERT nuevo o cancelado)
        cargarUsuarios();
    }

    @FXML
    private void editarUsuario(ActionEvent event) {
        Usuario seleccionado = tableUsuario.getSelectionModel()
                .getSelectedItem();
        if (seleccionado == null) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Edicion",
                    "Debe seleccionar un registro de la tabla"
            );
            return;
        }
        if (abrirModal(seleccionado)) {
            // El modal edito las propiedades del objeto directamente;
            // persistir los cambios con UPDATE
            try {
                usuarioService.update(seleccionado);
                UtilController.mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Edicion",
                        "Registro actualizado correctamente"
                );
            } catch (Exception e) {
                UtilController.mostrarException(e);
            }
            cargarUsuarios();
        }
    }

    @FXML
    private void eliminarUsuario(ActionEvent event) {
        Usuario seleccionado = tableUsuario.getSelectionModel()
                .getSelectedItem();
        if (seleccionado == null) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Eliminar",
                    "Debe seleccionar un registro de la tabla"
            );
            return;
        }
        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Desea eliminar el registro de "
                        + seleccionado.getNombre()
                        + " " + seleccionado.getApellido() + "?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmacion.setHeaderText(null);
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent()
                && respuesta.get() == ButtonType.YES) {
            try {
                usuarioService.delete(seleccionado.getId());
                UtilController.mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Eliminar",
                        "Registro eliminado correctamente"
                );
            } catch (Exception e) {
                UtilController.mostrarException(e);
            }
            cargarUsuarios();
        }
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            navegationService.cambiarVista("login.fxml", txtBuscar);
        } catch (Exception e) {
            UtilController.mostrarException(e);
        }
    }

    private boolean abrirModal(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource(
                            "usuario-modal.fxml")
            );
            Parent root = loader.load();
            UsuarioModalController controller = loader.getController();
            controller.setUsuario(usuario);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Usuario");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
            return controller.isGuardado();
        } catch (IOException e) {
            UtilController.mostrarException(e);
            return false;
        }
    }
}
