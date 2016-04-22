package com.city.common.event.watcher;

import com.city.common.event.EsiEvent;
import com.city.common.event.listener.EsiListenerInterface;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wys on 2016/1/8.
 */
@Component
public class EsiEventWatched implements EsiEventWatchedInterface {
    List<EsiEvent> events = new ArrayList<>();
    List<EsiListenerInterface> listeners = new ArrayList<>();


    @Override
    public boolean notifyAllListener(EsiEvent eEvent) {
        boolean result = true;
        for (EsiListenerInterface listener : listeners) {
            if (!listener.isSupend()) {
                result = listener.handlerEvent(eEvent);
                if (!result)
                    break;
            }
        }
        return result;
    }

    /**
     * @param listener
     * @param index    null 添加到最后
     */
    @Override
    public void addListener(EsiListenerInterface listener, Integer index) {
        if (listener != null) {
            if (index != null)
                listeners.add(index, listener);
            else
                listeners.add(listener);
        }
    }

    @Override
    public void removeListener(EsiListenerInterface listener) {

    }

    @Override
    public void suspendAllListener() {
        for (EsiListenerInterface listener : listeners) {
            listener.suspend();
        }
    }

    @Override
    public void resumeAllListener() {
        for (EsiListenerInterface listener : listeners) {
            listener.resume();
        }
    }

}
