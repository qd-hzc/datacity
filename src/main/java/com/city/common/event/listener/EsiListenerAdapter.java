package com.city.common.event.listener;

import com.city.common.event.EsiEvent;

/**
 * Created by wys on 2016/1/8.
 */
public abstract class EsiListenerAdapter implements EsiListenerInterface {
    private boolean suspend = false;

    public abstract boolean handlerEvent(EsiEvent eEvent);

    @Override
    public boolean isSupend() {
        return suspend;
    }

    @Override
    public void suspend() {
        this.suspend = true;
    }

    @Override
    public void resume() {
        this.suspend = false;
    }
}
