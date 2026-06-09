package darkpanda73.PandaUtils.PandaYaml.Exceptions;

public class YamlException extends Exception {

	private static final long serialVersionUID = -3946211760738198961L;

	public YamlException(String message) {
		super(message);
	}

	public YamlException(String message, Exception exception) {
		super(message, exception);
	}

}
