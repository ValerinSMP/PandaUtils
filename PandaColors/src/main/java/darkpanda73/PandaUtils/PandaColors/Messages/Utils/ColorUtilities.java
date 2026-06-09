package darkpanda73.PandaUtils.PandaColors.Messages.Utils;

public class ColorUtilities {

	private static final String MODIFIER_REGEX = ".*(§[0-9a-fklmnorxA-FKLMNORX])+.*";
	private static final String COLOR_REGEX = ".*(§[0-9a-fA-F])+.*";
	private static final String STYLE_REGEX = ".*(§[klmnoKLMNO])+.*";
	private static final String RESET_REGEX = ".*(§r)+.*";
	private static final String HEX_REGEX = ".*(§x(§[0-9a-fA-F]{1}){6})+.*";

	public static String strip(String msg) {
		return org.bukkit.ChatColor.stripColor(color(msg));
	}

	public static String color(String msg) {
		char[] charArray = msg.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if (charArray[i] == '&' && (i == 0 || charArray[i - 1] != '\\')) {
				charArray[i] = '§';
			}
		}
		return new String(charArray).replace("\\&", "&");
	}

	public static String stripColorExceed(String text) {
		int start = 0, end = -1;
		String startModifier = null, endModifier = null, inside = null;
		while (start < text.length() && (start = text.indexOf('§', start)) != -1) {
			if (start + 1 < text.length()) {
				startModifier = text.substring(start, (start += 2));
				if (startModifier.equals("§x")) {
					if (start + 11 < text.length()) {
						try {
							startModifier += text.substring(start, (start + 12));
							if (!startModifier.matches(HEX_REGEX)) {
								continue;
							} else {
								start += 12;
							}
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					} else {
						continue;
					}
				}

				while ((end = text.indexOf(startModifier, start)) != -1) {
					endModifier = text.substring(end, end + startModifier.length());
					inside = text.substring(start, end);
					if (!inside.isEmpty()) {
						if (startModifier.matches(HEX_REGEX) || startModifier.matches(COLOR_REGEX)
								|| inside.matches(MODIFIER_REGEX)) {
							if (inside.matches(MODIFIER_REGEX)) {
								break;
							}
						} else if (startModifier.matches(STYLE_REGEX)) {
							if (inside.contains("§x") || inside.matches(COLOR_REGEX) || inside.matches(RESET_REGEX)
									|| inside.matches(HEX_REGEX) || inside.matches(MODIFIER_REGEX)) {
								break;
							}
						}
					}
					text = text.substring(0, end) + text.substring(end + endModifier.length(), text.length());
				}
			}
		}
		return text;
	}

}
