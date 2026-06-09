package darkpanda73.PandaUtils.PandaHTTPServer.Threads;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;

import darkpanda73.PandaUtils.PandaHTTPServer.Enums.Response;
import darkpanda73.PandaUtils.PandaHTTPServer.Exceptions.HttpResponse;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Text.HtmlText;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Endpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.DeleteEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.GetEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.HeadEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.OptionsEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.PostEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.PutEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Streams.HttpInputStream;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Streams.HttpOutputStream;
import darkpanda73.PandaUtils.PandaThreads.Exceptions.AlreadyStartedException;
import darkpanda73.PandaUtils.PandaThreads.Objects.Thread;

public class ClientThread extends Thread implements Thread.Runnable, Thread.CallBack {

	private HTTPServer server;
	private Socket client;

	public ClientThread(HTTPServer server, Socket client) throws AlreadyStartedException {
		super();
		this.setRunnable(this);
		this.setCallBack(this);
		this.server = server;
		this.client = client;
		server.getClientListener().getClientSockets().add(client);
		this.start();
	}

	public static void processSocket(HTTPServer server, Socket client) {
		/*
		 * Initializing variables
		 */
		HttpInputStream input = null;
		HttpOutputStream output = null;
		try {
			input = new HttpInputStream(server.getSettings(), client.getInputStream());
			output = new HttpOutputStream(client.getOutputStream());

			/*
			 * Loading and treating path
			 */

			try {
				input.loadRequestMethod();
			} catch (Exception e) {
				return;
			}

			// Getting related page
			Entry<Endpoint, String[]> endpoint = server.getEndpointAndArgs(input.getRequestMethod());

			if (endpoint != null) {
				/*
				 * Loading and treating headers
				 */

				input.loadHeaders();

				endpoint.getKey().processPreRequest(server, input, endpoint.getValue());

				try {
					if (endpoint.getKey().allowContent(server, input, endpoint.getValue())) {
						/*
						 * Loading and treating content
						 */
						input.loadContent();
					}

					try {
						switch (input.getRequestMethod().getMethod()) {
						case DELETE:
							((DeleteEndpoint) endpoint.getKey()).processDeleteRequest(server, input,
									endpoint.getValue());
							break;
						case GET:
							((GetEndpoint) endpoint.getKey()).processGetRequest(server, input, endpoint.getValue());
							break;
						case HEAD:
							((HeadEndpoint) endpoint.getKey()).processHeadRequest(server, input, endpoint.getValue());
							break;
						case OPTIONS:
							((OptionsEndpoint) endpoint.getKey()).processOptionsRequest(server, input,
									endpoint.getValue());
							break;
						case POST:
							((PostEndpoint) endpoint.getKey()).processPostRequest(server, input, endpoint.getValue());
							break;
						case PUT:
							((PutEndpoint) endpoint.getKey()).processPutRequest(server, input, endpoint.getValue());
							break;
						}
					} catch (HttpResponse e) {
						throw e;
					} catch (Exception e) {
						e.printStackTrace();
						throw e;
					}

					throw new HttpResponse(Response.INTERNAL_SERVER_ERROR);
				} catch (HttpResponse e) {
					throw e;
				} catch (Exception e) {
					throw new HttpResponse(Response.INTERNAL_SERVER_ERROR);
				}
			} else {
				throw new HttpResponse(Response.NOT_FOUND);
			}
		} catch (HttpResponse e) {
			if (output != null) {
				output.setResponseStatus(e.getResponse().responseStatus()).setContent(e.getContent())
						.setHeaders(e.getHeaders());
			}
		} catch (Exception e) {
			if (!(e instanceof InterruptedException) && !(e instanceof SocketException)) {
				if (server.getExternalCallback() != null) {
					server.getExternalCallback().onError(e);
				}
			}

			if (output.getResponseStatus() == null) {
				output.setResponseStatus(Response.INTERNAL_SERVER_ERROR.responseStatus())
						.setContent(
								new HtmlText("<h1>500 INTERNAL SERVER ERROR</h1><br><p>"
										+ e.getMessage() + "<br>" + Arrays.stream(e.getStackTrace())
												.map(trace -> trace.toString()).collect(Collectors.joining("<br>"))
										+ "</p>"));
			}
		} finally {
			try {
				if (output != null && output.getResponseStatus() != null) {
					if (!client.isClosed()) {
						output.loadValues();
						output.sendData(server.getSettings().getBytesPerSecond());
					}
				} else {
					client.close();
				}
			} catch (Exception e) {
				System.out.println("HTTP Request closed => " + e.getMessage());
			}
		}
	}

	public void onCloseThread() {
		try {
			client.close();
		} catch (IOException e) {
		}
		server.getClientListener().getClientSockets().remove(client);
	}

	/*
	 * Thread methods
	 */

	@Override
	public void run() {
		if (client instanceof SSLSocket) {
			SSLSocket sslsocket = (SSLSocket) client;
			sslsocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
				@Override
				public void handshakeCompleted(HandshakeCompletedEvent event) {
					processSocket(server, event.getSocket());
				}
			});
			try {
				sslsocket.startHandshake();
			} catch (Exception e) {
				if (server.getExternalCallback() != null) {
					server.getExternalCallback().onError(e);
				}
			}
		} else {
			processSocket(server, client);
		}
	}

	@Override
	public void onFinish() {
		onCloseThread();
	}

	@Override
	public void onInterrupt() {
		onCloseThread();
	}

	/*
	 * Empty methods
	 */

	@Override
	public void onError(Exception ex) {
	}

	@Override
	public void onInput(Object input) {
	}

	@Override
	public void onStart() {
	}

	@Override
	public void output(Object output) {
	}

}
