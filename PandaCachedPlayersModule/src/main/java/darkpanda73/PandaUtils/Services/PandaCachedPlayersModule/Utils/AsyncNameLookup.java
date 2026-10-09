package darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/** Bounded, coalesced lookup. No Bukkit access or I/O while holding the monitor. */
public final class AsyncNameLookup<T> implements AutoCloseable {
    private final Map<String, CompletableFuture<T>> pending = new HashMap<>();
    private final Executor executor;
    private final int limit;
    private boolean closed;

    public AsyncNameLookup(Executor executor, int limit) {
        this.executor = executor;
        this.limit = limit;
    }

    public CompletableFuture<T> lookup(String name, Function<String, T> loader) {
        if (name == null || !name.matches("[A-Za-z0-9_.]{1,32}")) {
            return CompletableFuture.completedFuture(null);
        }
        String key = name.toLowerCase(Locale.ROOT);
        CompletableFuture<T> future;
        synchronized (this) {
            if (closed) return CompletableFuture.failedFuture(new IllegalStateException("Lookup closed"));
            future = pending.get(key);
            if (future != null) return future.thenApply(value -> value);
            if (pending.size() >= limit) {
                return CompletableFuture.failedFuture(new IllegalStateException("Lookup busy"));
            }
            future = new CompletableFuture<>();
            pending.put(key, future);
        }
        CompletableFuture<T> result = future;
        result.orTimeout(15, TimeUnit.SECONDS);
        try {
            executor.execute(() -> {
                try {
                    if (!result.isDone()) result.complete(loader.apply(name));
                } catch (Throwable error) {
                    result.completeExceptionally(error);
                } finally {
                    // Keep the slot until the actual query ends, even if the caller timed out.
                    synchronized (this) { pending.remove(key, result); }
                }
            });
        } catch (RuntimeException error) {
            synchronized (this) { pending.remove(key, result); }
            result.completeExceptionally(error);
        }
        return result.thenApply(value -> value);
    }

    @Override
    public synchronized void close() {
        closed = true;
        pending.values().forEach(future -> future.completeExceptionally(new IllegalStateException("Lookup closed")));
        pending.clear();
    }
}
