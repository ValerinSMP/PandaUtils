package darkpanda73.PandaUtils.PandaPlugin.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;

/**
 * Annotation implemented to inject objects into a field. This fields has two
 * requirements: Be on a registered entity, or being statics. Registered
 * entities are the ones automatically detected by the {@link PandaPluginClass},
 * such as {@link PandaService} or {@link PandaListener}. For other entities
 * that are instantiated outside the plugin class, this fields must be static to
 * be detected.
 * 
 * @author DarkPanda73
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface PandaInject {

	/**
	 * Annotation to determine which method is the one that should be executed after
	 * injecting the entities into their fields. This follows the same as the
	 * {@link PandaInject} annotation, only registered entities or static fields may
	 * trigger this method.
	 * 
	 * @author relam
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@Inherited
	public @interface PostInjectMethod {

	}

}
