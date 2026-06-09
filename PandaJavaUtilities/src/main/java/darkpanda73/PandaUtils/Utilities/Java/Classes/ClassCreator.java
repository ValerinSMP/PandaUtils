package darkpanda73.PandaUtils.Utilities.Java.Classes;

import java.util.Arrays;

public class ClassCreator<T> {

	private Class<? extends T>[] classes;
	private Object[] args = new Object[0];

	@SafeVarargs
	public final ClassCreator<T> setClasses(Class<? extends T>... classes) {
		this.classes = classes;
		return this;
	}

	public final ClassCreator<T> setArgs(Object... args) {
		this.args = args;
		return this;
	}

	public final T getInstance() throws Throwable {
		if (classes == null) {
			throw new NullPointerException("Classes to be used must be specifed");
		}

		Class<?>[] constructorClasses = Arrays.stream(args).map(Object::getClass)
				.toArray(length -> new Class<?>[length]);
		Throwable throwable = null;
		for (Class<? extends T> clazz : classes) {
			try {
				return clazz.getConstructor(constructorClasses).newInstance(args);
			} catch (Throwable e) {
				throwable = e;
				continue;
			}
		}

		if (throwable != null) {
			throw throwable;
		} else {
			throw new NullPointerException("Unable to load class to manage skins");
		}
	}

	public final T getInstanceSafely() {
		try {
			return getInstance();
		} catch (Throwable e) {
			return null;
		}
	}

	public static <T> ClassCreator<T> inst() {
		return new ClassCreator<>();
	}

}
