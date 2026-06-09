package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class XwwwFormUrlEncodedApplication extends Application {

	private Map<String, String> data;

	public XwwwFormUrlEncodedApplication(byte[] content) {
		super(ApplicationType.X_WWW_FORM_URLENCODED, content);
		this.data = Arrays.stream(new String(content).split("&")).map(pair -> pair.split("=", 2))
				.collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
	}

	public String getValue(String key) {
		return this.data.get(key);
	}

}
