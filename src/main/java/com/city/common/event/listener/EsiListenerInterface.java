package com.city.common.event.listener;

import com.city.common.event.EsiEvent;

/**
 * Created by wys on 2016/1/8.
 */
public interface EsiListenerInterface {
    public boolean handlerEvent(EsiEvent eEvent);

    public boolean isSupend();

    public void suspend();

    public void resume();
}
