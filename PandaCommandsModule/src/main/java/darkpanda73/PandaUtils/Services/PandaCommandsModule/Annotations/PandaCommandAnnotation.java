package darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface PandaCommandAnnotation {

	public String id();

	public String pathName();

	public boolean helpCommand() default true;

	public String defaultName();

	public String defaultDescription();

	public String defaultPermission() default "";

	public String defaultUsage() default "";

	public String[] defaultAliases() default {};

	public int cooldown() default 0;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	public static @interface PandaSubCommandAnnotation {

		public Class<? extends PandaCommand> parentCommand();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	public static @interface ShowInfoOn {

		public Class<? extends PandaCommand> value();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	public static @interface Customizable {

		public boolean name() default true;

		public boolean description() default true;

		public boolean permission() default false;

		public boolean usage() default false;

		public boolean aliases() default false;

		public boolean cooldown() default false;

	}

}
