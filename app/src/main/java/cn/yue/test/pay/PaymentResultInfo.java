package cn.yue.test.pay;

import com.google.gson.annotations.SerializedName;

/**
 * Description:
 * Created by yue on 8/8/24
 */
public class PaymentResultInfo {

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
}
