package darkpanda73.PandaUtils.PandaUpdateChecker.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;

public class PandaUpdateCheckerUtilities {

	public static void getVersion(String resourceId, Consumer<String> consumer) {
		TasksUtils.executeOnAsync(() -> {
			try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId + "/~")
					.openStream(); Scanner scann = new Scanner(is)) {
				if (scann.hasNextLine()) {
					consumer.accept(scann.nextLine());
				}
			} catch (IOException e) {
			}
		});
	}
}
