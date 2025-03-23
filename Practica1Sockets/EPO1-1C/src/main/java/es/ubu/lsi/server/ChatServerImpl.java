package es.ubu.lsi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.common.ChatMessage.MessageType;

public class ChatServerImpl implements ChatServer {

	/** Puerto por defecto que utilizará el servidor si no se indica otro. */
	private static final int DEFAULT_PORT = 1500;

	/** Identificador incremental para asignar IDs únicos a los clientes. */
	private static int clientId = 0;

	/** Formato usado para mostrar la hora en los mensajes. */
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	/** Puerto en el que el servidor estará escuchando conexiones. */
	private int port;

	/** Bandera para indicar si el servidor está en ejecución. */
	private boolean alive;

	/** Mapa que asocia nombres de usuario con sus respectivos hilos. */
	Map<String, ServerThreadForClient> clientsMap = new HashMap<>();

	/** Mapa que vincula los IDs de clientes con sus nombres de usuario. */
	Map<Integer, String> clientsIdMap = new HashMap<>();

	/** Mapa de usuarios bloqueados temporalmente (baneados). */
	Map<String, Boolean> bannedUsers = new HashMap<>();

	/** Socket principal del servidor. */
	ServerSocket server;

	/** Constructor que inicia el servidor con el puerto predeterminado. */
	public ChatServerImpl() {
		this.alive = true;
		this.port = DEFAULT_PORT;
	}

	/**
	 * Genera y devuelve un ID único para cada cliente nuevo.
	 *
	 * @return ID del cliente.
	 */
	private synchronized int getNextId() {
		return clientId++;
	}

	/**
	 * Imprime por consola el contenido de los mapas de clientes para depuración.
	 */
	public void mostrarMapas() {
		System.out.println("Contenido de clientsMap (Usuario -> Thread):");
		for (Map.Entry<String, ServerThreadForClient> entry : clientsMap.entrySet()) {
			System.out.println("Usuario: " + entry.getKey() + ", Thread: " + entry.getValue().toString());
		}

		System.out.println("\nContenido de clientsIdMap (ID -> Username):");
		for (Map.Entry<Integer, String> entry : clientsIdMap.entrySet()) {
			System.out.println("ID: " + entry.getKey() + ", Usuario: " + entry.getValue());
		}
	}

	/**
	 * Inicia el servidor, abre el socket y empieza a aceptar conexiones entrantes.
	 */
	@Override
	public void startup() {
	    try {
	        this.server = new ServerSocket(this.port);
	        System.out.println("[" + getDateString() + "] Servidor iniciado en el puerto: " + this.port);
	    } catch (IOException e) {
	        System.err.println("ERROR: No se puede conectar al servidor");
	        System.exit(1);
	    }

	    while (alive) {
	        try {
	            System.out.println("Escuchando conexiones en " + server.getInetAddress() + ":" + server.getLocalPort());
	            // Cada nueva conexión es manejada por un hilo independiente.
	            Socket client = server.accept();
	            ServerThreadForClient clientThread = new ServerThreadForClient(client);
	            clientThread.start();
	        } catch (IOException e) {
	            if (!alive) {
	                System.out.println("[" + getDateString() + "] Servidor deteniéndose...");
	            } else {
	                System.err.println("ERROR: No se pudo aceptar la conexión. Apagando el servidor...");
	            }
	            break;
	        }
	    }
	}

	/**
	 * Cierra el servidor y desconecta todos los clientes activos.
	 */
	@Override
	public void shutdown() {
	    alive = false;
	    try {
	        for (ServerThreadForClient client : clientsMap.values()) {
	            client.shutdownClient();
	        }
	        clientsMap.clear();

	        if (server != null && !server.isClosed()) {
	            server.close();
	        }
	    } catch (IOException e) {
	        System.err.println("[" + getDateString() + "] Error durante el apagado del servidor.");
	    } finally {
	        onServerShutdown();
	    }
	}

