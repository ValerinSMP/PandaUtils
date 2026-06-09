package darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import lombok.Getter;

public class PandaCustomizablePermission extends PandaPermission {

	protected @Getter PandaStringField fileField;

	public PandaCustomizablePermission(String path, String defaultPermission) {
		this(path, defaultPermission, Insertion.NONE);
	}

	public PandaCustomizablePermission(String path, String defaultPermission, Insertion defaultInsertion) {
		this(path, path, defaultPermission, defaultInsertion);
	}

	public PandaCustomizablePermission(String path, String oldPath, String defaultPermission) {
		this(path, oldPath, defaultPermission, Insertion.NONE);
	}

	public PandaCustomizablePermission(String path, String oldPath, String defaultPermission,
			Insertion defaultInsertion) {
		super(defaultPermission, defaultInsertion);

		fileField = new PandaStringField("Permissions." + path, oldPath,
				(defaultInsertion.getSymbol() != null ? defaultInsertion.getSymbol().toString() + defaultPermission
						: defaultPermission));
	}

	public String getDefaultPermission() {
		return super.getPermission();
	}

	public Insertion getDefaultInsertion() {
		return super.getInsertion();
	}

	@Override
	public String getPermission() {
		return getInsertion() != Insertion.NONE ? fileField.getContent().substring(1) : fileField.getContent();
	}

	@Override
	public Insertion getInsertion() {
		return !fileField.getContent().isEmpty() ? Insertion.findBySymbol(fileField.getContent().charAt(0))
				: Insertion.NONE;
	}

}
