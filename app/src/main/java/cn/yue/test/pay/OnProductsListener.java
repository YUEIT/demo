package cn.yue.test.pay;

import androidx.annotation.NonNull;

import java.util.HashMap;

/**
 * Description:
 * Created by Luo biao on 8/7/24
 */
public interface OnProductsListener {

    void onResult(@NonNull HashMap<String, ProductInfo> products);

    void onFailure(int code, String message);
}

