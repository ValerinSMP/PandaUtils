package darkpanda73.PandaUtils.PandaColors.Messages.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import darkpanda73.PandaUtils.PandaColors.Enums.ChatColor;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.ColorMode;
import darkpanda73.PandaUtils.PandaColors.Messages.Utils.Objects.RGBColor;
import darkpanda73.PandaUtils.PandaColors.Messages.Utils.Objects.RGBGradient;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;

public class RGBUtilities {

	public static final Pattern HEX_PATTERN = Pattern.compile("&(#[0-9a-fA-F]{6})");
	public static final Pattern GRADIENT_PATTERN = Pattern
			.compile("<((([klmno]|(w[0-9]+))*)|(\\/))?((#[0-9a-fA-F]{6})*)>");

	public static void removeHexPatterns(List<MessageFragment> fragments) {
		fragments.forEach(fragment -> removeByPattern(HEX_PATTERN, fragment));
	}

	public static void removeGradientPatterns(List<MessageFragment> fragments) {
		fragments.forEach(fragment -> removeByPattern(GRADIENT_PATTERN, fragment));
	}

	private static void removeByPattern(Pattern pattern, MessageFragment fragment) {
		Matcher matcher = pattern.matcher(fragment.getText());
		while (matcher.find()) {
			fragment.setText(
					fragment.getText().substring(0, matcher.start()) + fragment.getText().substring(matcher.end()));
			matcher.reset(fragment.getText());
		}
	}

	public static void replaceHexPattern(List<MessageFragment> fragments) {
		fragments.forEach(fragment -> {
			if (fragment.getColorMode() == ColorMode.IGNORE) {
				return;
			} else if (fragment.getColorMode() == ColorMode.STRIP) {
				removeByPattern(HEX_PATTERN, fragment);
				return;
			}

			Matcher matcher = HEX_PATTERN.matcher(fragment.getText());
			while (matcher.find()) {
				fragment.setText(fragment.getText().substring(0, matcher.start())
						+ ChatColor.rgbToColor(matcher.group(1)) + fragment.getText().substring(matcher.end()));
				matcher.reset(fragment.getText());
			}
		});
	}

	public static void replaceGradientPattern(List<MessageFragment> fragments) {
		if (ChatColor.rgbIsAvailable()) {
			for (int i = 0; i < fragments.size(); i++) {
				MessageFragment fragment = fragments.get(i);

				if (fragment.getColorMode() == ColorMode.IGNORE) {
					continue;
				} else if (fragment.getColorMode() == ColorMode.STRIP) {
					removeByPattern(GRADIENT_PATTERN, fragment);
					continue;
				}

				Matcher matcher = GRADIENT_PATTERN.matcher(fragment.getText());
				int currentIndex = 0;
				while (currentIndex < fragment.getText().length() && matcher.find(currentIndex)) {
					if (matcher.group(5) == null) {
						RGBGradient currentGradient = new RGBGradient();

						String gradients = matcher.group(6);
						if (gradients != null && !gradients.isEmpty()) {
							Arrays.stream(gradients.substring(1).split("#"))
									.forEach(gradient -> currentGradient.getColors().add(new RGBColor(gradient)));
						}

						char[] modifiers = matcher.group(2).toCharArray();
						if (modifiers != null && modifiers.length > 0) {
							for (int j = 0; j < modifiers.length; j++) {
								char modifier = Character.toLowerCase(modifiers[j]);
								switch (modifier) {
								case 'k':
									currentGradient.setObfuscated(true);
									break;
								case 'l':
									currentGradient.setBold(true);
									break;
								case 'm':
									currentGradient.setStrikethrough(true);
									break;
								case 'n':
									currentGradient.setUnderline(true);
									break;
								case 'o':
									currentGradient.setItalic(true);
									break;
								case 'w':
									int startIndex = ++j;

									while (j < modifiers.length && modifiers[j] >= '1' && modifiers[j] <= '9') {
										j++;
									}

									if (startIndex != j) {
										currentGradient.setWaveLength(Integer
												.parseInt(new String(Arrays.copyOfRange(modifiers, startIndex, j))));
									}

									j--;
									break;
								}
							}
						}

						int j = i;
						for (; j < fragments.size(); j++) {
							MessageFragment fragmentEnd = fragments.get(j);
							Matcher matcherEnd = GRADIENT_PATTERN.matcher(fragmentEnd.getText());

							int currentIndexEnd = j == i ? matcher.end() : 0;
							if (matcherEnd.find(currentIndexEnd)) {
								if (matcherEnd.group(5) != null) {
									int startCharIndex = matcher.start();
									int endCharIndex = matcherEnd.start();

									fragment.setText(fragment.getText().substring(0, startCharIndex).concat(
											fragment.getText().substring(startCharIndex + matcher.group().length())));

									if (fragment == fragmentEnd) {
										endCharIndex -= matcher.group().length();
									}

									fragmentEnd
											.setText(fragmentEnd.getText().substring(0, endCharIndex).concat(fragmentEnd
													.getText().substring(endCharIndex + matcherEnd.group().length())));

									String gradientsEnd = matcherEnd.group(6);
									if (gradientsEnd != null && !gradientsEnd.isEmpty()) {
										Arrays.stream(gradientsEnd.substring(1).split("#")).forEach(
												gradient -> currentGradient.getColors().add(new RGBColor(gradient)));
									}

									Pair<Integer, MessageFragment> startGroup = Pair.of(startCharIndex, fragment);
									Pair<Integer, MessageFragment> endGroup = Pair.of(endCharIndex, fragmentEnd);

									int endTextLength = fragmentEnd.getText().length();

									currentGradient.applyGradient(fragments, startGroup, endGroup);

									i = j;
									currentIndex = endCharIndex + (fragmentEnd.getText().length() - endTextLength);
								} else {
									if (i == j) {
										currentIndex = matcherEnd.start() - (matcher.end() - matcher.start());
										fragment.setText(fragment.getText().substring(0, matcher.start())
												.concat(fragment.getText().substring(matcher.end())));
										matcher.reset(fragment.getText());
									} else {
										currentIndex = matcherEnd.start();
										fragment.setText(fragment.getText().substring(0, matcher.start())
												.concat(fragment.getText().substring(matcher.end())));
									}
									i = j;
								}

								fragment = fragments.get(i);
								matcher.reset(fragment.getText());
								break;
							}
						}

						if (j == fragments.size()) {
							fragment.setText(fragment.getText().substring(0, matcher.start())
									.concat(fragment.getText().substring(matcher.end())));
							return;
						}
					} else {
						fragment.setText(fragment.getText().substring(0, matcher.start())
								.concat(fragment.getText().substring(matcher.end())));
						currentIndex = matcher.start();
						matcher.reset(fragment.getText());
					}
				}
			}
		} else {
			fragments.forEach(fragment -> removeByPattern(GRADIENT_PATTERN, fragment));
		}
	}

}
