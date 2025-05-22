package kebab_simulator.event.services.process;

import kebab_simulator.event.services.EventProcess;
import kebab_simulator.event.services.EventProcessCallback;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class EventLoadAssetsProcess<@Nullable T> extends EventProcess {

    private Logger logger = LoggerFactory.getLogger(EventLoadAssetsProcess.class);
    private Supplier<T> func;
    private EventProcessCallback<T> callback;

    public EventLoadAssetsProcess(String name, Supplier<T> func) {
        this(name, func, null);
    }

    public EventLoadAssetsProcess(String name, Supplier<T> func, EventProcessCallback<T> callback) {
        super(name);
        this.func = func;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            T data = this.func.get();
            if (this.callback != null) this.callback.onSuccess(data);

        } catch (Exception e) {
            this.logger.error("Error while processing assets: \n{}", e);
            if (this.callback != null) this.callback.onFailure(e);
        }
    }
}
