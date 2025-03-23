package es.ubu.lsi.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

/**
 * Esta clase implementa la interfaz {@link ChatClient} y gestiona la conexión con un servidor de chat.<br>
 * Permite al cliente enviar y recibir mensajes a través de un socket utilizando flujos de entrada y salida de objetos.<br>
 * Práctica 1 de la asignatura de Sistemas Distribuidos del Grado de Ingenería Informática de la Universidad de Burgos
 * @version 1.0.0
 * @author Jon Ander Incera Moreno
 * @author Miguel José Gómez López
 * 
 */
public class ChatClientImpl implements ChatClient {

	//Variables de clase
    private String server;
    private String username;
    private int port;
    private static int id;
    private boolean carryOn = true;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Scanner input;
    
    /**
     * Constructor que inicializa el cliente de chat.
     *
     * @param server Dirección del servidor al que conectarse.
     * @param port Puerto de conexión del servidor.
     * @param username Nombre de usuario del cliente.
     */
    public ChatClientImpl(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
        try {
            socket = new Socket(server, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("ERROR: No se puede lanzar el cliente!");
            System.exit(1);
        }
    }
    
    /**
     * Inicia el cliente y gestiona la interacción con el usuario.
     * El cliente se conecta al servidor, escucha los mensajes entrantes
     * y envía mensajes a través de la consola hasta que el cliente decide
     * cerrar la sesión o apagar el servidor.
     *
     * @return {@code true} si la operación fue exitosa, {@code false} si se produjo un error.
     */
    @Override
    public boolean start() {
        try {
            connect();
            try (Scanner input = new Scanner(System.in)) {
                while (carryOn) {
                    String text = input.nextLine();
                    if (text.equalsIgnoreCase("LOGOUT")) {
                        sendMessage(new ChatMessage(id, MessageType.LOGOUT, ""));
                        return true;
                    } else if (text.equalsIgnoreCase("SHUTDOWN")) {
                        sendMessage(new ChatMessage(id, MessageType.SHUTDOWN, ""));
                        return true;
                    } else {
                        sendMessage(new ChatMessage(id, MessageType.MESSAGE, text));
                    }
                }
            }
        } finally {
            disconnect();
        }
        return true;
    }

    /**
     * Envía un mensaje al servidor a través del flujo de salida.
     *
     * @param msg El mensaje que se desea enviar al servidor.
     */
    @Override
    public void sendMessage(ChatMessage msg) {
        try {
            outputStream.writeObject(msg);
        } catch (IOException e) {
            System.err.println("ERROR: No se ha podido enviar el mensaje.");
            disconnect();
        }
    }

    /**
     * Desconecta el cliente cerrando los recursos asociados a la conexión.
     */
    @Override
    public void disconnect() {
        carryOn = false;
        closeResource(input);
        closeResource(outputStream);
        closeResource(socket);
    }

    /**
     * Cierra un recurso de tipo {@link AutoCloseable}, manejando posibles excepciones.
     *
     * @param resource El recurso que se desea cerrar.
     */
    private void closeResource(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                System.err.println("ERROR: No se pudo cerrar el recurso.");
            }
        }
    }

    /**
     * Establece la conexión con el servidor y gestiona la autenticación
     * del usuario, así como el inicio del hilo para escuchar los mensajes
     * del servidor.
     */
    private void connect() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            sendMessage(new ChatMessage(0, MessageType.MESSAGE, username));
            ChatMessage response = (ChatMessage) inputStream.readObject();
            System.out.println(response.getMessage());
            if (response.getType() == MessageType.LOGOUT) {
                disconnect();
                System.exit(0);
            }
            id = response.getId();
            new Thread(new ChatClientListener(inputStream)).start();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("ERROR: No se recibe respuesta del servidor");
            disconnect();
            System.exit(1);
        }
    }

    /**
     * Muestra una ayuda sobre cómo ejecutar los comandos de Maven para iniciar
     * el servidor y el cliente de chat.
     */
    private static void ayuda() {
        System.out.println("El comando mvn exec:java@servidor inicializa el servidor, debe ejecutarse de primero");
        System.out.println("El comando mvn exec:java@cliente inicia un cliente, puede pasar una dirección IP.");
        System.out.println("En caso de que no, se usará por defecto localhost; pero debe pasar obligatoriamente un nombre de usuario");
    }

    /**
     * Método principal que se ejecuta al iniciar el cliente.
     * Se encarga de manejar los parámetros de entrada y lanzar la
     * aplicación cliente con la configuración correspondiente.
     *
     * @param args Argumentos de línea de comandos que contienen el servidor, puerto y nombre de usuario.
     */
    public static void main(String[] args) {
        int port = 1500;
        String server = "localhost";
        String username = null;

        if (args.length == 2) {
            server = args[0];
            username = args[1];
        }
        else if (args.length == 1) {
            username = args[0];
        }
        else {
            System.err.println("Error en los parámetros.");
            ayuda();
            System.exit(1);
        }

        new ChatClientImpl(server, port, username).start();
    }

    /**
     * Clase interna que se encarga de escuchar los mensajes enviados
     * por el servidor y mostrarlos al usuario en la consola.
     */
    class ChatClientListener implements Runnable {
        private ObjectInputStream serverInput;

        public ChatClientListener(ObjectInputStream in) {
            this.serverInput = in;
        }
        
        /**
         * Método que se ejecuta en un hilo separado para recibir y mostrar
         * los mensajes del servidor mientras el cliente está conectado.
         */
        @Override
        public void run() {
            try {
                while (carryOn) {
                    ChatMessage msg = (ChatMessage) serverInput.readObject();
                    System.out.println(msg.getMessage());
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("ERROR: Conexión perdida...");
                carryOn = false;
            } finally {
                disconnect();
            }
        }
    }
}
