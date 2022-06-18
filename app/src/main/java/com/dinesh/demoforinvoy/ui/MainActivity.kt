package com.dinesh.demoforinvoy.ui

import android.os.Bundle
import com.dinesh.demoforinvoy.R
import com.google.firebase.FirebaseApp
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
    }
}