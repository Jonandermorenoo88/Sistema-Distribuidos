package es.ubu.lsi.server;

import java.io.IOException;
import es.ubu.lsi.common.ChatMessage;

/**
 * Esta interfaz define las operaciones básicas para un servidor de chat. <br>
 * Un servidor de chat maneja la comunicación entre múltiples clientes, permitiendo la transmisión de mensajes y la gestión de la conexión de los mismos.
 * Práctica 1 de la asignatura de Sistemas Distribuidos del Grado de Ingenería Informática de la Universidad de Burgos
 * @version 1.0.0
 * @author Jon Ander Incera Moreno
 * @author Miguel José Gómez López
 */
public interface ChatServer {
    
    /**
     * Inicia el servidor de chat. Este método debe ser implementado para establecer
     * la conexión con los clientes y comenzar a aceptar conexiones entrantes.
     *
     * @throws IOException si ocurre un error al intentar iniciar el servidor
     */
    void startup() throws IOException;

    /**
     * Detiene el servidor de chat. Este método debe cerrar todas las conexiones 
     * abiertas y liberar los recursos utilizados por el servidor.
     */
    void shutdown();

    /**
     * Envía un mensaje a todos los clientes conectados al servidor de chat.
     * Este método debe ser implementado para realizar la difusión del mensaje
     * a través de las conexiones activas.
     *
     * @param message El mensaje que se desea enviar a los clientes conectados.
     */
    void broadcast(ChatMessage message);

    /**
     * Elimina a un cliente del servidor de chat, identificándolo mediante su
     * ID. Este método debe ser implementado para gestionar la desconexión 
     * de un cliente.
     *
     * @param id El ID del cliente que se desea eliminar.
     */
    void remove(int id);
}