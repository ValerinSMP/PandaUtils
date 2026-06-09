package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OctetStreamApplication extends Application {

	private @NonNull Disposition disposition = Disposition.ATTACHMENT;
	private String name;
	private String fileName;

	public OctetStreamApplication(byte[] content) {
		super(ApplicationType.OCTET_STREAM, content);
	}

	@AllArgsConstructor
	public static enum Disposition {
		INLINE("inline"), ATTACHMENT("attachment");

		private String header;

		public String getHeader() {
			return this.header;
		}
	}
}
