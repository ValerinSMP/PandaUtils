package darkpanda73.PandaUtils.Utilities.Java.Objects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtilities {

	@SafeVarargs
	public static <T> T firstNonNull(T... objects) {
		for (T obj : objects) {
			if (obj != null) {
				return obj;
			}
		}
		return null;
	}

	public static byte[] serialize(final Object obj) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
			out.writeObject(obj);
			out.flush();
			return bos.toByteArray();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

		try (ObjectInput in = new ObjectInputStream(bis)) {
			return (T) in.readObject();
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}

}
