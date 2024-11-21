package cn.yue.test.pay;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.yue.base.activity.BaseFragmentActivity;
import cn.yue.test.R;
import cn.yue.test.databinding.FragmentPaymentBinding;

/**
 * Description:
 * Created by yue on 8/7/24
 */
public class PaymentActivity extends BaseFragmentActivity {

    private final IPaymentHelper paymentHelper = new PaymentHelper();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentHelper.startConnect();
    }

    @Override
    public void initView() {
        super.initView();
        setContentView(R.layout.fragment_payment);
        FragmentPaymentBinding binding = FragmentPaymentBinding.inflate(getLayoutInflater());
        binding.tvPayment.setOnClickListener(v -> {
            paymentHelper.startPayment(this, "", "", new OnPaymentListener() {
                @Override
                public void onSuccess(PaymentResultInfo info) {

                }

                @Override
                public void onFailure(int code, String error) {

                }
            });
        });
        queryProducts();
    }

    private void queryProducts() {
        List<String> products = new ArrayList<>();
        paymentHelper.queryProducts(products, new OnProductsListener() {

            @Override
            public void onResult(@NonNull HashMap<String, ProductInfo> products) {

            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        paymentHelper.stopConnect();
    }
}