	/**
	 * Lógica adicional de limpieza al apagar el servidor.
	 */
	private void onServerShutdown() {
	    System.out.println("[" + getDateString() + "] El servidor se ha apagado correctamente.");
	}

	/**
	 * Envía el mensaje recibido a todos los clientes conectados, si no están baneados.
	 *
	 * @param message mensaje a enviar.
	 */
	@Override
	public void broadcast(ChatMessage message) {
	    String senderUsername = getUsernameById(message.getId());

	    if (bannedUsers.getOrDefault(senderUsername, false)) {
	        return;
	    }

	    String time = "[" + getDateString() + "]";
	    String formattedMessage = String.format("%s %s: %s", time, senderUsername, message.getMessage());

	    for (ServerThreadForClient handler : clientsMap.values()) {
	        if (!bannedUsers.getOrDefault(handler.getUsername(), false)) {
	            try {
	                ChatMessage newMsg = new ChatMessage(message.getId(), message.getType(), formattedMessage);
	                handler.output.writeObject(newMsg);
	            } catch (IOException e) {
	                System.err.println("ERROR: No se pudo enviar mensaje al cliente " + handler.getUsername());
	                remove(handler.id);
	            }
	        }
	    }
	}

	/**
	 * Obtiene el nombre de usuario correspondiente a un ID de cliente.
	 *
	 * @param id del cliente.
	 * @return nombre del usuario.
	 */
	public String getUsernameById(int id) {
		return clientsIdMap.get(id);
	}

	/**
	 * Elimina a un cliente del sistema y cierra su conexión.
	 *
	 * @param id identificador del cliente.
	 */
	@Override
	public void remove(int id) {
		String usernameToDelete = getUsernameById(id);
		if (usernameToDelete != null) {
			ServerThreadForClient client = clientsMap.remove(usernameToDelete);
			if (client != null) {
				client.shutdownClient();
				clientsIdMap.remove(id);
				System.out.println("[" + getDateString() + "] Cliente  " + usernameToDelete + " eliminado.");
				System.out.println("Clientes conectados: " + clientsMap.size());
			}
		} else {
			System.out.println("[" + getDateString() + "] No se encontró el cliente con ID: " + usernameToDelete);
		}
	}

	/**
	 * Devuelve la hora actual como texto en formato HH:mm:ss.
	 *
	 * @return hora actual como String.
	 */
	public String getDateString() {
		return sdf.format(new Date());
	}

	/**
	 * Método principal para lanzar el servidor.
	 *
	 * @param args argumentos de línea de comandos.
	 */
	public static void main(String[] args) {
		new ChatServerImpl().startup();
	}

	/**
	 * Hilo que gestiona la conexión y mensajes de un cliente.
	 */
	class ServerThreadForClient extends Thread {

		/** ID único del cliente conectado. */
		private int id;

		/** Indica si el hilo está activo. */
		private boolean running;

		/** Nombre de usuario del cliente. */
		private String username;

		/** Socket de comunicación con el cliente. */
		private Socket socket;

		/** Flujo de entrada del cliente. */
		private ObjectInputStream input;

		/** Flujo de salida hacia el cliente. */
		private ObjectOutputStream output;

		/**
		 * Constructor del hilo que recibe el socket del cliente.
		 *
		 * @param socket conexión con el cliente.
		 */
		public ServerThreadForClient(Socket socket) {
		    this.socket = socket;
		    this.running = true;

		    try {
		        output = new ObjectOutputStream(socket.getOutputStream());
		        input = new ObjectInputStream(socket.getInputStream());
		    } catch (IOException e) {
		        System.err.println("ERROR: No se pudo crear el hilo manejador de la conexión!");
		        try {
		            socket.close();
		        } catch (IOException ex) {
		            System.err.println("ERROR: No se pudo cerrar el socket tras fallar la inicialización.");
		        }
		        throw new RuntimeException("Fallo al inicializar los streams de entrada/salida para el socket.", e);
		    }
		}

