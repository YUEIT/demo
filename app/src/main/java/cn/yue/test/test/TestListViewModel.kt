package cn.yue.test.test

import cn.yue.base.fragment.vm.PageViewModel
import cn.yue.base.net.wrapper.BaseListBean
import io.reactivex.rxjava3.core.Single

/**
 * Description:
 * Created by yue on 5/11/2024
 */
class TestListViewModel : PageViewModel<TestBean>() {
    override fun doLoadData(nt: Int) {
      Single.fromCallable {
          BaseListBean<TestBean>().apply {
              for (i in 0.. 10) {
                mList?.add(TestBean())
              }
          }
      }.defaultSubscribe()
    }
}