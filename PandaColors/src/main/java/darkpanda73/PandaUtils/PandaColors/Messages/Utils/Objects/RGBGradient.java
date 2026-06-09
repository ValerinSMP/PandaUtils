package darkpanda73.PandaUtils.PandaColors.Messages.Utils.Objects;

import java.util.ArrayList;
import java.util.List;

import darkpanda73.PandaUtils.PandaColors.Enums.ChatColor;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment;
import darkpanda73.PandaUtils.PandaColors.Messages.Utils.ColorUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RGBGradient {

	private List<RGBColor> colors = new ArrayList<>();
	private boolean obfuscated = false;
	private boolean bold = false;
	private boolean strikethrough = false;
	private boolean underline = false;
	private boolean italic = false;
	private int waveLength = -1;

	public void applyGradient(List<MessageFragment> fragments, Pair<Integer, MessageFragment> startFragment,
			Pair<Integer, MessageFragment> endFragment) {
		if (colors.size() == 0) {
			return;
		}

		int startFragmentIndex = fragments.indexOf(startFragment.getSecond());
		int endFragmentIndex = fragments.indexOf(endFragment.getSecond());
		int startCharIndex = startFragment.getFirst();
		int endCharIndex = endFragment.getFirst();

		if (startFragment.getSecond() == endFragment.getSecond()) {
			String startStripped = ColorUtilities.strip(
					startFragment.getSecond().getText().substring(startFragment.getFirst(), endFragment.getFirst()));
			endCharIndex = startCharIndex + startStripped.length();
			startFragment.getSecond()
					.setText(startFragment.getSecond().getText().substring(0, startFragment.getFirst())
							.concat(startStripped)
							.concat(startFragment.getSecond().getText().substring(endFragment.getFirst())));
			endFragment.setFirst(endCharIndex);
		} else {
			String startStripped = ColorUtilities
					.strip(startFragment.getSecond().getText().substring(startFragment.getFirst()));
			startFragment.getSecond().setText(
					startFragment.getSecond().getText().substring(0, startFragment.getFirst()).concat(startStripped));

			String endStripped = ColorUtilities
					.strip(endFragment.getSecond().getText().substring(0, endFragment.getFirst()));
			endCharIndex = endStripped.length();
			endFragment.getSecond()
					.setText(endStripped.concat(endFragment.getSecond().getText().substring(endFragment.getFirst())));
		}

		int messageLength = startFragment.getSecond() == endFragment.getSecond() ? (endCharIndex - startCharIndex)
				: (startFragment.getSecond().getText().length() - startCharIndex + endCharIndex);

		for (int i = startFragmentIndex + 1; i < endFragmentIndex; i++) {
			MessageFragment fragment = fragments.get(i);
			fragment.setText(ColorUtilities.strip(fragment.getText()));
			messageLength += fragment.getText().length();
		}

		if (messageLength == 0) {
			return;
		}

		StringBuilder sb = new StringBuilder();

		if (colors.size() == 1) {
			sb.append(startFragment.getSecond().getText().substring(0, startCharIndex));
			sb.append(ChatColor.rgbToColor(colors.get(0).toSpigotColor().substring(1)).toString());

			if (obfuscated) {
				sb.append("§k");
			}

			if (bold) {
				sb.append("§l");
			}

			if (strikethrough) {
				sb.append("§m");
			}

			if (underline) {
				sb.append("§n");
			}

			if (italic) {
				sb.append("§o");
			}

			sb.append(startFragment.getSecond().getText().substring(startCharIndex));

			startFragment.getSecond().setText(sb.toString());
		} else {
			int limit = colors.size() - 1;

			int currentColor = 0;
			int waveLength = this.waveLength != -1 ? this.waveLength : messageLength;
			boolean reverse = false;
			RGBColor startColor = colors.get(0);
			RGBColor endColor = colors.get(1);

			int gradientStartIndex = 0;
			int gradientEndIndex = (int) (waveLength * ((1D) / limit));
			int gradientCurrentIndex = 0;

			for (int i = startFragmentIndex; i <= endFragmentIndex; i++) {
				MessageFragment fragment = fragments.get(i);

				sb.setLength(0);
				int startIndex = fragment == startFragment.getSecond() ? startFragment.getFirst() : 0;
				int endIndex = fragment == endFragment.getSecond() ? endFragment.getFirst()
						: fragment.getText().length();

				for (int m = startIndex; m < endIndex; m++) {
					while ((gradientCurrentIndex + gradientStartIndex) == gradientEndIndex) {
						if (currentColor + 1 == limit) {
							reverse = !reverse;
							currentColor = 0;

							startColor = colors.get(!reverse ? currentColor : (limit - currentColor));
							endColor = colors.get(!reverse ? (currentColor + 1) : (limit - currentColor - 1));

							gradientStartIndex = 0;
							gradientEndIndex = (int) (waveLength * ((1D) / limit));
							gradientCurrentIndex = 0;
						} else {
							currentColor++;

							startColor = colors.get(!reverse ? currentColor : (limit - currentColor));
							endColor = colors.get(!reverse ? (currentColor + 1) : (limit - currentColor - 1));

							gradientStartIndex = gradientEndIndex;
							gradientEndIndex = (int) (waveLength * ((double) (1D + currentColor) / limit));
							gradientCurrentIndex = 0;
						}
					}

					double percent = (double) gradientCurrentIndex++
							/ ((double) (gradientEndIndex - gradientStartIndex));
					sb.append(ChatColor.rgbToColor(String.format("#%02x%02x%02x",
							(startColor.getRed() + (int) ((endColor.getRed() - startColor.getRed()) * percent)),
							(startColor.getGreen() + (int) ((endColor.getGreen() - startColor.getGreen()) * percent)),
							(startColor.getBlue() + (int) ((endColor.getBlue() - startColor.getBlue()) * percent)))));

					if (obfuscated) {
						sb.append("§k");
					}

					if (bold) {
						sb.append("§l");
					}

					if (strikethrough) {
						sb.append("§m");
					}

					if (underline) {
						sb.append("§n");
					}

					if (italic) {
						sb.append("§o");
					}

					sb.append(fragment.getText().charAt(m));
				}

				fragment.setText(fragment.getText().substring(0, startIndex).concat(sb.toString())
						.concat(fragment.getText().substring(endIndex)));
			}
		}
	}

}
