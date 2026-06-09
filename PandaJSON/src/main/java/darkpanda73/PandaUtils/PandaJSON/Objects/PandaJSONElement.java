package darkpanda73.PandaUtils.PandaJSON.Objects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class PandaJSONElement {

	public enum JSONDataType {
		STRING, LONG, DOUBLE, BOOLEAN, NULL
	}

	public boolean isArray() {
		return false;
	}

	public boolean isObject() {
		return false;
	}

	public boolean isData() {
		return false;
	}

	public PandaJSONArray asArray() {
		throw new IllegalStateException("Element is not an array");
	}

	public PandaJSONObject asObject() {
		throw new IllegalStateException("Element is not an object");
	}

	public PandaJSONData asData() {
		throw new IllegalStateException("Element is not a data");
	}

	@Override
	public String toString() {
		return toString(0);
	}

	public void save(File file) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(toString());
			writer.flush();
		}
	}

	public abstract String toString(int level);

}