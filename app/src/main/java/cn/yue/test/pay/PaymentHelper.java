package cn.yue.test.pay;


import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.yue.base.utils.Utils;

public class PaymentHelper implements IPaymentHelper, PurchasesUpdatedListener {

    private final static String TAG = "PaymentHelper";
    private final HashMap<String, ProductDetails> productMap = new HashMap<>();

    private final BillingClient client = BillingClient.newBuilder(Utils.getContext())
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts().build())
        .build();

    private final BillingClientStateListener stateListener = new BillingClientStateListener() {

        /**
         * 连接断开
         */
        @Override
        public void onBillingServiceDisconnected() {
            Log.i(TAG, "billingHelper -> disconnected");
            startConnect();
        }

        /**
         * 连接成功
         */
        @Override
        public void onBillingSetupFinished(@NonNull BillingResult result) {
            Log.i(TAG, "billingHelper -> billing setup finish");
            if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                queryProducts();
            }
        }

    };

    @Override
    public void startConnect() {
        Log.i(TAG, "billingHelper -> startConnect");
        client.startConnection(stateListener);

    }

    @Override
    public void stopConnect() {
        Log.i(TAG, "billingHelper -> stopConnect");
        if (client.isReady()) {
            client.endConnection();
        }
    }

    private void queryProducts() {
        if (client.isReady()) {
            Log.i(TAG, "billingHelper -> query products");
            List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
            for (String productId : productMap.keySet()) {
                productList.add(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build());
            }
            QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();
            client.queryProductDetailsAsync(params, (billingResult, list) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "billingHelper -> query billing size " + list.size());
                    HashMap<String, ProductInfo> productInfoList = new HashMap<>();
                    for (ProductDetails productDetails : list) {
                        Log.i(TAG, "billingHelper -> query billing size " + productDetails);
                        productMap.put(productDetails.getProductId(), productDetails);
                        ProductInfo productInfo = new ProductInfo();
                        productInfo.productId = productDetails.getProductId();
                        List<ProductInfo.SubscriptionInfo> subscriptionList = new ArrayList<>();
                        productInfo.subscriptions = subscriptionList;
                        List<ProductDetails.SubscriptionOfferDetails> subs = productDetails.getSubscriptionOfferDetails();
                        if (subs != null) {
                            for (ProductDetails.SubscriptionOfferDetails details : subs) {
                                ProductInfo.SubscriptionInfo subscriptionInfo = new ProductInfo.SubscriptionInfo();
                                if (!details.getPricingPhases().getPricingPhaseList().isEmpty()) {
                                    ProductDetails.PricingPhase pricingPhase = details.getPricingPhases().getPricingPhaseList().get(0);
                                    subscriptionInfo.offerToken = details.getOfferToken();
                                    subscriptionInfo.offerId = details.getOfferId();
                                    subscriptionInfo.formattedPrice = pricingPhase.getFormattedPrice();
                                    subscriptionInfo.priceAmountMicros = pricingPhase.getPriceAmountMicros();
                                    subscriptionInfo.priceCurrencyCode = pricingPhase.getPriceCurrencyCode();
                                    subscriptionInfo.billingPeriod = pricingPhase.getBillingPeriod();
                                    subscriptionInfo.billingCycleCount = pricingPhase.getBillingCycleCount();
                                    subscriptionList.add(subscriptionInfo);
                                }
                            }
                        }
                        productInfoList.put(productInfo.productId, productInfo);
                    }
                    Log.i(TAG, "billingHelper -> query billing result " + new Gson().toJson(productInfoList));
                    if (onProductsListener != null) {
                        OnProductsListener listener = onProductsListener.get();
                        if (listener != null) {
                            listener.onResult(productInfoList);
                        }
                    }
                } else {
                    Log.i(TAG, "billingHelper -> query billing failure ");
                    if (onProductsListener != null) {
                        OnProductsListener listener = onProductsListener.get();
                        if (listener != null) {
                            listener.onFailure(billingResult.getResponseCode(),
                                billingResult.getDebugMessage());
                        }
                    }
                }
            });
        } else {
            client.startConnection(stateListener);
        }
    }

    private WeakReference<OnProductsListener> onProductsListener;

    @Override
    public void queryProducts(List<String> productIdArray, OnProductsListener listener) {
        this.onProductsListener = new WeakReference<>(listener);
        for (String productId : productIdArray) {
            if (!productMap.containsKey(productId)) {
                productMap.put(productId, null);
            }
        }
        queryProducts();
    }

    private WeakReference<OnPaymentListener> onPaymentListener;

    @Override
    public void startPayment(Activity context,
                             String productId,
                             String offsetToken,
                             OnPaymentListener onPaymentListener) {
        this.onPaymentListener = new WeakReference<>(onPaymentListener);
        queryPurchases(context, productId, offsetToken);
    }

    private void queryPurchases(Activity context, String productId, String offsetToken) {
        ProductDetails product = productMap.get(productId);
        if (product == null) {
            return;
        }
        Log.i(TAG, "billingHelper -> query purchase $productId");
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build();
        client.queryPurchasesAsync(params, (billingResult, list) -> {
            Purchase unknowPurchase = null;
            for (Purchase purchase : list) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED
                    && !purchase.isAcknowledged()) {
                    unknowPurchase = purchase;
                }
            }
            if (unknowPurchase == null) {
                launchPayment(context, product, offsetToken);
            } else {
                handlePurchase(list);
            }
        });
    }

    private void launchPayment(Activity context, ProductDetails productDetail, String offsetToken) {
        Log.i(TAG, "billingHelper -> launch billing flow $productDetail");
        if (!client.isReady()) {
            paymentFailure(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED
                , "Play Store service is not connected now - potentially transient state");
            return;
        }
        if (productDetail == null) {
            paymentFailure(BillingClient.BillingResponseCode.ITEM_UNAVAILABLE
                , "Requested product is not available for purchase");
            return;
        }
        BillingFlowParams.ProductDetailsParams params = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetail)
            .setOfferToken(offsetToken)
            .build();
        BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(new ArrayList<>(Collections.singleton(params)))
