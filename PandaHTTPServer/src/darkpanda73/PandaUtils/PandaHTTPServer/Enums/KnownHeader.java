package darkpanda73.PandaUtils.PandaHTTPServer.Enums;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class KnownHeader {
	public static final KnownHeader CONTENT_TYPE = new KnownHeader("Content-Type");
	public static final KnownHeader CONTENT_LENGTH = new KnownHeader("Content-Length");
	public static final KnownHeader CONTENT_DISPOSITION = new KnownHeader("Content-Disposition");
	public static final KnownHeader CONNECTION = new KnownHeader("Connection");
	public static final KnownHeader LOCATION = new KnownHeader("Location");
	public static final KnownHeader CACHE_CONTROL = new KnownHeader("Cache-Control");

	public static final KnownHeader[] VALUES;

	static {
		List<KnownHeader> staticFields = new ArrayList<>();
		for (Field field : KnownHeader.class.getDeclaredFields()) {
			try {
				if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && !field.getType().isArray()) {
					staticFields.add(KnownHeader.class.cast(field.get(null)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		VALUES = staticFields.toArray(new KnownHeader[staticFields.size()]);
	}

	private String headerKey;

	private KnownHeader(String headerKey) {
		this.headerKey = headerKey;
	}

	public String getHeaderKey() {
		return this.headerKey;
	}

	public static KnownHeader getKnownHeader(String header) {
		for (KnownHeader knownHeader : VALUES) {
			if (knownHeader.getHeaderKey().equalsIgnoreCase(header)) {
				return knownHeader;
			}
		}
		return new KnownHeader(header);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof KnownHeader)) {
			return false;
		}
		return ((KnownHeader) obj).getHeaderKey().equalsIgnoreCase(headerKey);
	}

}
