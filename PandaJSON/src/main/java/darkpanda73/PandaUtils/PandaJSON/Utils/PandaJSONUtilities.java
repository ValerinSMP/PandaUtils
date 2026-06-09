package darkpanda73.PandaUtils.PandaJSON.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicInteger;

import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONArray;
import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONData;
import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONElement;
import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONObject;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Objects.ObjectUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;

public class PandaJSONUtilities {

	private static final char START_OBJECT = '{';
	private static final char END_OBJECT = '}';
	private static final char START_ARRAY = '[';
	private static final char END_ARRAY = ']';
	private static final char STRING_DELIMITER = '"';
	private static final char KEYVALUE_SPLITTER = ':';
	private static final char KEYVALUE_LIMIT = ',';
	private static final char SPECIALKEY_IGNORER = '\\';

	public static PandaJSONElement parseJson(File file) throws Exception {
		try (Reader reader = new FileReader(file)) {
			return read(reader);
		} catch (Exception e) {
			throw e;
		}
	}

	public static PandaJSONElement parseJson(InputStream input) throws Exception {
		try (Reader reader = new InputStreamReader(input)) {
			return read(reader);
		} catch (Exception e) {
			throw e;
		}
	}

	public static PandaJSONElement parseJson(String jsonstring) throws Exception {
		try (Reader reader = new StringReader(jsonstring)) {
			return read(reader);
		} catch (Exception e) {
			throw e;
		}
	}

	private static PandaJSONElement read(Reader reader) throws Exception {
		AtomicInteger currentChar = new AtomicInteger(-1);
		while (currentChar.updateAndGet(
				(old) -> ObjectUtilities.firstNonNull(MethodUtilities.catchAndIgnore(() -> reader.read()), -1)) != -1) {
			switch (currentChar.get()) {
			case END_ARRAY:
			case END_OBJECT:
				throw new Exception("Unexpected end of an array/object!");
			case START_ARRAY:
			case START_OBJECT:
				return currentChar.get() == START_ARRAY ? readArray(reader, currentChar)
						: readObject(reader, currentChar);
			default:
				break;
			}
		}
		return new PandaJSONObject();
	}

	private static PandaJSONArray readArray(Reader reader, AtomicInteger currentChar) throws Exception {
		PandaJSONArray array = new PandaJSONArray();
		while (currentChar.updateAndGet(
				(old) -> ObjectUtilities.firstNonNull(MethodUtilities.catchAndIgnore(() -> reader.read()), -1)) != -1) {
			if (currentChar.get() == ' ' || currentChar.get() == '\n' || currentChar.get() == '\r') {
				continue;
			}

			boolean check;
			do {
				check = false;
				switch (currentChar.get()) {
				case END_ARRAY:
					currentChar.set(-1);
					return array;
				case START_ARRAY:
				case START_OBJECT:
					array.add(currentChar.get() == START_ARRAY ? readArray(reader, currentChar)
							: readObject(reader, currentChar));
					break;
				case KEYVALUE_LIMIT:
					break;
				case END_OBJECT:
					throw new Exception("Unexpected end of an object!");
				case KEYVALUE_SPLITTER:
					throw new Exception("Unexpected key value generation!");
				default:
					array.add(readKeyValue(reader, currentChar).getValue());
					if (currentChar.get() != -1)
						check = true;
					break;
				}
			} while (check);
		}
		throw new Exception("Unfinished array!");
	}

	private static PandaJSONObject readObject(Reader reader, AtomicInteger currentChar) throws Exception {
		PandaJSONObject object = new PandaJSONObject();
		while (currentChar.updateAndGet(
				(old) -> ObjectUtilities.firstNonNull(MethodUtilities.catchAndIgnore(() -> reader.read()), -1)) != -1) {
			if (currentChar.get() == ' ' || currentChar.get() == '\n' || currentChar.get() == '\r')
				continue;
			boolean check;
			do {
				check = false;
				switch (currentChar.get()) {
				case END_OBJECT:
					currentChar.set(-1);
					return object;
				case KEYVALUE_LIMIT:
					break;
				case END_ARRAY:
					throw new Exception("Unexpected end of an array!");
				case START_ARRAY:
					throw new Exception("Unexpected start of an array!");
				case START_OBJECT:
					throw new Exception("Unexpected start of an object!");
				case KEYVALUE_SPLITTER:
					throw new Exception("Unexpected key value generation!");
				default:
					Pair<String, PandaJSONElement> entry = readKeyValue(reader, currentChar);
					object.put(entry.getFirst(), entry.getSecond());
					if (currentChar.get() != -1)
						check = true;
					break;
				}
			} while (check);
		}
		throw new Exception("Unfinished object!");
	}

	private static Pair<String, PandaJSONElement> readKeyValue(Reader reader, AtomicInteger currentChar)
			throws Exception {
		String key = null;
		PandaJSONElement value = null;
		StringBuilder sb = new StringBuilder();
		if (currentChar.get() != -1)
			sb.append((char) currentChar.get());
		while (currentChar.updateAndGet(
				(old) -> ObjectUtilities.firstNonNull(MethodUtilities.catchAndIgnore(() -> reader.read()), -1)) != -1) {
			if (sb.length() != 0) {
				if (sb.charAt(0) == STRING_DELIMITER) {
					if (currentChar.get() == STRING_DELIMITER && sb.charAt(sb.length() - 1) != SPECIALKEY_IGNORER) {
						value = new PandaJSONData(sb.toString() + (char) currentChar.get());
						sb.setLength(0);
					} else
						sb.append((char) currentChar.get());
					continue;
				} else if (currentChar.get() == ' ') {
					value = new PandaJSONData(sb.toString());
					sb.setLength(0);
				}
			}

			switch (currentChar.get()) {
			case START_ARRAY:
			case START_OBJECT:
				if (key == null || key.isEmpty())
					throw new Exception("Trying to add an object/array without a key!");
				if (currentChar.get() == START_ARRAY)
					value = readArray(reader, currentChar);
				else
					value = readObject(reader, currentChar);
				break;
			case KEYVALUE_SPLITTER:
				if (value == null)
					throw new Exception("Trying to create a key-value object without key!");
				if (!value.isData())
					throw new Exception("Trying to use an object or array as key!");
				key = value.asData().valueAsString();
				value = null;
				break;
			case KEYVALUE_LIMIT:
				currentChar.set(-1);
			case END_ARRAY:
			case END_OBJECT:
				if (value == null) {
					if (sb.length() == 0)
						throw new Exception("No value found!");
					value = new PandaJSONData(sb.toString().trim());
				}
				if (key != null && key.isEmpty())
					throw new Exception("Trying to load a value with an empty key!");
				return Pair.of(key == null ? "" : key, value);
			default:
				if (currentChar.get() == '\n' || currentChar.get() == '\r'
						|| (currentChar.get() == ' ' && sb.length() == 0)) {
					break;
				}
				if (value == null) {
					sb.append((char) currentChar.get());
				}
				break;
			}
		}
		throw new Exception("Unfinished object!");
	}

}
