package com.example.guruapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.guruapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment()

        // 메인 화면 초기 실행 시 홈화면으로 설정
        if (savedInstanceState == null) {
            binding.menuNavi.selectedItemId = R.id.navi_home
        }
    }

    private fun setFragment() {
        binding.menuNavi.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navi_home -> {
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction
                        .replace(R.id.mainFrameLayout, HomeFragment())
                        .commit()
                    true
                }
                R.id.navi_mission -> {
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction
                        .replace(R.id.mainFrameLayout, MissionFragment())
                        .commit()
                    true
                }
                R.id.navi_info -> {
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction
                        .replace(R.id.mainFrameLayout, MypageFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
