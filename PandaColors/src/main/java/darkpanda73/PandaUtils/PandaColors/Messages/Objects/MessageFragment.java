package darkpanda73.PandaUtils.PandaColors.Messages.Objects;

import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;

public class MessageFragment implements Cloneable {

	public static enum ColorMode {
		COLOR, STRIP, IGNORE
	}

	private Supplier<String> textProvider;
	private @Getter @Setter ClickEvent clickEvent;
	private @Getter @Setter HoverEvent hoverEvent;
	private String suppliedText;
	private @Getter @Setter ColorMode colorMode = ColorMode.COLOR;
	private @Getter @Setter boolean cacheText = true;

	private MessageFragment() {
	}

	public MessageFragment(Supplier<String> textProvider) {
		this(textProvider, null, null);
	}

	public MessageFragment(Supplier<String> textProvider, ClickEvent clickevent) {
		this(textProvider, clickevent, null);
	}

	public MessageFragment(Supplier<String> textProvider, HoverEvent hoverevent) {
		this(textProvider, null, hoverevent);
	}

	public MessageFragment(Supplier<String> textProvider, ClickEvent clickevent, HoverEvent hoverevent) {
		this.textProvider = textProvider;
		this.clickEvent = clickevent;
		this.hoverEvent = hoverevent;
	}

	public String getText() {
		if (!cacheText) {
			return textProvider.get();
		}
		return suppliedText != null ? suppliedText : (suppliedText = textProvider.get());
	}

	public void setText(String text) {
		this.suppliedText = text;
		this.textProvider = () -> this.suppliedText;
	}

	@Override
	public String toString() {
		return getText();
	}

	public MessageFragment clone() {
		MessageFragment fragment = new MessageFragment();

		fragment.textProvider = this.textProvider;
		fragment.clickEvent = this.clickEvent;
		fragment.hoverEvent = this.hoverEvent;
		fragment.suppliedText = this.suppliedText;
		fragment.colorMode = this.colorMode;
		fragment.cacheText = this.cacheText;

		return fragment;
	}

	public static class ClickEvent {

		private Action action;
		private String value;

		public ClickEvent(Action action, String value) {
			this.action = action;
			this.value = value;
		}

		public Action getAction() {
			return action;
		}

		public String getValue() {
			return value;
		}

		public enum Action {
			OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE
		}

	}

	@AllArgsConstructor
	@Getter
	public static class HoverEvent {

		private Action action;
		private BaseComponent[] value;

		public enum Action {
			SHOW_TEXT, SHOW_ACHIEVEMENT, SHOW_ITEM, SHOW_ENTITY
		}

	}

}
