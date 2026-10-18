package ni.edu.uam.demologin.repository;

import ni.edu.uam.demologin.config.IConnection;
import ni.edu.uam.demologin.config.PostgreSQLConnection;
import ni.edu.uam.demologin.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioRepositoryJdbc implements UsuarioRepository {

    private static final String SELECT_COLUMNS =
            "SELECT id," +
                    "primer_nombre," +
                    "primer_apellido," +
                    "ruta_foto," +
                    "usuario," +
                    "correo," +
                    "password " +
                    "FROM usuario ";

    private final IConnection connection;

    public UsuarioRepositoryJdbc(IConnection connection) {
        this.connection = connection;
    }

    @Override
    public List<Usuario> findAll() throws Exception {
        String sql = SELECT_COLUMNS + " ORDER BY primer_nombre ASC";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            return usuarios;
        }
    }



    @Override
    public Optional<Usuario> findById(UUID id) throws Exception {
        String sql = SELECT_COLUMNS + " WHERE id = ?";
        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void save(Usuario usuario) throws Exception {
        String sql = "INSERT INTO usuario " +
                     "(id," +
                     "primer_nombre," +
                     "primer_apellido," +
                     "usuario," +
                     "password," +
                     "correo," +
                     "ruta_foto)" +
                     "VALUES(?,?,?,?,?,?,?)";
        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (usuario.getId() == null) {
                usuario.setId(UUID.randomUUID());
            }
            ps.setObject(1, usuario.getId());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getUsuario());
            ps.setString(5, usuario.getPassword());
            ps.setString(6, usuario.getCorreo());
            ps.setString(7, usuario.getRutafoto());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Usuario usuario) throws Exception {
        String sql = "UPDATE usuario SET " +
                "primer_nombre = ?," +
                "primer_apellido = ?," +
                "usuario = ?," +
                "password = ?," +
                "correo = ?," +
                "ruta_foto = ? " +
                "WHERE id = ?";
        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getUsuario());
            ps.setString(4, usuario.getPassword());
            ps.setString(5, usuario.getCorreo());
            ps.setString(6, usuario.getRutafoto());
            ps.setObject(7, usuario.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(UUID id) throws Exception {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Usuario> findByLogin(String usuario
            , String password) throws Exception {
        final String sql = " SELECT id,usuario,correo,password FROM public.usuario WHERE usuario = ? AND password = ? ";
        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setUsuario(rs.getString("usuario"));
                    u.setPassword(rs.getString("password"));
                    u.setCorreo(rs.getString("correo"));
                    u.setId(rs.getObject("id", UUID.class));
                    return Optional.of(u);
                }
            }
        }
        return Optional.empty();
    }
    private Usuario mapearUsuario(ResultSet rs) throws Exception {
        Usuario usuario = new Usuario(
                rs.getString("primer_nombre"),
                rs.getString("primer_apellido"),
                rs.getString("correo"),
                rs.getString("usuario"),
                rs.getString("password"),
                rs.getString("ruta_foto")
        );
        usuario.setId(rs.getObject("id", UUID.class));
        return usuario;
    }
}
