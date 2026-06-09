package darkpanda73.PandaUtils.PandaColors;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.Utilities.Java.String.StringUtilities;

public class Test {

	public static void main(String[] args) {
		MessageTemplate.inst("&e{arena_room_name} &8| &eWave &a{current_round} &estarting in &a{time}s")
				.setReplacements(new Replacement("{arena_room_name}", () -> "Test"),
						new Replacement("{current_round}",
								() -> String.valueOf(5 + 1 - ((int) (Math.min(25, 5) / 5) * 5))),
						new Replacement("{time}", () -> StringUtilities.toDecimal(20000 - ((double) 5 / 20D), 1)))
				.toString();

		long start = System.currentTimeMillis();

		Replacement[] replacements = new Replacement[] { new Replacement("{arena_room_name}", () -> "Test"),
				new Replacement("{current_round}", () -> String.valueOf(5 + 1 - ((int) (Math.min(25, 5) / 5) * 5))),
				new Replacement("{time}", () -> StringUtilities.toDecimal(20000 - ((double) 5 / 20D), 1)) };

		for (int i = 0; i < 200; i++) {
			MessageTemplate.inst("&e{arena_room_name} &8| &eWave &a{current_round} &estarting in &a{time}s")
					.setReplacements(replacements).toString();
		}

		System.out.println("Time required: " + (System.currentTimeMillis() - start));
	}

}
