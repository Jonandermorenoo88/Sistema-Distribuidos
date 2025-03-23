package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;

/**
 * Interfaz que define los métodos básicos para un cliente de chat.<br>
 * Los clientes deben implementar esta interfaz para poder conectarse a un servidor de chat, enviar mensajes y desconectarse de la sesión.<br>
 * Práctica 1 de la asignatura de Sistemas Distribuidos del Grado de Ingenería Informática de la Universidad de Burgos
 * @version 1.0.0
 * @author Jon Ander Incera Moreno
 * @author Miguel José Gómez López
 */
public interface ChatClient {

    /**
     * Inicia la comunicación con el servidor de chat.
     * Este método debe gestionar la conexión al servidor, recibir y enviar mensajes,
     * y mantener la sesión activa hasta que el cliente decida desconectarse.
     *
     * @return {@code true} si la operación fue exitosa, {@code false} si se produjo un error.
     */
    boolean start();

    /**
     * Envía un mensaje al servidor de chat.
     * Este método debe utilizar el mensaje proporcionado para transmitirlo al servidor
     * a través de la conexión establecida.
     *
     * @param msg El mensaje que se desea enviar al servidor.
     */
    void sendMessage(ChatMessage msg);

    /**
     * Desconecta el cliente del servidor de chat.
     * Este método debe cerrar la conexión y liberar los recursos asociados a la sesión.
     */
    void disconnect();
}
