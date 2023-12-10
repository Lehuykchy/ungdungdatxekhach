package com.example.ungdungdatxekhach.user.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.ungdungdatxekhach.admin.fragment.LoginAdminFragment
import com.example.ungdungdatxekhach.databinding.ActivityLoginBinding
import com.example.ungdungdatxekhach.user.fragment.LoginUserFragment
import com.google.android.material.tabs.TabLayoutMediator


class LoginActivity : AppCompatActivity(){
    private lateinit var binding: ActivityLoginBinding
    private lateinit var fragmentLoginAdapter : FragmentLoginAdapter
    private lateinit var viewPager : ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        fragmentLoginAdapter = FragmentLoginAdapter(this)
        viewPager = binding.vpglogin
        viewPager.adapter = fragmentLoginAdapter
        val tabLayoutMediator = TabLayoutMediator(binding.tabLogin, viewPager){
                tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Đăng nhập user"
                }
                1 -> {
                    tab.text = "Đăng nhập admin"
                }
            }
        }
        tabLayoutMediator.attach();


    }


}

class FragmentLoginAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoginUserFragment()
            1 -> LoginAdminFragment()
            else -> {
                LoginUserFragment()
            }
        }
    }
}