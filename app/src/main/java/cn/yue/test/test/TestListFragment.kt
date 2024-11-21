package cn.yue.test.test

import cn.yue.base.mvvm.components.BasePageVMFragment
import cn.yue.base.widget.recyclerview.CommonAdapter
import cn.yue.base.widget.recyclerview.CommonViewHolder
import cn.yue.test.R

/**
 * Description:
 * Created by yue on 5/11/2024
 */
class TestListFragment : BasePageVMFragment<TestListViewModel, TestBean>() {

    override fun needScaffold(): Boolean {
        return false
    }

    override fun initAdapter(): CommonAdapter<TestBean> {
        return object : CommonAdapter<TestBean>() {
            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_test
            }

            override fun bindData(holder: CommonViewHolder, position: Int, itemData: TestBean) {
                holder.setText(R.id.tv_test, "$position")
            }

        }
    }
}