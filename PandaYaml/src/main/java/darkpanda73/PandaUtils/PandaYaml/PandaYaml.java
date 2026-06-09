package darkpanda73.PandaUtils.PandaYaml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Utils.YamlUtils;
import lombok.Getter;

public class PandaYaml {

	private @Getter long requiredTimeToLoad;
	private @Getter long requiredTimeToSave;

	private @Getter YamlSection root = new YamlSection("root");

	public PandaYaml() {
		reset();
	}

	public PandaYaml(File file) throws YamlException, FileNotFoundException, IOException {
		loadYAML(file);
	}

	public PandaYaml(InputStream input) throws YamlException, IOException {
		loadYAML(input);
	}

	public PandaYaml(HashMap<String, Object> data) {
		loadYAML(data);
	}

	public void reset() {
		requiredTimeToLoad = requiredTimeToSave = 0L;
		root.clear();
	}

	public void loadYAML(File file) throws YamlException, FileNotFoundException, IOException {
		if (file.exists()) {
			try (FileInputStream input = new FileInputStream(file)) {
				loadYAML(input);
			}
		}
	}

	public void loadYAML(InputStream input) throws YamlException, IOException {
		reset();
		long start = System.currentTimeMillis();
		try (InputStream inputStream = input) {
			if (input != null) {
				try (InputStreamReader inputStreamReader = new InputStreamReader(input, StandardCharsets.UTF_8);) {
					try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
						try {
							this.root = YamlUtils.readYamlObjects(bufferedReader);
						} catch (YamlException e) {
							reset();
							throw e;
						}
					}
				}
			}
		}
		requiredTimeToLoad = System.currentTimeMillis() - start;
	}

	public void loadYAML(HashMap<String, Object> data) {
		reset();
		long start = System.currentTimeMillis();
		data.forEach((key, value) -> getRoot().set(key, value));
		requiredTimeToLoad = System.currentTimeMillis() - start;
	}

	public void saveYAML(File file) throws IOException {
		long start = System.currentTimeMillis();

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}

		try (FileOutputStream output = new FileOutputStream(file)) {
			try (OutputStreamWriter outputWriter = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
				outputWriter.write(toString());
				outputWriter.close();
			}
		}
		requiredTimeToSave = System.currentTimeMillis() - start;
	}

	public PandaYaml cloneYaml() {
		try (ByteArrayInputStream input = new ByteArrayInputStream(toString().getBytes())) {
			return new PandaYaml(input);
		} catch (YamlException | IOException e) {
		}
		return new PandaYaml();
	}

	@Override
	public String toString() {
		return root.getChilds().stream().map(child -> child.toString())
				.collect(Collectors.joining(System.lineSeparator()));
	}
}