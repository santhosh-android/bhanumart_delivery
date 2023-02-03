package com.cm_grocery.app.Services;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CM_GroceryDeliveryInterface {
    //String BASE_URL = "https://bhanumart.com/new/webservices/";
    String BASE_URL = "https://bhanumart.com/dev/v2/";

    @FormUrlEncoded
    @POST("db-login")
    Call<ResponseBody> getLogin(@FieldMap Map<String, String> loginMap);

    @GET("settings")
    Call<ResponseBody> getSettings();

    @FormUrlEncoded
    @POST("db-new-orders")
    Call<ResponseBody> getOrderApi(@FieldMap Map<String, String> orderMap);

    @FormUrlEncoded
    @POST("db-view-order")
    Call<ResponseBody> getOrderDetails(@Field("order_id") String getDetails);

    @FormUrlEncoded
    @POST("db-dashboard-counts")
    Call<ResponseBody> getDashBorad(@Field("db_id") String getCount);

    @FormUrlEncoded
    @POST("db-profile")
    Call<ResponseBody> getProfile(@Field("db_id") String profile);

    @FormUrlEncoded
    @POST("db-tip-balance")
    Call<ResponseBody> getTip(@Field("db_id") String balance);

    @FormUrlEncoded
    @POST("db-confirm-pickup")
    Call<ResponseBody> pickUpOrderApi(@FieldMap Map<String, String> pickUpOrder);

    @FormUrlEncoded
    @POST("db-pickup-orders")
    Call<ResponseBody> getPickUpOrders(@FieldMap Map<String, String> PickUpMap);

    @FormUrlEncoded
    @POST("db-confirm-delivery")
    Call<ResponseBody> orderDeliveryApiCall(@FieldMap Map<String, String> deliveryOrder);

    @FormUrlEncoded
    @POST("db-completed-orders")
    Call<ResponseBody> getCompletedOrders(@FieldMap Map<String, String> completedOrdsMap);

    @FormUrlEncoded
    @POST("db-wallet-balance")
    Call<ResponseBody> getWalletbalance(@Field("db_id") String walletBalance);

    @FormUrlEncoded
    @POST("db-transfer-to-admin")
    Call<ResponseBody> sendWalletMoney(@FieldMap Map<String, String> moneySendtoAdmin);

    @GET("payment-via-list")
    Call<ResponseBody> getpaymentVia();

    //Pick and Drop Ordes
    @FormUrlEncoded
    @POST("pd-new-orders")
    Call<ResponseBody> getPickNewOrders(@Field("db_id") String newOrders);

    @FormUrlEncoded
    @POST("pd-confirm-pickup")
    Call<ResponseBody> pickdropPickUpApi(@FieldMap Map<String, String> pickDropApi);

    @FormUrlEncoded
    @POST("pd-confirm-delivery")
    Call<ResponseBody> confirmDeliviryApi(@FieldMap Map<String, String> orderDelivery);

    @FormUrlEncoded
    @POST("pd-pickup-orders")
    Call<ResponseBody> getPickPickUp(@Field("db_id") String pickUp);

    @FormUrlEncoded
    @POST("pd-completed-orders")
    Call<ResponseBody> getCompletedPickup(@Field("db_id") String completedPick);

    @FormUrlEncoded
    @POST("pd-view-order")
    Call<ResponseBody> getOrderDetailsApi(@Field("order_id") String viewOrders);

    @FormUrlEncoded
    @POST("update-db-status")
    Call<ResponseBody> updateStatus(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("update-db-lat-long")
    Call<ResponseBody> updateLocation(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("db-shift-change-request")
    Call<ResponseBody> updateShift(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("db-shifts")
    Call<ResponseBody> getShifts(@Field("db_id") String viewOrders);

    @FormUrlEncoded
    @POST("db-complete-report")
    Call<ResponseBody> getReports(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("check-db-version")
    Call<ResponseBody> versionCheck(@Field("version") String viewOrders);

    @FormUrlEncoded
    @POST("db-accept-order")
    Call<ResponseBody> acceptOrder(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("db-reject-order")
    Call<ResponseBody> cancelOrder(@FieldMap Map<String, String> map);

}
