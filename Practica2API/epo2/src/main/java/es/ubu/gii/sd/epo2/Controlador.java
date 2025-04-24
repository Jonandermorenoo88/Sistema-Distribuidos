package es.ubu.gii.sd.epo2;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.ui.Model;

@Controller
public class Controlador {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping({"/", "/inicio"})
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/crear_usuario")
    public String crearUsuario() {
        return "crear_usuario";
    }

    @GetMapping("/paises")
    public String getPaises(Model model) {
        // Consumir API externa
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:4400/paises";

        Pais[] paisesArray = restTemplate.getForObject(url, Pais[].class);
        List<Pais> paises = Arrays.asList(paisesArray);

        model.addAttribute("paises", paises);
        return "paises";
    }

    @PostMapping("/crear-usuario")
    public String guardarUsuario(
            @RequestParam String nombre,
            @RequestParam String usuario,
            @RequestParam String contrasena,
            Model model) {

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setUsuario(usuario);
        nuevo.setContrasena(contrasena);

        usuarioRepository.guardarUsuario(nuevo);

        model.addAttribute("mensaje", "Usuario creado correctamente");
        return "inicio";
    }

    @PostMapping("/login")
    public String login(@RequestParam String usuario,
                        @RequestParam String contrasena,
                        Model model) {

        boolean valido = usuarioRepository.validarCredenciales(usuario, contrasena);

        if (valido) {
            return "redirect:/paises";
        } else {
            model.addAttribute("mensaje", "Usuario o contraseña incorrectos");
            return "inicio";
        }
    }
    
    @GetMapping({"/crear_pais"})
    public String crearPais() {
        return "crear_pais";
    }
    
    @PostMapping("/crear-pais")
    public String crearPais(
            @RequestParam String nombre,
            @RequestParam String capital,
            @RequestParam String continente,
            @RequestParam int poblacion,
            Model model) {

        // Crear el objeto Pais con los datos del formulario
        Pais nuevoPais = new Pais();
        nuevoPais.setNombre(nombre);
        nuevoPais.setCapital(capital);
        nuevoPais.setContinente(continente);
        nuevoPais.setPoblacion(poblacion);

        // Hacer POST al API externa
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://127.0.0.1:4400/paises";

        restTemplate.postForObject(url, nuevoPais, String.class);

        // Redirigir a la tabla actualizada
        return "redirect:/paises";
    }
    
    @PostMapping("/borrar-pais")
    public String borrarPais(@RequestParam("id") Long id) {
        String url = "http://127.0.0.1:4400/paises/" + id;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(url);

        return "redirect:/paises";
    }
    
    @GetMapping("/editar_pais/{id}")
    public String editarPais(@PathVariable("id") Long id, Model model) {
        String url = "http://127.0.0.1:4400/paises/" + id;
        RestTemplate restTemplate = new RestTemplate();

        Pais pais = restTemplate.getForObject(url, Pais.class);

        model.addAttribute("pais", pais);
        return "editar_pais";
    }
    
    @PostMapping("/editar-pais")
    public String actualizarPais(
            @RequestParam Long id,
            @RequestParam String nombre,
            @RequestParam String capital,
            @RequestParam String continente,
            @RequestParam int poblacion) {

        // Crear el objeto con los datos del formulario
        Pais paisActualizado = new Pais();
        paisActualizado.setId(id);
        paisActualizado.setNombre(nombre);
        paisActualizado.setCapital(capital);
        paisActualizado.setContinente(continente);
        paisActualizado.setPoblacion(poblacion);

        // Realizar PUT al API externa
        String url = "http://127.0.0.1:4400/paises/" + id;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(url, paisActualizado);

        return "redirect:/paises";
    }
    
    @GetMapping("/paises/txt")
    public String descargarPaises() {
        // Redirige directamente a la URL del endpoint donde el archivo está disponible.
        return "redirect:http://localhost:4400/paises/txt";
    }
    
}
