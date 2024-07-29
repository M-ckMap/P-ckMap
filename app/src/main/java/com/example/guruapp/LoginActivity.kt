package com.example.guruapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guruapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Auth 초기화
        auth = FirebaseAuth.getInstance()

        // 버튼 클릭 리스너 설정
        binding.emailLoginBtn.setOnClickListener {
            emailLogin()
        }
    }

    private fun emailLogin() {
        val email = binding.emailEdt.text.toString()
        val password = binding.passwordEdt.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            signinAndSignup(email, password)
        }
    }

    private fun signinAndSignup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 이메일로 성공적으로 계정을 만들었을 경우
                    moveMainPage(task.result?.user)
                } else if (task.exception?.message != null) {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    // 이메일 계정이 이미 존재하는 경우
                    signinEmail(email, password)
                }
            }
    }

    private fun signinEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    moveMainPage(task.result?.user)
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Finish this activity
        }
    }

    //로그인 유지하는 코드
    /*override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            moveMainPage(auth.currentUser)
        }
    }*/
}
