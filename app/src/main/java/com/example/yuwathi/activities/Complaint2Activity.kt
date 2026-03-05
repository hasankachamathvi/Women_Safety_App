package com.example.yuwathi.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yuwathi.R

class Complaint2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint2)

        val spinnerContact = findViewById<Spinner>(R.id.spinner_contact_pref)
        val contactOptions = arrayOf("In-app notification only", "Phone call", "SMS", "Do not contact")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contactOptions)
        spinnerContact.adapter = adapter

        val btnSubmit = findViewById<Button>(R.id.btn_submit)
        btnSubmit.setOnClickListener {
            Toast.makeText(this, "Complaint Submitted Successfully!", Toast.LENGTH_LONG).show()
            // Here you would normally send data to a server
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}
