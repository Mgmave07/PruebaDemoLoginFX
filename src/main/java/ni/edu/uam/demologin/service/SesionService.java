package ni.edu.uam.demologin.service;

import ni.edu.uam.demologin.model.Usuario;

public final class SesionService {
    private static Usuario usuarioAutenticado = null;

    private SesionService() {
        // clase utilitaria: no se instancia
    }

    /** Llamada exclusivamente desde un login exitoso. */
    public static void iniciarSesion(Usuario usuario) {
        usuarioAutenticado = usuario;
    }

    /** Llamada al cerrar sesion o regresar al login. */
    public static void cerrarSesion() {
        usuarioAutenticado = null;
    }

    public static boolean haySesionActiva() {
        return usuarioAutenticado != null;
    }

    public static Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }
}
