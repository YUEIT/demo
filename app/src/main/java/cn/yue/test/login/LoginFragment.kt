package cn.yue.test.login

import android.content.Intent
import android.os.Bundle
import cn.yue.base.fragment.BaseVMFragment
import cn.yue.base.utils.code.setOnSingleClickListener
import cn.yue.test.R
import cn.yue.test.databinding.FragmentLoginBinding

class LoginFragment : BaseVMFragment<LoginViewModel>() {

    private lateinit var thirdLoginHelper: ThirdLoginHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thirdLoginHelper = ThirdLoginHelper(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    private lateinit var binding: FragmentLoginBinding
    override fun initView(savedInstanceState: Bundle?) {
        thirdLoginHelper.init()
        binding = FragmentLoginBinding.bind(requireView())
        binding.loginTV.setOnSingleClickListener {

        }
    }

    override fun onFragmentBackPressed(): Boolean {
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        thirdLoginHelper.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}
