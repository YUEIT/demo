package cn.yue.test.pay;

import android.app.Activity;

import java.util.List;

/**
 * Description:
 * Created by yue on 8/8/24
 */
public interface IPaymentHelper {

    void startConnect();

    void stopConnect();

    void queryProducts(List<String> productIdArray, OnProductsListener listener);

    void startPayment(Activity context,
                      String productId,
                      String offsetToken,
                      OnPaymentListener onPaymentListener);

}
