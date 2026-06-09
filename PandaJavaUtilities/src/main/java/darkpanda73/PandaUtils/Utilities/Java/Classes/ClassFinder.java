package darkpanda73.PandaUtils.Utilities.Java.Classes;

import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Accessors(chain = true)
public class ClassFinder {

	private final @ToString.Include String packageAndName;

	public static ClassFinder forClass(String packageAndName) {
		return new ClassFinder(packageAndName);
	}

	public ClassResult findSafely() {
		try {
			Class.forName(packageAndName);
			return new ClassResult(Class.forName(packageAndName));
		} catch (ClassNotFoundException e) {
			return new ClassResult(null);
		}
	}

	public ClassResult find() {
		ClassResult clazz = findSafely();

		if (clazz.isEmpty()) {
			throw new NullPointerException("Unable to find field: " + this.toString());
		}

		return clazz;
	}

	@AllArgsConstructor
	public static class ClassResult {

		@Getter
		private Class<?> foundClass;

		public boolean isEmpty() {
			return this.foundClass == null;
		}

		public boolean isPresent() {
			return this.foundClass != null;
		}

		public void ifPresent(Consumer<ClassResult> classResult) {
			if (isPresent()) {
				classResult.accept(this);
			}
		}

	}

}
