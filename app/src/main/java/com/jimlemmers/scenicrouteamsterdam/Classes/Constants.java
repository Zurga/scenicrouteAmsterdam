package com.jimlemmers.scenicrouteamsterdam.Classes;


/**
 * Created by jim on 1/13/17.
 */
public final class Constants {
    public static final String PACKAGE_NAME = "com.jimlemmers.scenicrouteamsterdam";
    public static final String FIREBASE_ROUTE = "routes";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final long GEOFENCE_TIMEOUT = 3000l;
    public static final int GEOFENCE_RADIUS = 5000;
    public static final String SERVER_URL = "http://163.172.221.210:5000/api/scenicroute";
    public static final String TEST_ROUTE = "{'route':[{'lat': 52.3556845, 'lng': 4.9545822}," +
            " {'lat': 52.3559067, 'lng': 4.9549596}," +
            " {'lat': 52.3562829, 'lng': 4.9545162}," +
            " {'lat': 52.3544275, 'lng': 4.951395}," +
            " {'lat': 52.3443541, 'lng': 4.9345173}," +
            " {'lat': 52.3475559, 'lng': 4.9293173, 'name': 'Test '}," +
            " {'lat': 52.34595909999999, 'lng': 4.921158399999999}," +
            " {'lat': 52.3478944, 'lng': 4.919024299999999}," +
            " {'lat': 52.360085, 'lng': 4.9088033}," +
            " {'lat': 52.3611022, 'lng': 4.908108599999999}," +
            " {'lat': 52.36228879999999, 'lng': 4.9074203}," +
            " {'lat': 52.3662128, 'lng': 4.905046899999999}," +
            " {'lat': 52.3675905, 'lng': 4.904230099999999}," +
            " {'lat': 52.3683752, 'lng': 4.9040911}," +
            " {'lat': 52.3695367, 'lng': 4.9012271}," +
            " {'lat': 52.3720978, 'lng': 4.9004373}," +
            " {'lat': 52.3721442, 'lng': 4.8994889}," +
            " {'lat': 52.3699287, 'lng': 4.897421}," +
            " {'lat': 52.3702281, 'lng': 4.8958461}," +
            " {'lat': 52.3704123, 'lng': 4.8952648}]," +
            "'pois':[{'lat': 52.3556845, 'lng': 4.9545822, 'name': 'SP'}]}";
    public static final String TEST_ROUTE_2 = "{'route':[{'lat':52.3788138,'lng':4.8991056}," +
            "{'lat':52.3788138,'lng':4.8991056}," +
            "{'lat':52.3788138,'lng':4.8991056}," +
            "{'lat':52.37854350000001,'lng':4.8991733}," +
            "{'lat':52.37854350000001,'lng':4.8991733}," +
            "{'lat':52.37772649999999,'lng':4.899070699999999}," +
            "{'lat':52.37772649999999,'lng':4.899070699999999}," +
            "{'lat':52.3775395,'lng':4.8986028}," +
            "{'lat':52.3775395,'lng':4.8986028}," +
            "{'lat':52.3772972,'lng':4.8984513}," +
            "{'lat':52.3772972,'lng':4.8984513}," +
            "{'lat':52.3761938,'lng':4.896976}," +
            "{'lat':52.3761938,'lng':4.896976}," +
            "{'lat':52.3764741,'lng':4.8962284}," +
            "{'lat':52.3764741,'lng':4.8962284}," +
            "{'lat':52.37637059999999,'lng':4.896104600000001}," +
            "{'lat':52.37637059999999,'lng':4.896104600000001}," +
            "{'lat':52.37633539999999,'lng':4.896060299999999}," +
            "{'lat':52.37633539999999,'lng':4.896060299999999}," +
            "{'lat':52.3760413,'lng':4.895610599999999}," +
            "{'lat':52.3760413,'lng':4.895610599999999}," +
            "{'lat':52.3758524,'lng':4.8962033}," +
            "{'lat':52.3758524,'lng':4.8962033}," +
            "{'lat':52.3759601,'lng':4.8958656}," +
            "{'lat':52.3759601,'lng':4.8958656}," +
            "{'lat':52.3760413,'lng':4.895610599999999}," +
            "{'lat':52.3760413,'lng':4.895610599999999}," +
            "{'lat':52.3756877,'lng':4.8950608}," +
            "{'lat':52.3756877,'lng':4.8950608}," +
            "{'lat':52.375608,'lng':4.8949812}," +
            "{'lat':52.375608,'lng':4.8949812}," +
            "{'lat':52.3736694,'lng':4.892695199999999}," +
            "{'lat':52.3736694,'lng':4.892695199999999}," +
            "{'lat':52.3734271,'lng':4.8932706}," +
            "{'lat':52.3734271,'lng':4.8932706}," +
            "{'lat':52.3731716,'lng':4.8936582}]}";
}