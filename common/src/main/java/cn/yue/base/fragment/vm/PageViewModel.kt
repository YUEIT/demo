package cn.yue.base.fragment.vm

import cn.yue.base.net.wrapper.BaseListBean
/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class PageViewModel<S>() : ListViewModel<BaseListBean<S>, S>()