package com.example.guruapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import java.util.regex.Pattern

class LoginFragment : AppCompatActivity() {
    lateinit var btnLogin: Button
    lateinit var editTextId: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnRegister: Button
    var DB:DBHelper?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DB = DBHelper(this)

        btnLogin = findViewById(R.id.btnLogin)
        editTextId = findViewById(R.id.editTextId)
        editTextPassword = findViewById(R.id.editTextPassword)
        btnRegister = findViewById(R.id.btnRegister)

        // 로그인 버튼 클릭
        btnLogin!!.setOnClickListener {
            val user = editTextId!!.text.toString()
            val pass = editTextPassword!!.text.toString()

            // 빈칸 제출시 Toast
            if (user == "" || pass == "") {
                Toast.makeText(this@LoginFragment, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                val checkUserpass = DB!!.checkUserpass(user, pass)
                // id 와 password 일치시
                if (checkUserpass == true) {
                    Toast.makeText(this@LoginFragment, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this@LoginFragment, "아이디 또는 비밀번호가 맞지 않아요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // 회원가입 버튼 클릭
        btnRegister.setOnClickListener {
            val loginIntent = Intent(this, RegisterActivity::class.java)
            startActivity(loginIntent)
        }
    }
}
