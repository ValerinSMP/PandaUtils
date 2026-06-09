package darkpanda73.PandaUtils.PandaColors.Messages.Utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class FragmentUtilities {

	public static List<MessageFragment> textToFragments(String text, Replacement[] replacements) {
		// If there aren't replacements, return a MessageFragment instance returning the
		// same text.
		if (text.length() == 0 || replacements.length == 0) {
			return Arrays.asList(new MessageFragment(() -> text));
		}

		// Creating a list with all the found replacements to lately transform the
		// string into MessageFragment instances.
		List<Entry<Integer, Replacement>> splits = new ArrayList<>();
		// Sort so the largest texts are first, to ensure no collisions with
		// replacements like 'text' and 'texttwo', going 'texttwo' first.
		Arrays.sort(replacements, (replacement1,
				replacement2) -> replacement1.getTextToReplace().compareTo(replacement2.getTextToReplace()) * -1);

		// Find every occurrence of the replacement in the text. If that index is not
		// occupied by another replacement, then add to the list.
		int currentIndex;
		for (Replacement replacement : replacements) {
			currentIndex = 0;
			while ((currentIndex = text.indexOf(replacement.getTextToReplace(), currentIndex)) != -1) {
				int finalCurrentIndex = currentIndex;

				Optional<Entry<Integer, Replacement>> entryFound = splits.stream()
						.filter(entry -> entry.getKey() == finalCurrentIndex).findFirst();

				if (entryFound.isPresent()) {
					currentIndex += entryFound.get().getValue().getTextToReplace().length();
					continue;
				}

				splits.add(new AbstractMap.SimpleEntry<Integer, Replacement>(finalCurrentIndex, replacement));

				currentIndex = finalCurrentIndex + replacement.getTextToReplace().length();
			}
		}

		// If there aren't found replacements on the text, then returns the text
		// directly.
		if (splits.size() == 0) {
			return Arrays.asList(new MessageFragment(() -> text));
		}

		// Sort the list, so it's sorted by indexes, so we create fragments that are
		// already in sequence.
		splits.sort((entry1, entry2) -> entry1.getKey().compareTo(entry2.getKey()));

		// For every split, generate a MessageFragment.
		List<MessageFragment> fragments = new ArrayList<>();
		currentIndex = 0;
		for (Entry<Integer, Replacement> split : splits) {
			int finalCurrentIndex = currentIndex;

			// If the currentIndex value is not the same as the index of the split, means
			// that there's normal text before it.
			if (split.getKey() != currentIndex) {
				fragments.add(new MessageFragment(() -> text.substring(finalCurrentIndex, split.getKey())));
			}

			fragments.add(split.getValue().getMessageFragment() != null ? split.getValue().getMessageFragment().clone()
					: null);
			currentIndex = split.getKey() + split.getValue().getTextToReplace().length();
		}

		// If the final index is not the same as the text length, means that there's
		// still pending text at the end, and must be added as another text
		if (currentIndex != text.length()) {
			int finalCurrentIndex = currentIndex;

			fragments.add(new MessageFragment(() -> text.substring(finalCurrentIndex)));
		}

		return fragments.stream().filter(fragment -> fragment.getText() != null).collect(Collectors.toList());
	}

	public static List<TextComponent> fragmentsToComponents(List<MessageFragment> fragments, boolean colored) {
		List<TextComponent> components = new ArrayList<>();

		// For each fragment, transform it into a TextComponent instance.
		fragments.forEach(fragment -> {
			// Generate a TextComponent that will act as a container, where the extra
			// components are the text of this current fragment. The TextComponent will
			// contain the hover and click events.
			TextComponent textComponent;

			if (fragment.getText() == null || fragment.getText().isEmpty()) {
				textComponent = new TextComponent("");
			} else {
				switch (fragment.getColorMode()) {
				case STRIP:
					textComponent = new TextComponent(ColorUtilities.strip(fragment.getText()));
				case IGNORE:
					textComponent = new TextComponent(fragment.getText());
				default:
					textComponent = new TextComponent(
							TextComponent.fromLegacyText(colored ? ColorUtilities.color(fragment.getText())
									: ColorUtilities.strip(fragment.getText())));
				}
			}

			// If the content is empty, means that there's no text in this component
			if (textComponent.getExtra() == null || textComponent.getExtra().size() == 0) {
				return;
			}

			// TODO: Fix the content of the events once the MessageTemplate is prepared to
			// offer a text with the system.

			// Parses the specified hover event specified on the replacement to a HoverEvent
			// instance from md5.
			textComponent.setHoverEvent(
					fragment.getHoverEvent() != null
							? new HoverEvent(HoverEvent.Action.values()[fragment.getHoverEvent().getAction().ordinal()],
									fragment.getHoverEvent().getValue())
							: null);

			// Parses the specified click event specified on the replacement to a ClickEvent
			// instance from md5.
			textComponent.setClickEvent(
					fragment.getClickEvent() != null
							? new ClickEvent(ClickEvent.Action.values()[fragment.getClickEvent().getAction().ordinal()],
									fragment.getClickEvent().getValue())
							: null);

			// If there's no color specified in this component, and there was a previous
			// component, then apply the previous format, as if there's a color, it
			// shouldn't be changing the format of the current color.
			if (components.size() > 0 && colored) {
				BaseComponent lastComponent = components.get(components.size() - 1);
				lastComponent = lastComponent.getExtra().get(lastComponent.getExtra().size() - 1);
				for (int i = 0; i < textComponent.getExtra().size(); i++) {
					BaseComponent extraComponent = textComponent.getExtra().get(i);

					if (extraComponent.getColorRaw() != null) {
						extraComponent.setBold(extraComponent.isBoldRaw() != null ? extraComponent.isBold() : false);
						extraComponent
								.setItalic(extraComponent.isItalicRaw() != null ? extraComponent.isItalic() : false);
						extraComponent.setObfuscated(
								extraComponent.isObfuscatedRaw() != null ? extraComponent.isObfuscated() : false);
						extraComponent.setStrikethrough(
								extraComponent.isStrikethroughRaw() != null ? extraComponent.isStrikethrough() : false);
						extraComponent.setUnderlined(
								extraComponent.isUnderlinedRaw() != null ? extraComponent.isUnderlined() : false);

						lastComponent = extraComponent;
						break;
					}

					extraComponent.setColor(lastComponent.getColorRaw());
					extraComponent.setBold(extraComponent.isBoldRaw() != null ? extraComponent.isBoldRaw()
							: lastComponent.isBoldRaw());
					extraComponent.setInsertion(extraComponent.getInsertion() != null ? extraComponent.getInsertion()
							: lastComponent.getInsertion());
					extraComponent.setItalic(extraComponent.isItalicRaw() != null ? extraComponent.isItalicRaw()
							: lastComponent.isItalicRaw());
					extraComponent
							.setObfuscated(extraComponent.isObfuscatedRaw() != null ? extraComponent.isObfuscatedRaw()
									: lastComponent.isObfuscatedRaw());
					extraComponent.setStrikethrough(
							extraComponent.isStrikethroughRaw() != null ? extraComponent.isStrikethroughRaw()
									: lastComponent.isStrikethroughRaw());
					extraComponent
							.setUnderlined(extraComponent.isUnderlinedRaw() != null ? extraComponent.isUnderlinedRaw()
									: lastComponent.isUnderlinedRaw());

					if (i == 0) {
						textComponent.setColor(lastComponent.getColorRaw());
						textComponent.setBold(textComponent.isBoldRaw() != null ? textComponent.isBoldRaw()
								: lastComponent.isBoldRaw());
						textComponent.setInsertion(textComponent.getInsertion() != null ? textComponent.getInsertion()
								: lastComponent.getInsertion());
						textComponent.setItalic(textComponent.isItalicRaw() != null ? textComponent.isItalicRaw()
								: lastComponent.isItalicRaw());
						textComponent
								.setObfuscated(textComponent.isObfuscatedRaw() != null ? textComponent.isObfuscatedRaw()
										: lastComponent.isObfuscatedRaw());
						textComponent.setStrikethrough(
								textComponent.isStrikethroughRaw() != null ? textComponent.isStrikethroughRaw()
										: lastComponent.isStrikethroughRaw());
						textComponent
								.setUnderlined(textComponent.isUnderlinedRaw() != null ? textComponent.isUnderlinedRaw()
										: lastComponent.isUnderlinedRaw());
					}
				}
			}

			// Adds the new TextCompoonent to the list.
			components.add(textComponent);
		});

		return components;
	}

}
