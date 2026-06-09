package darkpanda73.PandaUtils.PandaYaml.Utils;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;

public class YamlUtils {

	private static final String NAMED_OBJECT_START_PATTERN = "^[0-9a-zA-Z\\-_\\.]*(:\\s?)";
	private static final String DECIMAL_PATTERN = "^[-+]?[0-9]*\\.[0-9]+([eE][-+]?[0-9]+)?$";
	private static final String LONG_PATTERN = "^[-+]?[0-9]*$";
	private static final String BOOLEAN_LOWERCASE_PATTERN = "(true|false)";

	public static YamlSection readYamlObjects(BufferedReader reader) throws YamlException {
		final YamlSection yamlSection = new YamlSection("root");
		final List<YamlSection> sectionTree = new ArrayList<>();
		final List<String> lastLines = new ArrayList<>();

		final Supplier<YamlSection> getSection = () -> sectionTree.size() > 0 ? sectionTree.get(sectionTree.size() - 1)
				: yamlSection;

		final BiConsumer<String, List<String>> newListMethod = (sectionName, listValue) -> {
			getSection.get().set(sectionName, listValue);
		};

		final Consumer<String> newSectionMethod = (sectionName) -> {
			YamlSection section = new YamlSection(getSection.get(), sectionName);

			getSection.get().getChilds().add(section);

			sectionTree.add(section);
		};

		final AtomicReference<String> name = new AtomicReference<>();
		final AtomicReference<List<String>> list = new AtomicReference<>();
		final AtomicReference<String> completeLine = new AtomicReference<>();
		final AtomicReference<Consumer<String>> onCompleteLine = new AtomicReference<>();

		try {
			reader.lines().forEach(line -> {
				// List used to print the lines managed to inform about the section of the
				// error.
				if (lastLines.size() > 5) {
					lastLines.remove(0);
				}

				lastLines.add(line);

				// If completeLine is not null, means it's actually trying to search for a
				// complete line for a section.
				if (completeLine.get() != null) {
					// First we remove the leading spaces.
					int i = 0;
					for (; i < line.length() && line.charAt(i) == ' '; i++) {
					}

					// To remove it, we get the quantity of spaces and we do a substring from the
					// first available character.
					String trim = line.substring(i);

					// If trim is not empty, means there's content, but if it's empty, this means
					// that it's a space, which we include on the completeLine instead of the
					// section.
					if (!trim.isEmpty()) {
						// First we check if this line has the delimiter used for the current
						// completeLine.
						int indexOf = trim.lastIndexOf(completeLine.get().charAt(0), trim.length() - 1);

						// We check if the index of the character is not -1, and if it's equal to 0,
						// meaning the first char is the end of the complete line, or if the previous
						// char is not a escape char. Otherwise, the line is included on the complete
						// line to keep getting the complete line.
						if (indexOf != -1 && (indexOf == 0 || trim.charAt(indexOf - 1) != '\\')) {
							// We check that the line is empty after the delimiter. If not, we throw a new
							// exception telling about the bad format. This is not really an issue, but to
							// ensure a good format on the files, better to advise.
							if (trim.substring(indexOf + 1).trim().isEmpty()) {
								// If everything is okey, then we execute the onCompleteLine consumer, to do the
								// proper actions. Then we set to null the name, completeLine and onCompleteLine
								// references.
								completeLine.getAndUpdate(
										string -> string.concat(" ").concat(trim.substring(0, indexOf + 1)));

								completeLine
										.getAndUpdate(string -> string.replaceAll("\\\\" + completeLine.get().charAt(0),
												String.valueOf(completeLine.get().charAt(0))));

								onCompleteLine.get()
										.accept(completeLine.get().substring(1, completeLine.get().length() - 1));

								completeLine.set(null);
								onCompleteLine.set(null);
							} else {
								throw new RuntimeException(String.format(
										"Trying to add more text after finalizing the line with the proper delimiter:%s%s",
										System.lineSeparator(),
										lastLines.stream().collect(Collectors.joining(System.lineSeparator(),
												System.lineSeparator(), System.lineSeparator()))));
							}
						} else {
							completeLine.getAndUpdate((string) -> string.concat(" ").concat(trim));
						}
					} else {
						completeLine.getAndUpdate((string) -> string.concat(" "));
					}
					return;
				}

				// If line is blank, means there must be a space to be added on the current
				// section, which is the last added section or the root section.
				if (line.trim().isEmpty()) {
					if (name.get() != null) {
						if (list.get() != null) {
							newListMethod.accept(name.getAndSet(null), list.getAndSet(null));
						} else {
							newSectionMethod.accept(name.getAndSet(null));
						}
					}

					getSection.get().space();
				} else {
					int i = 0;
					for (; i < line.length() && line.charAt(i) == ' '; i++) {
					}

					int currentHeight = i / 2;

					String content = line.substring(i);

					if (currentHeight > sectionTree.size()) {
						if (currentHeight > sectionTree.size() + 1 || name.get() == null || list.get() != null) {
							throw new RuntimeException(
									String.format("Trying to initialize a new section on a non-authorized height:%s%s",
											System.lineSeparator(),
											lastLines.stream().collect(Collectors.joining(System.lineSeparator(),
													System.lineSeparator(), System.lineSeparator()))));
						} else if (name.get() != null) {
							newSectionMethod.accept(name.getAndSet(null));
						}
					}

					if (currentHeight < sectionTree.size()) {
						if (name.get() != null) {
							if (list.get() != null) {
								newListMethod.accept(name.getAndSet(null), list.getAndSet(null));
							} else {
								newSectionMethod.accept(name.getAndSet(null));
							}
						}

						while (currentHeight < sectionTree.size()) {
							sectionTree.remove(sectionTree.size() - 1);
						}
					}

					Pattern pattern = Pattern.compile(NAMED_OBJECT_START_PATTERN);
					Matcher matcher = pattern.matcher(content);

					if (matcher.find()) {
						if (name.get() != null) {
							if (list.get() != null) {
								newListMethod.accept(name.getAndSet(null), list.getAndSet(null));
							} else {
								newSectionMethod.accept(name.getAndSet(null));

								while (currentHeight < sectionTree.size()) {
									sectionTree.remove(sectionTree.size() - 1);
								}
							}
						}

						String[] data = content.split(matcher.group(1), 2);

						if (data.length > 1 && !data[1].trim().isEmpty()) {
							if (data[1].charAt(0) == ' ') {
								data[1] = data[1].substring(1);
							}

							if (data[1].charAt(0) == '"' || data[1].charAt(0) == '\'') {
								int indexOf = data[1].lastIndexOf(data[1].charAt(0), data[1].length());

								if (indexOf > 0 && data[1].charAt(indexOf - 1) != '\\') {
									if (data[1].substring(indexOf + 1).trim().isEmpty()) {
										data[1] = data[1].replaceAll("\\\\" + data[1].charAt(0),
												String.valueOf(data[1].charAt(0)));

										getSection.get().set(data[0], data[1].substring(1, data[1].length() - 1));
									} else {
										throw new RuntimeException(String.format(
												"Trying to add more text after finalizing the line with the proper delimiter:%s%s",
												System.lineSeparator(),
												lastLines.stream().collect(Collectors.joining(System.lineSeparator(),
														System.lineSeparator(), System.lineSeparator()))));
									}
								} else {
									name.set(data[0]);
									completeLine.set(data[1]);
									onCompleteLine
											.set((finalLine) -> getSection.get().set(name.getAndSet(null), finalLine));
								}
							} else {
								if (data[1].trim().equals("null")) {
									getSection.get().set(data[0], null);
								} else if (data[1].trim().equals("[]")) {
									getSection.get().set(data[0], new ArrayList<String>());
								} else if (data[1].matches(DECIMAL_PATTERN)) {
									getSection.get().set(data[0], Double.parseDouble(data[1]));
								} else if (data[1].matches(LONG_PATTERN)) {
									getSection.get().set(data[0], Long.parseLong(data[1]));
								} else if (data[1].toLowerCase().matches(BOOLEAN_LOWERCASE_PATTERN)) {
									getSection.get().set(data[0], Boolean.parseBoolean(data[1]));
								} else {
									getSection.get().set(data[0], data[1]);
								}
							}
						} else {
							name.set(data[0]);
						}
					} else if (content.startsWith("#")) {
						if (name.get() != null) {
							if (list.get() != null) {
								newListMethod.accept(name.getAndSet(null), list.getAndSet(null));
							} else {
								newSectionMethod.accept(name.getAndSet(null));
								sectionTree.remove(sectionTree.size() - 1);
							}
						}

						getSection.get().comment(content.substring(1));
					} else if (content.startsWith("- ")) {
						if (name.get() != null) {
							if (list.get() == null) {
								list.set(new ArrayList<>());
							}
						} else {
							throw new RuntimeException(
									"Trying to set a list where there's no section for a list currently");
						}

						content = content.substring(2);

						if (!content.isEmpty() && (content.charAt(0) == '"' || content.charAt(0) == '\'')) {
							int indexOf = content.lastIndexOf(content.charAt(0), content.length());

							if (indexOf > 0 && content.charAt(indexOf - 1) != '\\') {
								if (content.substring(indexOf + 1).trim().isEmpty()) {
									content = content.replaceAll("\\\\" + content.charAt(0),
											String.valueOf(content.charAt(0)));

									list.get().add(content.substring(1, content.length() - 1));
								} else {
									throw new RuntimeException(String.format(
											"Trying to add more text after finalizing the line with the proper delimiter:%s%s",
											System.lineSeparator(),
											lastLines.stream().collect(Collectors.joining(System.lineSeparator(),
													System.lineSeparator(), System.lineSeparator()))));
								}
							} else {
								completeLine.set(content);
								onCompleteLine.set((finalLine) -> list.get().add(finalLine));
							}
						} else {
							list.get().add(content);
						}
					} else {
						throw new RuntimeException(
								String.format("It wasn't possible to determine which kind of object is '%s'", line));
					}
				}
			});

			if (name.get() != null) {
				if (list.get() != null) {
					newListMethod.accept(name.getAndSet(null), list.getAndSet(null));
				} else {
					newSectionMethod.accept(name.getAndSet(null));
				}
			}

			return yamlSection;
		} catch (RuntimeException e) {
			throw new YamlException("Error trying to read a YAML file", e);
		}
	}

}
