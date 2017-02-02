package com.jimlemmers.scenicrouteamsterdam.Interfaces;

import com.jimlemmers.scenicrouteamsterdam.Models.Route;

/**
 * Created by jim on 1/21/17.
 */

public interface RouteReceived {
    void onRouteReceived(Route route);
}