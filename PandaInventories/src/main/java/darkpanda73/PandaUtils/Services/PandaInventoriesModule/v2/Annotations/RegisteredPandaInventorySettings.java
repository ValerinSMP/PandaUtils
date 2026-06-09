package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisteredPandaInventorySettings {

	public String value();

	public String folderPath();

}
