package darkpanda73.PandaUtils.PandaColors.Enums;

import java.lang.reflect.Method;

import darkpanda73.PandaUtils.PandaColors.Messages.Utils.Objects.RGBColor;

public enum ChatColor {
	BLACK('0', net.md_5.bungee.api.ChatColor.BLACK), DARK_BLUE('1', net.md_5.bungee.api.ChatColor.DARK_BLUE),
	DARK_GREEN('2', net.md_5.bungee.api.ChatColor.DARK_GREEN), DARK_AQUA('3', net.md_5.bungee.api.ChatColor.DARK_AQUA),
	DARK_RED('4', net.md_5.bungee.api.ChatColor.DARK_RED), DARK_PURPLE('5', net.md_5.bungee.api.ChatColor.DARK_PURPLE),
	GOLD('6', net.md_5.bungee.api.ChatColor.GOLD), GRAY('7', net.md_5.bungee.api.ChatColor.GRAY),
	DARK_GRAY('8', net.md_5.bungee.api.ChatColor.DARK_GRAY), BLUE('9', net.md_5.bungee.api.ChatColor.BLUE),
	GREEN('a', net.md_5.bungee.api.ChatColor.GREEN), AQUA('b', net.md_5.bungee.api.ChatColor.AQUA),
	RED('c', net.md_5.bungee.api.ChatColor.RED), LIGHT_PURPLE('d', net.md_5.bungee.api.ChatColor.LIGHT_PURPLE),
	YELLOW('e', net.md_5.bungee.api.ChatColor.YELLOW), WHITE('f', net.md_5.bungee.api.ChatColor.WHITE),
	MAGIC('k', net.md_5.bungee.api.ChatColor.MAGIC), BOLD('l', net.md_5.bungee.api.ChatColor.BOLD),
	STRIKETHROUGH('m', net.md_5.bungee.api.ChatColor.STRIKETHROUGH),
	UNDERLINE('n', net.md_5.bungee.api.ChatColor.UNDERLINE), ITALIC('o', net.md_5.bungee.api.ChatColor.ITALIC),
	RESET('r', net.md_5.bungee.api.ChatColor.RESET);

	private char c;
	private RGBColor hex;
	private net.md_5.bungee.api.ChatColor color;

	ChatColor(char c, net.md_5.bungee.api.ChatColor color) {
		this.c = c;
		this.color = color;
	}

	public char getChar() {
		return c;
	}

	public RGBColor getHex() {
		if (this.hex == null) {
			switch (this) {
			case AQUA:
				this.hex = new RGBColor(5636095);
				break;
			case BLACK:
				this.hex = new RGBColor(0);
				break;
			case BLUE:
				this.hex = new RGBColor(5592575);
				break;
			case DARK_AQUA:
				this.hex = new RGBColor(43690);
				break;
			case DARK_BLUE:
				this.hex = new RGBColor(170);
				break;
			case DARK_GRAY:
				this.hex = new RGBColor(5592405);
				break;
			case DARK_GREEN:
				this.hex = new RGBColor(43520);
				break;
			case DARK_PURPLE:
				this.hex = new RGBColor(11141290);
				break;
			case DARK_RED:
				this.hex = new RGBColor(11141120);
				break;
			case GOLD:
				this.hex = new RGBColor(16755200);
				break;
			case GRAY:
				this.hex = new RGBColor(11184810);
				break;
			case GREEN:
				this.hex = new RGBColor(5635925);
				break;
			case LIGHT_PURPLE:
				this.hex = new RGBColor(16733695);
				break;
			case RED:
				this.hex = new RGBColor(16733525);
				break;
			case WHITE:
				this.hex = new RGBColor(16777215);
				break;
			case YELLOW:
				this.hex = new RGBColor(16777045);
				break;
			default:
				return null;
			}
		}
		return this.hex;
	}

	public net.md_5.bungee.api.ChatColor getColor() {
		return color;
	}

	public static ChatColor getByChar(char c) {
		for (ChatColor color : values())
			if (color.getChar() == c)
				return color;
		return ChatColor.RESET;
	}

	public static ChatColor getByName(String name) {
		for (ChatColor color : values())
			if (color.name().equalsIgnoreCase(name))
				return color;
		return ChatColor.RESET;
	}

	private static Boolean rgb = null;
	private static Method of = null;

	public static boolean rgbIsAvailable() {
		if (rgb == null) {
			try {
				of = net.md_5.bungee.api.ChatColor.class.getMethod("of", String.class);
				rgb = true;
			} catch (Exception e) {
				rgb = false;
			}
		}
		return rgb.booleanValue();
	}

	public static net.md_5.bungee.api.ChatColor rgbToColor(String color) {
		color.toUpperCase();
		if (rgbIsAvailable())
			try {
				return (net.md_5.bungee.api.ChatColor) of.invoke(null, color);
			} catch (Exception e) {
				e.printStackTrace();
			}
		RGBColor rgbColor = new RGBColor(color.substring(1));
		int score = -1;
		ChatColor chatColor = null;
		int tempScore;
		for (ChatColor checkChatColor : ChatColor.values()) {
			if (checkChatColor.getHex() == null) {
				continue;
			}

			tempScore = Math.abs((checkChatColor.getHex().getRed() - rgbColor.getRed())
					+ (checkChatColor.getHex().getGreen() - rgbColor.getGreen())
					+ (checkChatColor.getHex().getBlue() - rgbColor.getBlue()));
			if (chatColor == null || tempScore < score) {
				score = tempScore;
				chatColor = checkChatColor;
			}
		}
		return (chatColor != null ? chatColor : ChatColor.RESET).getColor();
	}

}
