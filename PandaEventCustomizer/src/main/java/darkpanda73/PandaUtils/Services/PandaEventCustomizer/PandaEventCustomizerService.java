package darkpanda73.PandaUtils.Services.PandaEventCustomizer;

import java.util.HashMap;
import java.util.function.Supplier;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Utils.JavaUtils;
import darkpanda73.PandaUtils.Services.PandaEventCustomizer.Annotations.PandaCustomizedEvent;

@PandaService
public class PandaEventCustomizerService {

	@PandaInject
	private PandaPluginClass plugin;

	private HashMap<Class<?>, Class<?>> events = new HashMap<>();

	@LoadMethod
	private void load() {
		JavaUtils.getClasses().forEach(clazz -> {
			if (clazz.isAnnotationPresent(PandaCustomizedEvent.class)) {
				events.put(clazz.getAnnotation(PandaCustomizedEvent.class).value(), clazz);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T> T generateEvent(Class<T> eventClass, Class<?>[] argsClasses, Object[] args, Supplier<T> orDefault) {
		try {
			if (events.containsKey(eventClass)) {
				return (T) events.get(eventClass).getConstructor(argsClasses).newInstance(args);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orDefault.get();
	}

}
