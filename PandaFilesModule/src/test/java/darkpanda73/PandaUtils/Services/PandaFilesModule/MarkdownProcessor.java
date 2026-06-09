package darkpanda73.PandaUtils.Services.PandaFilesModule;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlNamedObject;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.PandaFieldDescription;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaFieldContainer;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaField;
import darkpanda73.PandaUtils.Utilities.Java.Fields.FieldUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import darkpanda73.PandaUtils.Utilities.Java.String.StringUtilities;

public abstract class MarkdownProcessor {

	public void generate() {
		String packageName = getClass().getPackage().getName();

		Reflections reflections = new Reflections(
				new ConfigurationBuilder().forPackage(packageName, ClassLoader.getSystemClassLoader())
						.filterInputsBy(new FilterBuilder().includePackage(packageName))
						.addScanners(Scanners.SubTypes.filterResultsBy(s -> true)));

		List<Class<?>> classes = reflections.getAll(Scanners.SubTypes).stream()
				.filter(className -> className.startsWith(packageName)).map(className -> {
					try {
						Class<?> clazz = Class.forName(className);
						if (!clazz.isMemberClass() && !clazz.isLocalClass() && !clazz.isAnonymousClass()) {
							return clazz;
						}
					} catch (Throwable e) {
					}
					return null;
				}).filter(Objects::nonNull).collect(Collectors.toList());

		HashMap<String, HashMap<String, Pair<String[], Object>>> fields = new HashMap<>();

		classes.stream().map(this::getFields).forEach(
				list -> list.forEach(pair -> fields.computeIfAbsent(pair.getFirst(), (key) -> new HashMap<>()).put(
						pair.getSecond().getSecond().getPath().toLowerCase(),
						Pair.of(pair.getSecond().getFirst(), pair.getSecond().getSecond().getDefaultContent()))));

		fields.forEach((key, map) -> {
			if (key.equals("config")) {
				key = "Config";
			} else {
				key = "Lang";
			}

			PandaYaml yaml;
			try {
				yaml = new PandaYaml(new File("./src/main/java/company/pluginName/Resources/" + key + ".yml"));

				System.out.println("# " + key);
				System.out.println();

				yaml.getRoot().getNamedChilds().forEach(child -> printChild(map, child,
						map.getOrDefault(child.toPath().toLowerCase(), Pair.of(null, null)).getFirst(), 2));
			} catch (YamlException | IOException e) {
				e.printStackTrace();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void printChild(HashMap<String, Pair<String[], Object>> map, YamlNamedObject obj, String[] description,
			int level) {
		System.out.println(StringUtilities.repeat('#', level) + " " + obj.getName());

		if (obj.isYamlSection()) {
			obj.asYamlSection().getNamedChilds().forEach(child -> printChild(map, child,
					map.getOrDefault(child.toPath().toLowerCase(), Pair.of(null, null)).getFirst(), level + 1));
		} else {
			String[] split = obj.toPath().split("\\.");

			for (int i = 0; i < split.length - 1; i++) {
				split[i] = StringUtilities.repeat(" ", i * 2) + split[i] + System.lineSeparator();
			}

			split = Arrays.copyOfRange(split, 0, split.length - 1);

			if (map.containsKey(obj.toPath().toLowerCase())) {
				((YamlData<Object>) obj.asYamlData()).setData(map.get(obj.toPath().toLowerCase()).getSecond());
			}

			if (description != null) {
				System.out.println();
				Arrays.stream(description).forEach(line -> System.out.println(line + "\r\n"));
			}

			System.out.println();
			System.out.println("```" + System.lineSeparator() + Arrays.stream(split).collect(Collectors.joining())
					+ obj.toString(level - 2) + System.lineSeparator() + "```");
		}
	}

	@SuppressWarnings("unchecked")
	private List<Pair<String, Pair<String[], PandaField<Object>>>> getFields(Class<?> clazz) {
		if (clazz.isAnnotationPresent(RegisteredPandaFieldContainer.class)) {
			String fileId = clazz.getAnnotation(RegisteredPandaFieldContainer.class).value();
			return FieldUtilities.findByType(clazz, PandaField.class).stream().map(field -> {
				try {
					PandaFieldDescription description = field.getAnnotation(PandaFieldDescription.class);
					return description != null ? Pair.of(fileId, Pair.of(
							description.list().length > 0 ? description.list()
									: (!description.value().isEmpty() ? new String[] { description.value() }
											: new String[0]),
							(PandaField<Object>) FieldUtilities.getValue(clazz, field))) : null;
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.toList());
		} else {
			return FieldUtilities.findByAnnotation(clazz, RegisteredPandaField.class).stream().map(field -> {
				String fileId = field.getAnnotation(RegisteredPandaField.class).value();
				try {
					PandaFieldDescription description = field.getAnnotation(PandaFieldDescription.class);
					return description != null ? Pair.of(fileId, Pair.of(
							description.list().length > 0 ? description.list()
									: (!description.value().isEmpty() ? new String[] { description.value() }
											: new String[0]),
							(PandaField<Object>) FieldUtilities.getValue(clazz, field))) : null;
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.toList());
		}
	}

}
