package darkpanda73.PandaUtils.PandaColors.Messages.NMS.Base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Message;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Utils.ColorUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Utils.FragmentUtilities;
import darkpanda73.PandaUtils.PandaColors.Messages.Utils.RGBUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class BaseMessageBuilder implements MessageBuilder {

	protected static final MethodResult PLAYER_SENDTITLE;
	protected static final MethodResult PLAYER_SENDTITLEINOUT;

	static {
		PLAYER_SENDTITLE = MethodFinder.forClass(Player.class).setName("sendTitle")
				.setParams(String.class, String.class).findSafely();
		PLAYER_SENDTITLEINOUT = MethodFinder.forClass(Player.class).setName("sendTitle")
				.setParams(String.class, String.class, int.class, int.class, int.class).findSafely();
	}

	@Override
	public void sendTitle(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {
		if (PLAYER_SENDTITLEINOUT.isPresent()) {
			PLAYER_SENDTITLEINOUT.invokeSafely(player, title, subtitle, fadein, stay, fadeout);
		} else if (PLAYER_SENDTITLE.isPresent()) {
			PLAYER_SENDTITLE.invokeSafely(player, title, subtitle);
		}
	}

	@Override
	public Message createMessage(MessageTemplate template) {
		long start = System.currentTimeMillis();

		List<List<? extends BaseComponent>> components = new ArrayList<>();
		List<String> lines = new ArrayList<>();

		Arrays.asList(template.getText()).parallelStream().map(text -> {
			// Step 1: For each line, transform the content into an array of MessageFragment
			// instances. This way we have the normal text and the replaced text.

			List<MessageFragment> fragments = FragmentUtilities.textToFragments(text, template.getReplacements());

			// Step 2: Process gradient patterns before they are turned into minecraft
			// colors.
			if (template.isColor()) {
				RGBUtilities.replaceHexPattern(fragments);
				RGBUtilities.replaceGradientPattern(fragments);
			} else {
				RGBUtilities.removeHexPatterns(fragments);
				RGBUtilities.removeGradientPatterns(fragments);
			}

			return fragments;
		}).collect(Collectors.toList()).forEach(fragments -> {
			components.add(FragmentUtilities.fragmentsToComponents(fragments, template.isColor()));

			lines.add(ColorUtilities.stripColorExceed(fragments.stream().map(fragment -> {
				switch (fragment.getColorMode()) {
				case STRIP:
					return ColorUtilities.strip(fragment.getText());
				case IGNORE:
					return fragment.getText();
				default:
					return template.isColor() ? ColorUtilities.color(fragment.getText())
							: ColorUtilities.strip(fragment.getText());
				}
			}).collect(Collectors.joining())));
		});

		Message message = new Message(components, lines);
		message.setTimeRequired(System.currentTimeMillis() - start);
		return message;
	}

}
