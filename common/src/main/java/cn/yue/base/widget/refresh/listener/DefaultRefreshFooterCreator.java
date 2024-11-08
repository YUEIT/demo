package cn.yue.base.widget.refresh.listener;

import android.content.Context;

import androidx.annotation.NonNull;

import cn.yue.base.widget.refresh.api.RefreshFooter;
import cn.yue.base.widget.refresh.api.RefreshLayout;


/**
 * 默认Footer创建器
 * Created by scwang on 2018/1/26.
 */
public interface DefaultRefreshFooterCreator {
    @NonNull
    RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout);
}
