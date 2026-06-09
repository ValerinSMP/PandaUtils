package darkpanda73.PandaUtils.Services.PandaFilesModule;

import java.lang.reflect.Modifier;
import java.util.HashMap;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import darkpanda73.PandaUtils.PandaPlugin.Utils.InjectUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.JavaUtils;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaFieldContainer;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFile.Result;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Utilities.Java.Fields.FieldUtilities;

@PandaService(loadOn = LoadStep.LOAD, priority = -998)
public abstract class PandaFilesService {

	@PandaInject
	private PandaPluginClass plugin;

	private HashMap<String, PandaFolder> folders = new HashMap<>();
	private HashMap<String, PandaFile> files = new HashMap<>();

	@PostInjectMethod
	protected void inject() {
		InjectUtils.inject(PandaPrefixedStringField.class, plugin);
	}

	@LoadMethod
	protected void load(ReportResult methodResult) throws Throwable {
		try {
			JavaUtils.getClasses().forEach(clazz -> {
				try {
					registerFields(clazz);
				} catch (Throwable e) {
				}
			});
		} catch (Throwable e) {
			methodResult.addErrors(new ReportError(
					String.format("Unable to load registered fields from other classes: %s", e.getMessage()), false,
					e));
		}

		reload(methodResult);
	}

	@ReloadMethod
	protected void reload(ReportResult methodResult) throws Throwable {
		Result result = null;

		for (PandaFolder folder : folders.values()) {
			result = folder.create();

			if (result == Result.BACKUP) {
				methodResult.addErrors(new ReportError(
						String.format("There were issues creating or updating folder '%s'", folder.getFile().getPath()),
						false, null));
			} else if (result == Result.FAILED) {
				throw new ReportError(
						String.format("Unable to create or update folder '%s'", folder.getFile().getPath()));
			}
		}

		for (PandaFile file : files.values()) {
			result = file.create();

			if (result == Result.BACKUP) {
				methodResult.addErrors(new ReportError(
						String.format("There were issues creating or updating filter '%s'", file.getFile().getPath()),
						false, null));
			} else if (result == Result.FAILED) {
				methodResult.addErrors(new ReportError(
						String.format("Unable to create or update file '%s'", file.getFile().getPath())));
			}
		}
	}

	public void addFolder(PandaFolder folder) {
		this.folders.put(folder.getId(), folder);
	}

	public void addFile(PandaFile file) {
		this.files.put(file.getId(), file);
	}

	public PandaFolder getFolder(String id) {
		return folders.get(id);
	}

	public PandaFile getFile(String id) {
		return files.get(id);
	}

	public PandaYamlFile getYaml(String id) {
		PandaFile file = getFile(id);
		if (file != null && file instanceof PandaYamlFile) {
			return (PandaYamlFile) file;
		}
		return null;
	}

	public void registerFields(Class<?> clazz) {
		if (clazz.isAnnotationPresent(RegisteredPandaFieldContainer.class)) {
			String fileId = clazz.getAnnotation(RegisteredPandaFieldContainer.class).value();
			PandaYamlFile yaml = getYaml(fileId);
			if (yaml != null) {
				FieldUtilities.findByType(clazz, PandaField.class).stream()
						.filter(field -> Modifier.isStatic(field.getModifiers())).forEach(field -> {
							try {
								yaml.registerFields((PandaField<?>) FieldUtilities.getValue(clazz, field));
							} catch (IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						});
			}
		} else {
			FieldUtilities.findByAnnotation(clazz, RegisteredPandaField.class).stream()
					.filter(field -> Modifier.isStatic(field.getModifiers())).forEach(field -> {
						String fileId = field.getAnnotation(RegisteredPandaField.class).value();
						PandaYamlFile yaml = getYaml(fileId);
						if (yaml != null) {
							try {
								yaml.registerFields((PandaField<?>) FieldUtilities.getValue(clazz, field));
							} catch (IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
		}
	}

}
