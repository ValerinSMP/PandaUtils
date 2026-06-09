package darkpanda73.PandaUtils.PandaSQLModule.v2.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation required to load the information about the allowed SQLs to use and
 * some default information required for both of them, or default data to be
 * used.
 * 
 * @author DarkPanda73
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface PandaSQLConfigV2 {

	/**
	 * If the database is required or not. In case of being false, the service will
	 * keep the plugin running even without database.
	 * 
	 * @return
	 */
	public boolean required() default true;

	/**
	 * If it's allowed the usage of MySQL. In case of allowed, it'll generate the
	 * required fields on the 'config' file registered on the PandaFilesService.
	 * 
	 * @return
	 */
	public boolean allowMySQL() default false;

	/**
	 * If the MySQL configuration should be forced to be on true, so it's forced to
	 * use MySQL. This is intended if it has to always connect to MySQL and fail if
	 * it cannot.
	 * 
	 * @return
	 */
	public boolean forceMySQL() default false;

	/**
	 * If it's allowed the usage of SQLite. By default it's true, but can be turned
	 * off to make the usage of MySQL mandatory.
	 * 
	 * @return
	 */
	public boolean allowSQLite() default true;

}
