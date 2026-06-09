package darkpanda73.PandaUtils.PandaPlugin.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface PandaService {

	/**
	 * On which step the service should be loaded. Actually there are only two, LOAD
	 * and ENABLE. The DISABLE load step is currently ignored.
	 * 
	 * @return
	 */
	public LoadStep loadOn() default LoadStep.ENABLE;

	/**
	 * The priority the service has over other services. On lower number means
	 * higher priority. It's suggested to modify this field if you have services
	 * that depends on others.
	 * 
	 * @return
	 */
	public int priority() default 999;

	/**
	 * Indicates if this service can be injected into other classes. This will
	 * include the service inside the injectables list.
	 * 
	 * @return
	 */
	public boolean injectable() default true;

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

	/**
	 * Any method inside a service assigned with the {@link LoadMethod} annotation
	 * will get executed after loading the service. The plugin will check for any
	 * exception, including {@link ReportResult} or {@link ReportError} exceptions.
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
	public static @interface LoadMethod {

	}

	/**
	 * Any method inside a service assigned with the {@link LoadMethod} annotation
	 * will get executed after reloading the service, which only executes this
	 * method, not the other two ones. The plugin will check for any exception,
	 * including {@link ReportResult} or {@link ReportError} exceptions.
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
	public static @interface ReloadMethod {

	}

	/**
	 * Any method inside a service assigned with the {@link UnloadMethod} annotation
	 * will get executed after unloading the service. The plugin will check for any
	 * exception, including {@link ReportResult} or {@link ReportError} exceptions.
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
	public static @interface UnloadMethod {

	}

}
