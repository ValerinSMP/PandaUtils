package darkpanda73.PandaUtils.PandaColors.Messages.Objects;

import java.util.function.Supplier;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.ClickEvent;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.ColorMode;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.HoverEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(chain = true)
@ToString
public class Replacement {

	private @Getter @NonNull String textToReplace;
	private @Getter @NonNull MessageFragment messageFragment;

	public Replacement(String texttoreplace, Supplier<String> textProvider) {
		this(texttoreplace, textProvider, null, null);
	}

	public Replacement(String texttoreplace, Supplier<String> textProvider, ClickEvent clickevent) {
		this(texttoreplace, textProvider, clickevent, null);
	}

	public Replacement(String texttoreplace, Supplier<String> textProvider, HoverEvent hoverevent) {
		this(texttoreplace, textProvider, null, hoverevent);
	}

	public Replacement(String texttoreplace, Supplier<String> textProvider, ClickEvent clickevent,
			HoverEvent hoverevent) {
		this(texttoreplace, new MessageFragment(textProvider, clickevent, hoverevent));
	}

	public Replacement setHoverEvent(HoverEvent hoverevent) {
		this.messageFragment.setHoverEvent(hoverevent);
		return this;
	}

	public Replacement setClickEvent(ClickEvent clickevent) {
		this.messageFragment.setClickEvent(clickevent);
		return this;
	}

	public Replacement cacheText(boolean cacheText) {
		this.messageFragment.setCacheText(cacheText);
		return this;
	}

	public Replacement colorMode(ColorMode colorMode) {
		this.messageFragment.setColorMode(colorMode);
		return this;
	}

}
