package com.cm_grocery.app.helper;

/**
 * Purpose : Holding all the reusable
 * Created by : Pushpendra Kumar at 05th March 2021
 */
public interface IConstants {
    /**
     * Purpose : All the usable intent key types is here.
     */
    class IntentStrings {
        public static String fragmentType = "fragmentType";
    }

    /**
     * Purpose : All the fragments is here which we are opening through the intent.
     */
    class FragmentTypes {
        public static String login = "login";
        public static String register = "register";
        public static String forgotPassword = "forgotPassword";
        public static String updatePassword = "updatePassword";
        public static String otp = "otp";
        public static String booking = "boooking";
        public static String bookingSuccess = "boookingSuccess";
        public static String myRequest = "My Accept Leads";
        public static String mySubscriptions = "My Subscriptions";
        public static String profile = "Profile";
        public static String profileEdit = "ProfileEdit";
        public static String vehicleEdit = "VehicleEdit";
        public static String faq = "faq";
        public static String contact = "contact";
        public static String about = "about";
        public static String driverReg = "driverReg";
        public static String privacy = "privacy";
        public static String terms = "terms";
        public static String membership = "membership";
        public static String completed = "completed";
        public static String notifications = "notifications";
        public static String activitypermission = "activitypermission";
        public static String startTheRide = "Start The Ride";
        public static String endRide = "End Ride";
        public static String offerRide = "Offer a Ride";
        public static String completeRides = "Complete Rides";
        public static String manageProfile = "Manage Profile";
        public static String driverSubscriptions = "My Subscriptions";
        public static String logout = "Logout";
    }

    /**
     * Purpose : All the default values is here.
     */
    class Defaults {
        public static String request_from = "service_vendor_android_app";
    }

    /**
     * Purpose : All the shared preferences names is here.
     */
    class Pref {
        public static String shared_preference = "shared_preference";
        public static String username = Params.username;
        public static String password = Params.password;
        public static String isLogin = "isLogin";
        public static String userId = "user_id";
        public static String rideStatus = "ride_status";
        public static final String LOCATION = "location";
    }

    /**
     * Purpose : All the params names will be here of the entire application.
     */
    class Params {
        public static String username = "username";
        public static String password = "password";
    }

    class CameraRequest {
        public static final int REQUEST_CAMERA_PERMISSION_CODE = 8002;
        public static final int REQUEST_GALLERY_PERMISSION_CODE = 8003;
        public static final int REQUEST_LOCATION_PERMISSION_CODE = 8005;

        public static final int OPEN_ADD_GALLERY_CODE = 6001;
        public static final int OPEN_ADD_CAMERA_CODE = 6002;

        public static final String CAMERA = "camera";
        public static final String GALLERY = "gallery";


        public static final int CROP_REQUEST_CODE = 9001;
        public static final int DIALOAGE_REQUEST_CODE = 9002;
    }

    /**
     * Purpose : All the observable events will be here.
     * Used this events for observing main view model data.
     */
    enum ObserverEvents {
        OPEN_MAIN_ACTIVITY, OPEN_LOGIN_SCREEN, OPEN_REGISTER_SCREEN, OPEN_FORGOT_PWD_SCREEN, START_LOADER, STOP_LOADER,
        SEND_BACK, OTP, BOOKING_REQUEST, UPDATE_PROFILE, PROFILE, PHOTO, FAQ, CONTACT, ABOUT, PRIVACY, TERMS, LOGOUT,
        REGISTER, MEMBERSHIP, CHECK_USER, COMPLETED, UPDATE_VEHICLE, OPEN_UPDATE_PASSWORD, CHANGE_PASSWORD,
        VEHICLE_REGISTRATION, IMAGE_SELECTION, OFFER_RIDE,OPEN_NOTIFICATIONS,OPEN_OVER_LAY_SETTINGS,REQUEST_PERMISSIONS,CHECK_GPS
    }
    class REQUEST {
        public static final int GPS = 1001;
    }
}
