package darkpanda73.PandaUtils.Utilities.Http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Setter(lombok.AccessLevel.NONE)
@Accessors(chain = true)
public class HttpClient {

	private @NonNull String url;
	private @NonNull Method method;
	private HashMap<String, String> headers = new HashMap<>();
	private @Setter int timeout = 2000;
	private byte[] body;

	public static HttpClient inst(String url, Method method) {
		return new HttpClient(url, method);
	}

	public void setBody(String body) {
		this.body = body.getBytes();
	}

	public void setBody(Object body) {
		this.body = body.toString().getBytes();
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public Response send() {
		try {
			URL urlobject = new URL(url);

			HttpURLConnection http = (HttpURLConnection) urlobject.openConnection();
			try {
				for (Entry<String, String> entry : this.headers.entrySet()) {
					http.setRequestProperty(entry.getKey(), entry.getValue());
				}

				http.setConnectTimeout(timeout);
				http.setReadTimeout(timeout);
				http.setRequestMethod(method.name());
				http.setDoInput(true);

				if (body != null) {
					http.setDoOutput(true);
					http.getOutputStream().write(body);
					http.getOutputStream().flush();
				}

				int responseCode = http.getResponseCode();

				try {
					return new Response(responseCode, read(http.getInputStream()));
				} catch (Exception e) {
					try {
						return new Response(responseCode, read(http.getErrorStream()));
					} catch (Exception e2) {
					}
				}

				return new Response(responseCode, null);
			} catch (Exception e) {
				throw e;
			} finally {
				http.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static enum Method {
		GET, POST, PATCH, PUT, DELETE;
	}

	@AllArgsConstructor
	@Getter
	public static class Response {

		private int code;
		private String body;

	}

	public String read(InputStream stream) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			StringBuilder sb = new StringBuilder();
			String input = null;
			try {
				while ((input = reader.readLine()) != null) {
					sb.append(input);
				}
			} catch (SocketTimeoutException e) {
			}
			return sb.toString();
		}
	}
}
