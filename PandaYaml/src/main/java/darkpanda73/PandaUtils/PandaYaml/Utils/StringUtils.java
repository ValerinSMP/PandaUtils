package darkpanda73.PandaUtils.PandaYaml.Utils;

public class StringUtils {

	public static String repeat(char character, int numberOfRepeats) {
		char[] string = new char[numberOfRepeats];
		for (int i = 0; i < numberOfRepeats; string[i++] = character)
			;
		return new String(string);
	}

}
