package darkpanda73.PandaUtils.PandaUtilities.Objects;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.scheduler.BukkitTask;

import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.Location.SimpleLocation;
import darkpanda73.PandaUtils.PandaUtilities.Location.SimpleLocation.SimpleLocationArea;
import darkpanda73.PandaUtils.PandaUtilities.Objects.QueryContainer.IdentifiedEntity;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableRunnable;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableSupplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class QueryContainer<T extends IdentifiedEntity> implements Closeable {

	private Supplier<Collection<T>> collectionSupplier;
	private Function<T, SimpleLocation> locationSupplier;

	private @Setter @Getter long searchRadius = 32;
	private @Setter @Getter long validRadius = 16;
	private @Setter @Getter long ttl = 1000 * 60 * 2;
	private @Setter @Getter boolean ignoreY = false;

	private List<CachedQuery<T>> cachedQueries = new ArrayList<>();
	private BukkitTask cachedQueriesTask;

	private @Getter boolean closed = false;

	public QueryContainer(Supplier<Collection<T>> collectionSupplier, Function<T, SimpleLocation> locationSupplier) {
		this.collectionSupplier = collectionSupplier;
		this.locationSupplier = locationSupplier;

		this.cachedQueriesTask = TasksUtils.executeOnAsyncWithTimer(() -> {
			try {
				executeSynchronized(() -> cachedQueries.removeIf(cachedQuery -> {
					if (System.currentTimeMillis() - cachedQuery.getLastRequest() > ttl) {
						cachedQuery.setExpired(true);
						return true;
					}
					return false;
				}));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}, 20L, 20L);
	}

	public void reset() {
		cachedQueries.clear();
	}

	@Override
	public void close() throws IOException {
		reset();
		cachedQueriesTask.cancel();

		closed = true;
	}

	public void onCreate(T object) {
		if (closed) {
			throw new IllegalAccessError("The query container is already closed");
		}

		this.cachedQueries.forEach(cachedQuery -> {
			if (cachedQuery.simpleLocationArea.isInside(locationSupplier.apply(object))) {
				cachedQuery.getObjects().add(object);
			}
		});
	}

	public void onUpdate(T object) {
		if (closed) {
			throw new IllegalAccessError("The query container is already closed");
		}

		onDelete(object);
		onCreate(object);
	}

	public void onDelete(T object) {
		if (closed) {
			throw new IllegalAccessError("The query container is already closed");
		}

		this.cachedQueries.forEach(cachedQuery -> cachedQuery.getObjects()
				.removeIf(existendObject -> existendObject.getUuid().equals(object.getUuid())));
	}

	public CachedQuery<T> query(SimpleLocation location) {
		if (closed) {
			throw new IllegalAccessError("The query container is already closed");
		}

		return query(SimpleLocationArea.of(location, location));
	}

	public CachedQuery<T> query(SimpleLocationArea locationArea) {
		if (closed) {
			throw new IllegalAccessError("The query container is already closed");
		}

		Optional<CachedQuery<T>> foundCachedQuery = this.cachedQueries.stream()
				.filter(cachedQuery -> cachedQuery.getSimpleLocationArea().isAllInside(locationArea)).findFirst();

		if (foundCachedQuery.isPresent()) {
			foundCachedQuery.get().setLastRequest(System.currentTimeMillis());
		} else {
			SimpleLocationArea validLocationArea = locationArea.clone();
			validLocationArea.getMinLocation().substract(validRadius, validRadius, validRadius);

			if (ignoreY) {
				validLocationArea.getMinLocation().setY(Double.NEGATIVE_INFINITY);
			}

			validLocationArea.getMaxLocation().add(validRadius, validRadius, validRadius);

			if (ignoreY) {
				validLocationArea.getMaxLocation().setY(Double.POSITIVE_INFINITY);
			}

			SimpleLocationArea searchLocationArea = locationArea.clone();
			searchLocationArea.getMinLocation().substract(searchRadius, searchRadius, searchRadius);

			if (ignoreY) {
				searchLocationArea.getMinLocation().setY(Double.NEGATIVE_INFINITY);
			}

			searchLocationArea.getMaxLocation().add(searchRadius, searchRadius, searchRadius);

			if (ignoreY) {
				searchLocationArea.getMaxLocation().setY(Double.POSITIVE_INFINITY);
			}

			CachedQuery<T> cachedQuery = new CachedQuery<T>(validLocationArea,
					collectionSupplier.get().stream()
							.filter(object -> searchLocationArea.isInside(locationSupplier.apply(object)))
							.collect(Collectors.toList()),
					System.currentTimeMillis());
			foundCachedQuery = Optional.of(cachedQuery);

			this.cachedQueries.removeIf(existentCachedQuery -> cachedQuery.getSimpleLocationArea()
					.isAllInside(existentCachedQuery.getSimpleLocationArea()));
			this.cachedQueries.add(0, cachedQuery);
		}

		return foundCachedQuery.get();
	}

	public <S> S executeSynchronizedWithReturn(FailableSupplier<S> func) throws Throwable {
		synchronized (this) {
			return func.get();
		}
	}

	public void executeSynchronized(FailableRunnable func) throws Throwable {
		executeSynchronizedWithReturn(() -> {
			func.run();
			return null;
		});
	}

	@ToString(onlyExplicitlyIncluded = true)
	@EqualsAndHashCode
	public static class CachedQuery<T extends IdentifiedEntity> {

		private @ToString.Include SimpleLocationArea simpleLocationArea;
		private @ToString.Include List<T> objects;
		private @ToString.Include long lastRequest;
		private @ToString.Include boolean expired = false;

		public CachedQuery(SimpleLocationArea simpleLocationArea, List<T> protections, long lastRequest) {
			this.simpleLocationArea = simpleLocationArea;
			this.objects = protections;
			this.lastRequest = lastRequest;
		}

		public SimpleLocationArea getSimpleLocationArea() {
			return this.simpleLocationArea;
		}

		public List<T> getObjects() {
			return this.objects;
		}

		public long getLastRequest() {
			return this.lastRequest;
		}

		public CachedQuery<T> setLastRequest(long lastRequest) {
			this.lastRequest = lastRequest;
			return this;
		}

		public boolean isExpired() {
			return this.expired;
		}

		public CachedQuery<T> setExpired(boolean expired) {
			this.expired = expired;
			return this;
		}

	}

	public static interface IdentifiedEntity {

		public UUID getUuid();

	}

}
