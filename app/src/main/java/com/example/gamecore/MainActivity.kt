package com.example.gamecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gamecore.data.local.AppDatabase
import com.example.gamecore.data.repository.ReviewRepository
import com.example.gamecore.data.repository.UsuarioRepository
import com.example.gamecore.data.repository.WishlistRepository
import com.example.gamecore.ui.MainApp
import com.example.gamecore.ui.theme.GameCoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getInstance(applicationContext)
        val usuarioRepository = UsuarioRepository(database.usuarioDao())
        val wishlistRepository = WishlistRepository(database.wishlistDao())
        val reviewRepository = ReviewRepository(database.reviewDao())

        enableEdgeToEdge()
        setContent {
            GameCoreTheme {
                MainApp(
                    usuarioRepository = usuarioRepository,
                    wishlistRepository = wishlistRepository,
                    reviewRepository = reviewRepository
                )
            }
        }
    }
}
