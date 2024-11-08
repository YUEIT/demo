package cn.yue.test.pay;

import com.google.gson.annotations.SerializedName;

/**
 * Description:
 * Created by yue on 8/7/24
 */
public class PaymentReportBean {

    @SerializedName("pkg")
    public String pkg;
    @SerializedName("deviceId")
    public String deviceId;
    @SerializedName("ver")
    public String ver;
    @SerializedName("orderType")
    public String orderType;
    @SerializedName("productId")
    public String productId;
    @SerializedName("purchaseToken")
    public String purchaseToken;
    @SerializedName("priceCurrencyCode")
    public String priceCurrencyCode;
    @SerializedName("priceAmountMicros")
    public String priceAmountMicros;
    @SerializedName("environment")
    public String environment;
    @SerializedName("gaid")
    public String gaid;
    @SerializedName("adid")
    public String adid;
    @SerializedName("ssid")
    public String ssid;
}
