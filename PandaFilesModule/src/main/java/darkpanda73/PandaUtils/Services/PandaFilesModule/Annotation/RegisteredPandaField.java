package darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegisteredPandaField {

	/**
	 * ID of the file where this field should be registered.
	 * 
	 * @return
	 */
	public String value();

}
