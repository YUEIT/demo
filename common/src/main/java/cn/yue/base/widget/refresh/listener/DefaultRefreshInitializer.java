package cn.yue.base.widget.refresh.listener;

import android.content.Context;

import androidx.annotation.NonNull;

import cn.yue.base.widget.refresh.api.RefreshLayout;


/**
 * 默认全局初始化器
 * Created by scwang on 2018/5/29 0029.
 */
public interface DefaultRefreshInitializer {
    void initialize(@NonNull Context context, @NonNull RefreshLayout layout);
}
