package es.ubu.gii.sd.epo2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void guardarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, usuario, contrasena) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                usuario.getNombre(),
                usuario.getUsuario(),
                usuario.getContrasena());
    }
    
    public boolean validarCredenciales(String usuario, String contrasena) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE usuario = ? AND contrasena = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, usuario, contrasena);
        return count != null && count > 0;
    }
}
