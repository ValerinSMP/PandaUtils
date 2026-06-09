package darkpanda73.PandaUtils.PandaPlugin.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bukkit.event.Listener;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface PandaListeners {

	public Class<? extends Listener>[] value() default {};

}
