package darkpanda73.PandaUtils.PandaBStats.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bstats.charts.CustomChart;

import darkpanda73.PandaUtils.PandaBStats.Service.PandaBStatsService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaImport;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaServices;

@PandaImport
@PandaServices(PandaBStatsService.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableBStats {

	public int value();

	public Class<? extends CustomChart>[] customCharts() default {};

}
