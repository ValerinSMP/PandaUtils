package darkpanda73.PandaUtils.PandaHTTPServer.Threads;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Endpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.HTTP.RequestMethod;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.HTTP.Settings;
import darkpanda73.PandaUtils.PandaHTTPServer.Utils.SSLUtils;
import darkpanda73.PandaUtils.PandaThreads.PandaThreadsService;
import darkpanda73.PandaUtils.PandaThreads.Exceptions.AlreadyStartedException;
import darkpanda73.PandaUtils.PandaThreads.Objects.Thread;
import darkpanda73.PandaUtils.Utilities.Java.String.StringUtilities;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class HTTPServer {

	/*
	 * HTTP Settings
	 */

	private int port = 80;
	private @Getter Settings settings = new Settings();
	private String mainFolderPath = "./html";

	/*
	 * Server
	 */

	private Thread.CallBack externalCallback;
	private ClientListener clientListener;
	private @Getter(lombok.AccessLevel.NONE) ServerSocketFactory factory = null;
	private @Getter(lombok.AccessLevel.NONE) ServerSocket server = null;
	private boolean running;

	/*
	 * Constructors
	 */

	public HTTPServer() {
		this.port = 80;
		factory = ServerSocketFactory.getDefault();
	}

	public HTTPServer(File privkey, File fullchain) throws Exception {
		this(privkey, fullchain, "");
	}

	public HTTPServer(File privkey, File fullchain, String password) throws Exception {
		this.port = 443;
		SSLContext context = SSLUtils.createSSLContext(privkey, fullchain, password);
		if (context != null) {
			this.factory = context.getServerSocketFactory();
		} else {
			throw new Exception("Not able to create a SSLContext!");
		}
	}

	public void startServer() throws IOException, BindException, AlreadyStartedException {
		running = true;
		server = factory.createServerSocket(port);
		server.setSoTimeout(10000);

		clientListener = new ClientListener(this, externalCallback);
		clientListener.start();
	}

	public void stopServer() throws Exception {
		this.running = false;
		if (this.server != null) {
			if (this.clientListener != null) {
				for (Socket client : new ArrayList<>(this.clientListener.getClientSockets())) {
					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				this.clientListener.getClientSockets().clear();
				this.clientListener.threadManager.unregisterThreads();
				this.server.close();

				if (this.clientListener.isStarted()) {
					this.clientListener.stop();
				}

				this.clientListener = null;
				this.server = null;
			} else {
				this.server.close();
				this.server = null;
			}
		}
	}

	/*
	 * Pages
	 */

	private List<Endpoint> endpoints = new ArrayList<>();

	public Entry<Endpoint, String[]> getEndpointAndArgs(RequestMethod requestMethod) {
		int idIndex = requestMethod.getPath().lastIndexOf("#");
		String[] pathSplit = StringUtilities
				.removeLeadingTrailingChar(
						idIndex != -1 ? requestMethod.getPath().substring(0, idIndex) : requestMethod.getPath(), '/')
				.split("/");
		Entry<String[], Endpoint> endpoint = endpoints.stream()
				.filter(entry -> Arrays.stream(entry.getAllowedMethods())
						.filter(method -> method == requestMethod.getMethod()).findAny().isPresent())
				.map(entry -> new SimpleEntry<String[], Endpoint>(
						StringUtilities.removeLeadingTrailingChar(entry.getPath(), '/').split("/"), entry))
				.filter(entry -> entry.getKey().length <= pathSplit.length).filter(entry -> {
					for (int i = 0; i < entry.getKey().length; i++) {
						if (!entry.getKey()[i].equals("*") && !entry.getKey()[i].equalsIgnoreCase("<arg>")
								&& (i >= pathSplit.length || !entry.getKey()[i].equals(pathSplit[i]))) {
							return false;
						}
					}
					return true;
				}).sorted((entry1, entry2) -> entry2.getKey().length - entry1.getKey().length).findFirst().orElse(null);
		if (endpoint != null) {
			List<String> args = new ArrayList<>();
			String[] key = endpoint.getKey();
			for (int i = 0; i < key.length; i++) {
				if (key[i].equalsIgnoreCase("<arg>")) {
					args.add(pathSplit[i]);
				} else if (key[i].equals("*")) {
					for (; i < pathSplit.length; i++) {
						args.add(pathSplit[i]);
					}
				}
			}
			return new SimpleEntry<Endpoint, String[]>(endpoint.getValue(), args.toArray(new String[args.size()]));
		}
		return null;
	}

	public HTTPServer removeEndpoint(String path) {
		Iterator<Endpoint> it = this.endpoints.iterator();
		while (it.hasNext()) {
			if (it.next().getPath().equals(path)) {
				it.remove();
			}
		}
		return this;
	}

	public HTTPServer addEndpoint(Endpoint endpoint) {
		this.endpoints.add(endpoint);
		return this;
	}

	public class ClientListener extends Thread implements Thread.Runnable {

		private HTTPServer httpServer;
		private @Getter PandaThreadsService threadManager = new PandaThreadsService();
		private @Getter List<Socket> clientSockets = new ArrayList<>();

		public ClientListener(HTTPServer httpServer, Thread.CallBack callback) {
			this.httpServer = httpServer;

			this.setRunnable(this);
			this.setCallBack(callback);
		}

		@Override
		public void run() {
			try {
				while (this.httpServer.isRunning()) {
					Socket socket = null;
					try {
						socket = server.accept();
						if (clientSockets.size() <= 65535) {
							threadManager.registerThread((Thread) new ClientThread(httpServer, socket)).startSecure();
						} else {
							socket.close();
						}
					} catch (Exception e) {
						if (socket != null && !(e instanceof SocketTimeoutException)) {
							socket.close();
							if (this.getCallback() != null) {
								this.getCallback().onError(e);
							}
						}
					}
				}
			} catch (Exception e) {
				this.getCallback().onError(e);
			}
		}

		@Override
		public void output(Object output) {
			if (this.getCallback() != null) {
				this.getCallback().onInput(output);
			}
		}

	}

}