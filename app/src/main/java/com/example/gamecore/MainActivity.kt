package com.example.gamecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gamecore.ui.theme.GameCoreTheme

object Routes {
    const val Login = "login"
    const val Register = "register"
    const val Store = "store"
    const val Cart = "cart"
    const val Library = "library"
    const val Profile = "profile"
    const val Settings = "settings"
    const val Wishlist = "wishlist"
    const val Reviews = "reviews/{gameId}"
    const val Game = "game/{gameId}"
    const val Review = "review/{reviewId}"

    fun game(id: String) = "game/$id"
    fun reviews(gameId: String) = "reviews/$gameId"
    fun review(id: Int) = "review/$id"
}

class MainActivity : ComponentActivity() {
    private val gameCoreViewModel: GameCoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameCoreViewModel.initializeDatabase(applicationContext)
        enableEdgeToEdge()
        setContent {
            GameCoreTheme {
                MainApp(gameCoreViewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: GameCoreViewModel) {
    val navController = rememberNavController()
    val cart = viewModel.cart
    val library = viewModel.library
    val wishlist = viewModel.wishlist
    val reviews = viewModel.reviews
    val destinations: (GameCoreDestination) -> Unit = { navController.navigateTopLevel(it.route()) }
    val allGames: () -> List<GameData> = { (storeGames + library + wishlist).distinctBy { it.id } }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = GameCoreColors.Background
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.Login,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable(Routes.Login) {
                LoginScreen(
                    onLogin = { email, password ->
                        viewModel.signIn(email, password) {
                            navController.navigate(Routes.Store) {
                                popUpTo(Routes.Login) { inclusive = true }
                            }
                        }
                    },
                    onOpenRegister = {
                        viewModel.clearAuthError()
                        navController.navigate(Routes.Register)
                    },
                    authError = viewModel.authError,
                    isSubmitting = viewModel.isAuthenticating
                )
            }
            composable(Routes.Register) {
                RegisterScreen(
                    onRegister = { name, username, email, password ->
                        viewModel.register(name, username, email, password) {
                            navController.navigate(Routes.Store) {
                                popUpTo(Routes.Register) { inclusive = true }
                            }
                        }
                    },
                    onOpenLogin = {
                        viewModel.clearAuthError()
                        navController.popBackStack()
                    },
                    authError = viewModel.authError,
                    isSubmitting = viewModel.isAuthenticating
                )
            }
            composable(Routes.Store) {
                StoreScreen(
                    cartCount = cart.size,
                    onOpenGame = { navController.navigate(Routes.game(it.id)) },
                    onOpenCart = { destinations(GameCoreDestination.CART) },
                    onDestinationClick = destinations,
                    onOpenProfile = { destinations(GameCoreDestination.PROFILE) }
                )
            }
            composable(Routes.Cart) {
                CartScreen(
                    cart = cart,
                    onRemoveItem = viewModel::removeFromCart,
                    onContinueShopping = { destinations(GameCoreDestination.STORE) },
                    onCheckout = {
                        viewModel.checkout()
                        destinations(GameCoreDestination.LIBRARY)
                    },
                    onDestinationClick = destinations,
                    onOpenProfile = { destinations(GameCoreDestination.PROFILE) }
                )
            }
            composable(Routes.Library) {
                LibraryScreen(
                    games = library,
                    cartCount = cart.size,
                    onOpenGame = { navController.navigate(Routes.game(it.id)) },
                    onDestinationClick = destinations,
                    onProfile = { destinations(GameCoreDestination.PROFILE) }
                )
            }
            composable(Routes.Profile) {
                ProfileScreen(
                    currentUser = viewModel.currentUser,
                    library = library,
                    cartCount = cart.size,
                    onOpenGame = { navController.navigate(Routes.game(it.id)) },
                    onOpenLibrary = { destinations(GameCoreDestination.LIBRARY) },
                    onOpenSettings = { navController.navigate(Routes.Settings) },
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Settings) {
                SettingsScreen(
                    currentUser = viewModel.currentUser,
                    cartCount = cart.size,
                    gamesCount = library.size,
                    wishlistCount = wishlist.count { wish -> !library.any { it.id == wish.id } },
                    onBack = { navController.popBackStack() },
                    onOpenWishlist = { navController.navigate(Routes.Wishlist) },
                    onLogout = {
                        viewModel.signOut()
                        navController.navigate(Routes.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Wishlist) {
                LaunchedEffect(Unit) {
                    viewModel.loadWishlist()
                }
                WishlistScreen(
                    games = wishlist.filterNot { wish -> library.any { it.id == wish.id } },
                    cartCount = cart.size,
                    onOpenGame = { navController.navigate(Routes.game(it.id)) },
                    onRemove = viewModel::removeFromWishlist,
                    onBack = { navController.popBackStack() },
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Reviews) { entry ->
                val gameId = entry.arguments?.getString("gameId") ?: storeGames.first().id
                LaunchedEffect(gameId) {
                    viewModel.loadReviews()
                }
                val game = allGames().firstOrNull { it.id == gameId } ?: storeGames.first()
                val gameReviews = reviews.filter { it.gameId == game.id }
                ReviewsScreen(
                    game = game,
                    reviews = gameReviews,
                    cartCount = cart.size,
                    isOwned = library.any { it.id == game.id },
                    onOpen = { navController.navigate(Routes.review(it.id)) },
                    onRemove = viewModel::removeReview,
                    onAdd = { rating, text -> viewModel.addReview(game.id, rating, text) },
                    currentUser = viewModel.currentUser,
                    onBack = { navController.popBackStack() },
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Game) { entry ->
                val game = allGames().firstOrNull { it.id == entry.arguments?.getString("gameId") } ?: storeGames.first()
                GameScreen(
                    game = game,
                    cartCount = cart.size,
                    onBack = { navController.popBackStack() },
                    onOpenCart = { destinations(GameCoreDestination.CART) },
                    onAddToCart = { viewModel.addToCart(game) },
                    onAddToWishlist = { onResult -> viewModel.addToWishlist(game, onResult) },
                    onOpenReviews = { navController.navigate(Routes.reviews(game.id)) },
                    isOwned = library.any { it.id == game.id },
                    isInWishlist = wishlist.any { it.id == game.id },
                    reviews = reviews,
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Review) { entry ->
                val reviewId = entry.arguments?.getString("reviewId")?.toIntOrNull()
                LaunchedEffect(reviewId) {
                    reviewId?.let(viewModel::loadReviewForDetail)
                }
                val review = viewModel.reviewForDetail
                if (review != null && review.id == reviewId) {
                    ReviewDetailScreen(
                        review = review,
                        cartCount = cart.size,
                        onSave = viewModel::updateReview,
                        onBack = { navController.popBackStack() },
                        onDestinationClick = destinations
                    )
                }
            }
        }
    }
}

private fun GameCoreDestination.route() = when (this) {
    GameCoreDestination.STORE -> Routes.Store
    GameCoreDestination.LIBRARY -> Routes.Library
    GameCoreDestination.CART -> Routes.Cart
    GameCoreDestination.PROFILE -> Routes.Profile
}

private fun NavHostController.navigateTopLevel(route: String) {
    if (currentDestination?.route != route) {
        val popped = popBackStack(route, inclusive = false)
        if (!popped) {
            navigate(route) {
                popUpTo(graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}
