package cn.yue.test.pay;

import java.util.List;

/**
 * Description:
 * Created by yue on 8/8/24
 */
public class ProductInfo {

    public String productId;

    public List<SubscriptionInfo> subscriptions;

    public static class SubscriptionInfo {

        public String offerToken;

        public String offerId;

        public String priceCurrencyCode;

        public String formattedPrice;

        public long priceAmountMicros;

        public String billingPeriod;

        public int billingCycleCount;
    }

}
