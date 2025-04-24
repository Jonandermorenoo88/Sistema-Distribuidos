package es.ubu.gii.sd.epo2;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class ControladorExcepciones {
    
	@ExceptionHandler(HttpClientErrorException.class)
    public String erroresAPI(HttpClientErrorException ex, Model model) {
        // Extraemos el mensaje de la respuesta de la API
        String mensajeError = ex.getResponseBodyAsString();
        
        // Agregamos el mensaje de error al modelo para que esté disponible en la vista
        model.addAttribute("mensaje", mensajeError);
        
        // Dependiendo del tipo de error, redirigimos a una página de error general
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return "error";  // Página de error cuando la API no encuentra el recurso
        } else if (ex.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            return "error";  // Página de error para errores internos de la API
        }
        
        return "error";  // Redirige a la página de error en cualquier otro caso
    }
}
