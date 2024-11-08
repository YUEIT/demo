package cn.yue.test.pay;

/**
 * Description:
 * Created by yue on 8/8/24
 */
public interface OnPaymentListener {

    void onSuccess(PaymentResultInfo info);

    void onFailure(int code, String error);
}
