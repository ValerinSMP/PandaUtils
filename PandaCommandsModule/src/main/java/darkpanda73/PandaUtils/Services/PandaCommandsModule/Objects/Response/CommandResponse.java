package darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response;

import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(chain = true)
public abstract class CommandResponse {

	public static enum Status {

		NOT_RUNNING, RUNNING, COMPLETED

	}

	private @Setter(AccessLevel.PROTECTED) @Getter Status status = Status.NOT_RUNNING;
	private @Getter long start = System.currentTimeMillis();
	private @Getter @Setter Long cooldown = null;

	public void execute() {
		setStatus(Status.COMPLETED);
	}

	public static CommandResponse trueResponse() {
		return new TrueResponse();
	}

	public static CommandResponse falseResponse() {
		return new FalseResponse();
	}

	public static QueuedCommandResponse queued(Runnable runnable) {
		return new QueuedCommandResponse(runnable);
	}

	public static QueuedCommandResponse queuedSync(Runnable runnable) {
		return new SyncQueuedCommandResponse(runnable);
	}

	public static QueuedCommandResponse queuedAsync(Runnable runnable) {
		return new AsyncQueuedCommandResponse(runnable);
	}

	public static class TrueResponse extends CommandResponse {

	}

	public static class FalseResponse extends CommandResponse {

	}

	@Data
	@EqualsAndHashCode(callSuper = false)
	@ToString(callSuper = false)
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class QueuedCommandResponse extends CommandResponse {

		public static enum RunMode {
			ASYNC, SYNC, INHERIT
		}

		private @NonNull Runnable action;
		private @Setter(value = AccessLevel.NONE) QueuedCommandResponse nextCommand;

		public QueuedCommandResponse next(Runnable action) {
			return this.nextCommand = queued(action);
		}

		public QueuedCommandResponse nextSync(Runnable action) {
			return this.nextCommand = queuedSync(action);
		}

		public QueuedCommandResponse nextAsync(Runnable action) {
			return this.nextCommand = queuedAsync(action);
		}

		@Override
		public void execute() {
			setStatus(Status.RUNNING);
			try {
				action.run();
				setStatus(Status.COMPLETED);
				if (getNextCommand() != null) {
					nextCommand.execute();
				}
			} catch (Throwable e) {
				e.printStackTrace();
				setStatus(Status.COMPLETED);
			}
		}

		public Status getStatus() {
			if (super.getStatus() == Status.RUNNING) {
				return Status.RUNNING;
			} else if (super.getStatus() == Status.COMPLETED) {
				if (nextCommand != null) {
					return nextCommand.getStatus();
				} else {
					return Status.COMPLETED;
				}
			}
			return Status.NOT_RUNNING;
		}

	}

	public static class SyncQueuedCommandResponse extends QueuedCommandResponse {

		private SyncQueuedCommandResponse(@NonNull Runnable action) {
			super(action);
		}

		@Override
		public void execute() {
			TasksUtils.execute(() -> super.execute());
		}

	}

	public static class AsyncQueuedCommandResponse extends QueuedCommandResponse {

		public AsyncQueuedCommandResponse(@NonNull Runnable action) {
			super(action);
		}

		@Override
		public void execute() {
			TasksUtils.executeOnAsync(() -> super.execute());
		}

	}

}
