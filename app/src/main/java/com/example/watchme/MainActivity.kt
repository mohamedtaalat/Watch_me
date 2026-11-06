package com.example.watchme

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.watchme.data.AuthRepository
import com.example.watchme.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.main.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView2) as? NavHostFragment
        val navController = navHostFragment?.navController ?: return

        // Sync navigation bar selection with current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homePage -> {
                    binding.bottomNavigation.menu.findItem(R.id.nav_home)?.isChecked = true
                }
                R.id.login, R.id.signup, R.id.forgetPassword -> {
                    // Deselect items when on auth screens
                    binding.bottomNavigation.menu.findItem(R.id.nav_home)?.isChecked = false
                    binding.bottomNavigation.menu.findItem(R.id.nav_logout)?.isChecked = false
                }
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (authRepository.isUserLoggedIn()) {
                        if (navController.currentDestination?.id != R.id.homePage) {
                            val navOptions = NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .build()
                            navController.navigate(R.id.homePage, null, navOptions)
                        }
                        true
                    } else {
                        Snackbar.make(
                            binding.bottomNavigation,
                            getString(R.string.msg_login_required),
                            Snackbar.LENGTH_LONG
                        )
                            .setAnchorView(binding.bottomNavigation)
                            .setAction("Log in") {
                                if (navController.currentDestination?.id != R.id.login) {
                                    navController.navigate(R.id.login)
                                }
                            }
                            .show()
                        false
                    }
                }

                R.id.nav_logout -> {
                    val wasLoggedIn = authRepository.isUserLoggedIn()
                    if (wasLoggedIn) {
                        authRepository.logout()
                        Snackbar.make(
                            binding.bottomNavigation,
                            getString(R.string.msg_logged_out),
                            Snackbar.LENGTH_SHORT
                        )
                            .setAnchorView(binding.bottomNavigation)
                            .show()
                    }

                    // Return to login screen and clear backstack
                    if (navController.currentDestination?.id != R.id.login) {
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.app_nav, true)
                            .build()
                        navController.navigate(R.id.login, null, navOptions)
                    }

                    binding.bottomNavigation.menu.findItem(R.id.nav_home)?.isChecked = false
                    false // Keep logout unselected as an action
                }

                else -> false
            }
        }
    }
}
