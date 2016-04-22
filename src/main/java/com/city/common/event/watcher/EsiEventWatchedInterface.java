package com.city.common.event.watcher;

import com.city.common.event.EsiEvent;
import com.city.common.event.listener.EsiListenerInterface;

/**
 * Created by wys on 2016/1/8.
 */
public interface EsiEventWatchedInterface {
    public boolean notifyAllListener(EsiEvent eEvent);

    public void addListener(EsiListenerInterface listener,Integer index);

    public void removeListener(EsiListenerInterface listener);

    public void suspendAllListener();

    public void resumeAllListener();
}
