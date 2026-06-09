package darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PandaFieldDescription {

	/**
	 * Description of the current field.
	 * 
	 * @return
	 */
	public String value() default "";

	/**
	 * Description of the current field on a list format.
	 * 
	 * @return
	 */
	public String[] list() default {};

}
