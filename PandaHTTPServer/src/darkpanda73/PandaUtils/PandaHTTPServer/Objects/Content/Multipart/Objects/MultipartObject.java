package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Multipart.Objects;

import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Content;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "content")
public class MultipartObject {

	private String name, fileName;
	private Content content;

}