//            .setObfuscatedAccountId(payload) //指定开发人员有效负载与购买信息一起发回
            .setObfuscatedAccountId("ad_id")
            .setObfuscatedProfileId("gaid")
            .build();
        BillingResult result = client.launchBillingFlow(context, purchaseParams);
        if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            if (onPaymentListener != null) {
                OnPaymentListener listener = onPaymentListener.get();
                if (listener != null) {
                    listener.onFailure(result.getResponseCode(), result.getDebugMessage());
                }
            }
        }
        Log.i(TAG, "billingHelper -> launch billing pay " + result);
    }

    /**
     * 完成支付后，同步购买数据
     */
    private void handlePurchase(List<Purchase> purchaseList) {
        for (Purchase purchase : purchaseList) {
            handlePurchase(purchase);
        }
    }

    private void handlePurchase(Purchase purchase) {
        Log.i(TAG, "billingHelper -> consume async");
        if (!client.isReady()) {
            paymentFailure(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED
                ,"Play Store service is not connected now - potentially transient state");
            return;
        }
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED
            && !purchase.isAcknowledged()) {
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
            client.consumeAsync(consumeParams, (result , s) -> {
                if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    paymentSuccess(purchase);
                } else {
                    paymentFailure(result.getResponseCode(), result.getDebugMessage());
                }
            });
        }

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                client.acknowledgePurchase(acknowledgePurchaseParams, (result -> {
                    if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        paymentSuccess(purchase);
                    } else {
                        paymentFailure(result.getResponseCode(), result.getDebugMessage());
                    }
                }));
            }
        }
    }

    private void paymentFailure(int code, String message) {
        Log.i(TAG, "payment failure");
        if (onPaymentListener != null) {
            OnPaymentListener listener = onPaymentListener.get();
            if (listener != null) {
                listener.onFailure(code, message);
            }
        }
    }

    private void paymentSuccess(Purchase purchase) {
        Log.i(TAG, "payment success " + purchase);
        reportPayment(purchase);
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        Log.i(TAG, "billingHelper -> launch billing pay result " + billingResult.getResponseCode());
        switch (billingResult.getResponseCode()) {
            case BillingClient.BillingResponseCode.OK:
                if (list == null || list.isEmpty()) {
                    paymentFailure(BillingClient.BillingResponseCode.ITEM_UNAVAILABLE
                        , "Requested product is not available for purchase");
                } else {
                    handlePurchase(list);
                }
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                paymentFailure(BillingClient.BillingResponseCode.USER_CANCELED
                    , "User canceled the purchase");
                break;
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                paymentFailure(
                    BillingClient.BillingResponseCode.USER_CANCELED
                        , "The user already owns this item");
                break;
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                paymentFailure(BillingClient.BillingResponseCode.USER_CANCELED
                    , "Developer error means that Google Play \n" +
                    "does not recognize the configuration. If you are just getting started, \n" +
                    "make sure you have configured the application correctly in the \n" +
                    "Google Play Console. The SKU product ID must match and the APK you \n" +
                    "are using must be signed with release keys"
                );
                break;
            default:
                paymentFailure(BillingClient.BillingResponseCode.USER_CANCELED ,
                    "Fatal error during the API action");
        }
    }

    private void reportPayment(Purchase purchase) {
        if (purchase.getProducts().isEmpty()) {
            return;
        }
        PaymentResultInfo paymentResultInfo = new PaymentResultInfo();
        paymentResultInfo.purchaseToken = purchase.getPurchaseToken();

        //todo 接口
        if (onPaymentListener != null) {
            OnPaymentListener listener = onPaymentListener.get();
            if (listener != null) {
                listener.onSuccess(paymentResultInfo);
            }
        }
    }

}
