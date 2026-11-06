package com.example.watchme

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.watchme.databinding.SplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: SplashScreenBinding
    private val splashDelay = 3000L
    private val handler = Handler(Looper.getMainLooper())

    private val navigateToMain = Runnable {
        if (!isFinishing && !isDestroyed) {
            val intent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(intent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(
                    OVERRIDE_TRANSITION_OPEN,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
            } else {
                @Suppress("DEPRECATION")
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }

            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startSplashAnimations()

        // Keep the splash screen visible for 3 seconds before navigating
        handler.postDelayed(navigateToMain, splashDelay)
    }

    private fun startSplashAnimations() {
        val logoAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_logo_anim)
        val textAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_slide_up)
        val progressAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in)

        binding.logoContainer.startAnimation(logoAnimation)
        binding.appTitle.startAnimation(textAnimation)
        binding.appTagline.startAnimation(textAnimation)
        binding.splashProgress.startAnimation(progressAnimation)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Prevent leaks or executing when activity is destroyed
        handler.removeCallbacks(navigateToMain)
    }
}
