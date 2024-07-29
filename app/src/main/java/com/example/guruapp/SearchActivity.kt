package com.example.guruapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search) // 활동의 레이아웃 파일 이름을 "activity_search"로 설정

        val buttonBack: ImageButton = findViewById(R.id.homeButton)
        buttonBack.setOnClickListener {
            // 이전 액티비티로 돌아가기
            onBackPressed()  // 또는 finish()
        }
    }
}