package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaPlugin.Utils.JavaUtils;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaField;
import lombok.Getter;

public class PandaYamlFile extends PandaFile {

	private @Getter PandaYaml defaultYaml;
	private List<PandaField<?>> fields = new LinkedList<>();

	public PandaYamlFile(String id, String path) {
		super(id, path);
	}

	public PandaYamlFile(String id, String path, PandaYaml defaultYaml) throws IOException {
		this(id, path);

		this.defaultYaml = defaultYaml;
	}

	public PandaYaml getYaml() throws FileNotFoundException, YamlException, IOException {
		return new PandaYaml(getFile());
	}

	@Override
	public Result onCreate() {
		if (this.defaultYaml != null) {
			try {
				this.defaultYaml.saveYAML(getFile());
			} catch (IOException e) {
				System.out.println(String.format("Unable to save default data for file '%s':", getFile().getPath()));
				e.printStackTrace();
				return Result.FAILED;
			}
		}

		return Result.SUCCESS;
	}

	@Override
	public Result onFinish() {
		Result result = Result.SUCCESS;

		PandaYaml yaml = null;

		try {
			yaml = new PandaYaml(getFile());
		} catch (IOException | YamlException e) {
			System.out.println(String.format("Unable to load data from file '%s' properly. Moving as a backup file:",
					getFile().getPath()));
			e.printStackTrace();

			try {
				File backupFile = new File(getFile().getPath() + ".backup");

				if (backupFile.exists()) {
					backupFile.delete();
				}

				Files.move(getFile().toPath(), backupFile.toPath());
			} catch (IOException e1) {
				System.out.println(String.format("Unable to move file '%s' as a backup file:", getFile().getPath()));
				e1.printStackTrace();
			}

			try {
				defaultYaml.saveYAML(getFile());

				yaml = new PandaYaml(getFile());
			} catch (IOException | YamlException e1) {
				System.out.println(String.format("Unable to load default data inserted from file '%s' properly:",
						getFile().getPath()));
				e1.printStackTrace();
			}

			result = Result.BACKUP;
		}

		if (yaml != null) {
			yaml = updateFileVersion(yaml);

			result = updateFields(yaml) == Result.SUCCESS ? result : Result.FAILED;
		}

		return result;
	}

	public void registerFields(PandaField<?>... fields) {
		for (PandaField<?> field : fields) {
			if (field != null && field.getPath() != null
					&& this.fields.stream().noneMatch(rField -> rField.getPath().equals(field.getPath()))) {
				this.fields.add(field);
			}
		}
	}

	public void registerFields(List<PandaField<?>> fields) {
		fields.stream().filter(field -> field.getPath() != null).forEach(field -> this.fields.add(field));
	}

	public void registerFieldsFromObject(Object fieldsContainer) {
		List<Object> staticFields = JavaUtils.getFields(fieldsContainer.getClass(), false).stream()
				.filter(field -> PandaField.class.isAssignableFrom(field.getType())).map(field -> {
					try {
						return JavaUtils.getFieldValue(fieldsContainer, field);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());
		registerFields(staticFields.toArray(new PandaField<?>[staticFields.size()]));
	}

	public void registerFieldsFromClass(Class<?> fieldsContainer) {
		List<Object> staticFields = JavaUtils.getFields(fieldsContainer, true).stream()
				.filter(field -> PandaField.class.isAssignableFrom(field.getType())).map(field -> {
					try {
						return JavaUtils.getFieldValue(null, field);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					return null;
				}).collect(Collectors.toList());
		registerFields(staticFields.toArray(new PandaField<?>[staticFields.size()]));
	}

	protected PandaYaml updateFileVersion(PandaYaml yaml) {
		return updateFileVersion(yaml, new HashMap<>());
	}

	protected final PandaYaml updateFileVersion(PandaYaml yaml, HashMap<String, String> fieldMoveMap) {
		if (this.defaultYaml == null) {
			return yaml;
		}

		try {
			int currentVersion = yaml.getRoot().getDataOrDefault("Version", 0).getInteger();
			int defaultVersion = this.defaultYaml.getRoot().getDataOrDefault("Version", 0).getInteger();

			if (currentVersion < defaultVersion) {
				PandaYaml defaultYaml = this.defaultYaml.cloneYaml();

				for (YamlData<?> currentSection : yaml.getRoot().getAllNamedChilds()) {
					if (!currentSection.toPath().equals("Version")) {
						defaultYaml.getRoot().set(currentSection.toPath(), currentSection);
					}
				}

				YamlData<?> currentSection;
				for (Entry<String, String> entry : fieldMoveMap.entrySet()) {
					currentSection = defaultYaml.getRoot().getData(entry.getKey());
					if (currentSection != null) {
						defaultYaml.getRoot().set(entry.getValue(), currentSection.getData());
						defaultYaml.getRoot().remove(entry.getValue());
					}
				}

				return defaultYaml;
			}
		} catch (Exception e) {
			System.out.println(
					String.format("Unable to update file '%s' with new default file content:", getFile().getPath()));
			e.printStackTrace();
		}
		return yaml;
	}

	private Result updateFields(PandaYaml yaml) {
		if (fields.size() > 0) {
			fields.forEach(field -> {
				YamlData<? extends Object> yamlSection = yaml.getRoot().getData(field.getPath());

				if (!field.getPath().equals(field.getOldPath())) {
					YamlData<?> oldYamlSection = yaml.getRoot().getData(field.getOldPath());
					if (oldYamlSection != null) {
						yamlSection = yaml.getRoot().set(field.getPath(), oldYamlSection.getData());
						yaml.getRoot().remove(field.getOldPath());
					}
				}

				if (yamlSection == null && !field.isOptional()) {
					yamlSection = yaml.getRoot().set(field.getPath(), field.getDefaultContent());
				}

				field.setObjectContent(
						yamlSection.getData() != null ? yamlSection.getData() : field.getDefaultContent());
			});

			try {
				yaml.saveYAML(getFile());
			} catch (IOException e) {
				System.out.println(String.format("Unable to save new data for file '%s':", getFile().getPath()));
				e.printStackTrace();
				return Result.FAILED;
			}
		}
		return Result.SUCCESS;
	}

	public void refreshFields() throws FileNotFoundException, YamlException, IOException {
		PandaYaml yaml = new PandaYaml(getFile());
		yaml = updateFileVersion(yaml);
		updateFields(yaml);
	}

}
