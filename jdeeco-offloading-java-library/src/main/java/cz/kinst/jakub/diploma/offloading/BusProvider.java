package cz.kinst.jakub.diploma.offloading;

import de.greenrobot.event.EventBus;

/**
 * Created by jakubkinst on 21/01/15.
 */
public class BusProvider {
    private static final EventBus sBus = new EventBus();

    public static EventBus get() {
        return sBus;
    }
}
