// MainActivity.kt
package com.intel.medicine

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.intel.medicine.ui.home.HomeActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // HomeActivity로 이동
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}