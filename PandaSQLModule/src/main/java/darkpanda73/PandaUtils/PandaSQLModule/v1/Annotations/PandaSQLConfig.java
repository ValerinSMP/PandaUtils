package darkpanda73.PandaUtils.PandaSQLModule.v1.Annotations;

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
public @interface PandaSQLConfig {

	/**
	 * The current version of the database. If the version is different from the one
	 * on the database, it'll start the update process.
	 * 
	 * @return
	 */
	public int version();

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
	 * If it's allowed the usage of SQLite. By default it's true, but can be turned
	 * off to make the usage of MySQL mandatory.
	 * 
	 * @return
	 */
	public boolean allowSQLite() default true;

}