		/**
		 * Gestiona la sesión del cliente: recibe mensajes y ejecuta acciones.
		 */
		@Override
		public void run() {
		    try {
		        loginUser();
		        while (running) {
		            ChatMessage message = (ChatMessage) input.readObject();
		            switch (message.getType()) {
		                case MESSAGE:
		                    showTypeMessage(message);
		                    break;
		                case LOGOUT:
		                    System.out.println("[" + getDateString() + "] Usuario desconectado: " + getUsername());
		                    remove(id);
		                    shutdownClient();
		                    running = false;
		                    break;
		                case SHUTDOWN:
		                    if (this.username.equalsIgnoreCase("ADMIN")) {
		                        System.out.println("[" + getDateString() + "] Comando de APAGADO recibido del admin. Apagando el servidor...");
		                        ChatServerImpl.this.shutdown();
		                        return;
		                    } else {
		                        System.out.println("[" + getDateString() + "] Comando de APAGADO recibido de usuario no-admin: " + getUsername() + ". Ignorando.");
		                    }
		                    break;
		                default:
		                    break;
		            }
		        }
		    } catch (ClassNotFoundException | IOException e) {
		        System.err.println("ERROR: Se perdió la conexión con el cliente " + getUsername());
		    } finally {
		        remove(id);
		        if (running) {
		            shutdownClient();
		        }
		    }
		}

		/**
		 * Procesa el contenido del mensaje y actúa según si es comando o texto común.
		 *
		 * @param message mensaje recibido.
		 */
		private void showTypeMessage(ChatMessage message) {
		    if (bannedUsers.getOrDefault(this.username, false)) {
		        return;
		    }

		    List<String> commandAndUser = extractCommandAndUser(message.getMessage());
		    if (!commandAndUser.isEmpty()) {
		        String command = commandAndUser.get(0);
		        String username = commandAndUser.get(1);
		        switch (command) {
		        case "drop":
		            dropUser(username);
		            break;
		        case "ban":
		            banUser(username, true);
		            break;
		        case "unban":
		            banUser(username, false);
		            break;
		        default:
		            break;
		        }
		    } else {
		        broadcast(message);
		    }
		}

		/**
		 * Extrae el comando y usuario objetivo de un mensaje tipo comando.
		 *
		 * @param mensaje texto a analizar.
		 * @return lista con comando y usuario, vacía si no es válido.
		 */
		public List<String> extractCommandAndUser(String mensaje) {
		    List<String> commands = Arrays.asList("drop", "ban", "unban");
		    String[] parts = mensaje.trim().split("\\s+", 2);

		    if (parts.length == 2) {
		        String command = parts[0].toLowerCase();
		        if (commands.contains(command)) {
		            return Arrays.asList(command, parts[1].trim());
		        }
		    }
		    return Collections.emptyList();
		}

		/**
		 * Recibe el nombre de usuario del cliente y lo valida para permitir su conexión.
		 */
		private void loginUser() throws IOException, ClassNotFoundException {
			ChatMessage loginMessage = (ChatMessage) input.readObject();
			if (loginMessage.getType() != MessageType.MESSAGE) {
				System.err.println("ERROR: Se esperaba un mensaje de nombre de usuario, se recibió algo diferente. Cerrando conexión.");
				shutdownClient();
				return;
			}

			this.username = loginMessage.getMessage();
			if (!checkUsername(getUsername())) {
				shutdownClient();
				System.err.println("[" + getDateString() + "] Conexión terminada para el cliente " + getUsername()
						+ ". Este nombre de usuario ya existe.");
				return;
			}

			clientsMap.put(getUsername(), this);
			this.id = getNextId();
			clientsIdMap.put(id, getUsername());
			sendInitialConnectionMessage();
			System.out.println("Clientes conectados: " + clientsMap.size());
		}

