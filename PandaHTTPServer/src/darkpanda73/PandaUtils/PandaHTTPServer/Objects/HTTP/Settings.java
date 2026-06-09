package darkpanda73.PandaUtils.PandaHTTPServer.Objects.HTTP;

import darkpanda73.PandaUtils.PandaHTTPServer.Utils.SettingsUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Settings {

	private long bytesPerSecond = SettingsUtils.DEFAULT_BYTES_PER_SECOND;
	private long maxMethodLength = SettingsUtils.DEFAULT_INPUT_MAX_METHOD_LENGTH;
	private long maxHeadersLength = SettingsUtils.DEFAULT_INPUT_MAX_HEADERS_LENGTH;
	private long maxContentLength = SettingsUtils.DEFAULT_INPUT_MAX_CONTENT_LENGTH;

}
