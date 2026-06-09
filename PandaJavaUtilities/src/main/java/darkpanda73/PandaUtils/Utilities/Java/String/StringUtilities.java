package darkpanda73.PandaUtils.Utilities.Java.String;

import java.math.BigDecimal;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringUtilities {

	public static String addSpaces(int spaces, String text) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < spaces; i++)
			sb.append(" ");
		sb.append(text);
		return sb.toString();
	}

	public static String removeBoundaries(String text, char boundaryChar) {
		if (text.length() > 1) {
			char c = text.charAt(0);
			if (c == boundaryChar && c == text.charAt(text.length() - 1)) {
				return text.substring(1, text.length() - 1);
			}
		}
		return text;
	}

	public static String removeLeadingChar(String s, char c) {
		int index;
		for (index = 0; index < s.length(); index++) {
			if (s.charAt(index) != c) {
				break;
			}
		}
		return s.substring(index);
	}

	public static String removeTrailingChar(String s, char c) {
		int index;
		for (index = s.length(); index > 0; index--) {
			if (s.charAt(index - 1) != c) {
				break;
			}
		}
		return s.substring(0, index);
	}

	public static String removeLeadingTrailingChar(String s, char c) {
		return removeTrailingChar(removeLeadingChar(s, c), c);
	}

	public static String toPercentage(float n) {
		return String.format("%.0f", n * 100) + "%";
	}

	public static String toPercentage(float n, int digits) {
		return String.format("%." + digits + "f", n * 100) + "%";
	}

	public static String toDecimal(double n, int decimals) {
		return String.format("%." + decimals + "f", n);
	}

	public static String toCurrency(double n) {
		return String.format("%,.2f", n);
	}

	public static String toCurrency(BigDecimal n) {
		return String.format("%,.2f", n);
	}

	public static String bytesToBinaryString(long bytes) {
		long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
		if (absB < 1024) {
			return bytes + " B";
		}
		long value = absB;
		CharacterIterator ci = new StringCharacterIterator("KMGTPE");
		for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
			value >>= 10;
			ci.next();
		}
		value *= Long.signum(bytes);
		return String.format("%.1f %ciB", value / 1024.0, ci.current());
	}

	public static String repeat(char character, int repeat) {
		return repeat(String.valueOf(character), repeat);
	}

	public static String repeat(String text, int repeat) {
		return new String(new char[repeat]).replace("\0", text);
	}

	public static String repeat(String text, int repeat, String delimiter) {
		return IntStream.range(0, repeat).mapToObj(i -> text).collect(Collectors.joining(delimiter));
	}

}