		/**
		 * Marca a un usuario como baneado o desbaneado.
		 *
		 * @param username usuario objetivo.
		 * @param ban true para banear, false para desbanear.
		 */
		private void banUser(String username, boolean ban) {
			bannedUsers.put(username, ban);
			String currentDate = getDateString();
			String action = ban ? "baneado" : "desbaneado";
			String logMessage = String.format("[%s] El cliente %s ha sido %s por %s", currentDate, username, action,
					getUsername());
			System.out.println(logMessage);

			broadcast(new ChatMessage(this.id, MessageType.MESSAGE, logMessage));
		}

		/**
		 * Expulsa a un usuario conectado del servidor.
		 *
		 * @param username nombre del usuario a desconectar.
		 */
		private void dropUser(String username) {
			ServerThreadForClient clientToDrop = clientsMap.get(username);
			String currentDate = getDateString();

			if (clientToDrop != null) {
				String dropMessage = String.format("[%s] El cliente %s ha sido eliminado por %s", currentDate, username,
						getUsername());
				System.out.println(dropMessage);
				clientToDrop.shutdownClient();
				remove(clientToDrop.id);

				sendDropMessage(dropMessage);
			} else {
				String notFoundMessage = String.format("[%s] Usuario %s no encontrado.", currentDate, username);

				sendDropMessage(notFoundMessage);
			}
		}

		/**
		 * Informa al cliente sobre el resultado de un intento de expulsión.
		 *
		 * @param message mensaje informativo.
		 */
		private void sendDropMessage(String message) {
			try {
				output.writeObject(new ChatMessage(this.id, MessageType.MESSAGE, message));
			} catch (IOException e) {
				System.err.println("ERROR: Enviando mensaje de fallo de eliminación a " + getUsername());
			}
		}

		/**
		 * Verifica si el nombre de usuario ya está en uso.
		 *
		 * @param username nombre propuesto.
		 * @return true si es válido, false si está repetido.
		 */
		private boolean checkUsername(String username) {
			synchronized (clientsMap) {
				if (clientsMap.containsKey(username)) {
					try {
						output.writeObject(new ChatMessage(0, MessageType.LOGOUT, "El nombre de usuario ya existe."));
					} catch (IOException e) {
						System.err.println("ERROR: No se pudo enviar el mensaje de nombre de usuario existente al cliente.");
					}
					return false;
				}
				return true;
			}
		}

		/**
		 * Devuelve el nombre del usuario asociado a este hilo.
		 *
		 * @return nombre de usuario.
		 */
		public String getUsername() {
			return this.username;
		}

		/**
		 * Envía un mensaje de bienvenida con el ID al usuario recién conectado.
		 */
		private void sendInitialConnectionMessage() {
			try {
				String welcomeMessage = String.format("[%s] Bienvenido(a), %s! Tu ID es %d. Esperando un mensaje...",
						getDateString(), getUsername(), id);
				output.writeObject(new ChatMessage(id, MessageType.MESSAGE, welcomeMessage));
				System.out.println("[" + getDateString() + "] " + getUsername() + " se ha conectado al servidor");
			} catch (IOException e) {
				System.err.println("ERROR: No se pudo enviar el mensaje de conexión inicial al cliente " + getUsername());
			}
		}

		/**
		 * Cierra todos los recursos utilizados por el cliente (streams y socket).
		 */
		private void shutdownClient() {
			running = false;
			closeQuietly(input, "input stream");
			closeQuietly(output, "output stream");
			closeQuietly(socket, "socket");
		}

		/**
		 * Cierra un recurso de manera silenciosa y segura.
		 *
		 * @param resource recurso a cerrar.
		 * @param resourceName nombre del recurso para mostrar en errores.
		 */
		private void closeQuietly(AutoCloseable resource, String resourceName) {
			if (resource != null) {
				try {
					resource.close();
				} catch (Exception e) {
					System.err.println("Error closing the " + resourceName + " for client " + getUsername());
				}
			}
		}
	}
}
