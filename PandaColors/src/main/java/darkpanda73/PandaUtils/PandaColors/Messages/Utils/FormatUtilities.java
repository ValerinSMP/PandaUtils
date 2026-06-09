package darkpanda73.PandaUtils.PandaColors.Messages.Utils;

import java.util.ArrayList;
import java.util.List;

import darkpanda73.PandaUtils.PandaColors.Enums.ChatColor;

public class FormatUtilities {

	public static String round(double number, int decimals) {
		String s = String.format("%." + decimals + "f", new Object[] { Double.valueOf(number) }).replace(',', '.');
		if (s.startsWith("-0") && s.endsWith("00"))
			s = s.replace("-", "");
		return s;
	}

	public static String generateBar(double d, int length, ChatColor chatcolor) {
		return generateBar(d, length, chatcolor, ChatColor.GRAY);
	}

	public static String generateBar(double d, int length, ChatColor chatcolor, ChatColor backgroundColor) {
		String text = "&8[&" + chatcolor.getChar();
		int b = (int) (d * (length));
		boolean color = true;
		for (int i = 0; i < length; i++) {
			if (color && i >= b) {
				text = String.valueOf(text) + "&" + backgroundColor.getChar();
				color = false;
			}
			text = String.valueOf(text) + "|";
		}
		text = String.valueOf(text) + "&8]";
		return text;
	}

	public static String[] splitByLines(String str, int maxperline) {
		List<String> list = new ArrayList<>();
		str = str.trim();
		while (str.length() > 0) {
			if (str.length() < maxperline + 1) {
				list.add(str.substring(0, str.length()));
				break;
			}
			int lastspace = str.lastIndexOf(' ', maxperline);
			int nextspace = str.indexOf(' ', maxperline);
			if (lastspace == -1 || nextspace == maxperline)
				list.add(str.substring(0, maxperline));
			else
				list.add(str.substring(0, lastspace));
			str = str.substring(lastspace == -1 || nextspace == maxperline ? maxperline : lastspace + 1).trim();
		}
		return list.toArray(new String[list.size()]);
	}

}
