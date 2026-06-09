package darkpanda73.PandaUtils.PandaPlugin.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface PandaListener {

	/**
	 * Indicates if this listener can be injected into other classes. This will
	 * include the service inside the injectables list.
	 * 
	 * @return
	 */
	public boolean injectable() default true;

	/**
	 * Indicates if this listener is optional. If the listener fails on instantiate,
	 * it'll be ignored.
	 * 
	 * @return
	 */
	public boolean optional() default false;

	/**
	 * Any method inside a service assigned with the {@link PostConstructMethod}
	 * annotation will get executed after the service has been constructed. The
	 * plugin will check for any exception, including {@link ReportResult} or
	 * {@link ReportError} exceptions.
	 * 
	 * This method allows to have two type of parameter structures: No parameters,
	 * or only a paramater for {@link ReportResult}.
	 * 
	 * @author DarkPanda73
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@Inherited
	public static @interface PostConstructMethod {

	}

}
