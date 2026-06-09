package darkpanda73.PandaUtils.PandaColors.Messages.Utils.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
@Setter(lombok.AccessLevel.NONE)
public class RGBColor {
	private int red, green, blue;

	public RGBColor(long dec) {
		this.blue = (int) (dec % 256);
		this.green = (int) ((dec /= 256) % 256);
		this.red = (int) ((dec /= 256) % 256);
	}

	public RGBColor(String hex) {
		this(Long.parseLong(hex.startsWith("#") ? hex.substring(1) : hex, 16));
	}

	public String toSpigotColor() {
		return "&#" + String.format("%02x", red) + String.format("%02x", green) + String.format("%02x", blue);
	}
}
