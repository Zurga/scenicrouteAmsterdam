package com.jimlemmers.scenicrouteamsterdam;

/**
 * Created by jim on 1/13/17.
 */
public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.jimlemmers.scenicrouteamsterdam";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    public static final long GEOFENCE_TIMEOUT = 1000l;
    public static final int GEOFENCE_RADIUS = 10;
    public static final String SERVER_URL = "http://jimlemmers.com/api/scenicroute/";
    public static final String TEST_ROUTE = "{'route':[{'lat': 52.3556845, 'lng': 4.9545822}," +
            " {'lat': 52.3559067, 'lng': 4.9549596}," +
            " {'lat': 52.3562829, 'lng': 4.9545162}," +
            " {'lat': 52.3544275, 'lng': 4.951395}," +
            " {'lat': 52.3443541, 'lng': 4.9345173}," +
            " {'lat': 52.3475559, 'lng': 4.9293173}," +
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
            " {'lat': 52.3704123, 'lng': 4.8952648}]}";
}