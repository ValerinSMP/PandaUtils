package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ItemGeneratorCollection.class)
public @interface ItemGenerator {

	public String value();

}
