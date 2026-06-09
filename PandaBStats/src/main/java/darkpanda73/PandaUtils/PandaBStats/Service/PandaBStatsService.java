package darkpanda73.PandaUtils.PandaBStats.Service;

import java.util.Arrays;

import org.bstats.bukkit.Metrics;

import darkpanda73.PandaUtils.PandaBStats.Annotations.EnableBStats;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;

@PandaService
public class PandaBStatsService {

	@PandaInject
	private PandaPluginClass plugin;

	private Metrics metrics;

	@PostInjectMethod
	private void postInject() {
	}

	@LoadMethod
	private void load() {
		if (metrics == null) {
			EnableBStats annotation = plugin.getClass().getAnnotation(EnableBStats.class);
			if (annotation != null) {
				metrics = new Metrics(plugin, annotation.value());

				Arrays.stream(annotation.customCharts()).forEach(customChart -> {
					try {
						metrics.addCustomChart(customChart.getConstructor().newInstance());
					} catch (Throwable e) {
						e.printStackTrace();
					}
				});
			}
		}
	}

}
