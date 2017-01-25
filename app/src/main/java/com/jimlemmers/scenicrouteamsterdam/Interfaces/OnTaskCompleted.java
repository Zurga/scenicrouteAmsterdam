package com.jimlemmers.scenicrouteamsterdam.Interfaces;

import com.jimlemmers.scenicrouteamsterdam.Async.RouteGetter;

/**
 * Created by jim on 1/21/17.
 */

public interface OnTaskCompleted {
    void onTaskCompleted(RouteGetter routeGetter);
}