package com.example.gamecore.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gamecore.data.model.GameData
import com.example.gamecore.data.model.storeGames
import com.example.gamecore.data.repository.ReviewRepository
import com.example.gamecore.data.repository.UsuarioRepository
import com.example.gamecore.data.repository.WishlistRepository
import com.example.gamecore.viewmodel.AuthViewModel
import com.example.gamecore.viewmodel.AuthViewModelFactory
import com.example.gamecore.viewmodel.CartViewModel
import com.example.gamecore.viewmodel.ReviewViewModel
import com.example.gamecore.viewmodel.ReviewViewModelFactory
import com.example.gamecore.viewmodel.WishlistViewModel
import com.example.gamecore.viewmodel.WishlistViewModelFactory

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

@Composable
fun MainApp(
    usuarioRepository: UsuarioRepository,
    wishlistRepository: WishlistRepository,
    reviewRepository: ReviewRepository
) {
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(usuarioRepository))
    val wishlistViewModel: WishlistViewModel = viewModel(factory = WishlistViewModelFactory(wishlistRepository))
    val reviewViewModel: ReviewViewModel = viewModel(factory = ReviewViewModelFactory(reviewRepository))
    val cartViewModel: CartViewModel = viewModel()

    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val wishlistState by wishlistViewModel.uiState.collectAsStateWithLifecycle()
    val reviewState by reviewViewModel.uiState.collectAsStateWithLifecycle()
    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val cart = cartState.cart
    val library = cartState.library
    val wishlist = wishlistState.games
    val reviews = reviewState.reviews
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
                    email = authState.loginEmail,
                    password = authState.loginPassword,
                    onEmailChange = authViewModel::onLoginEmailChange,
                    onPasswordChange = authViewModel::onLoginPasswordChange,
                    onLogin = { email, password ->
                        authViewModel.signIn(email, password) {
                            navController.navigate(Routes.Store) {
                                popUpTo(Routes.Login) { inclusive = true }
                            }
                        }
                    },
                    onOpenRegister = {
                        authViewModel.clearAuthError()
                        navController.navigate(Routes.Register)
                    },
                    authError = authState.authError,
                    isSubmitting = authState.isAuthenticating
                )
            }
            composable(Routes.Register) {
                RegisterScreen(
                    name = authState.registerName,
                    username = authState.registerUsername,
                    email = authState.registerEmail,
                    password = authState.registerPassword,
                    onNameChange = authViewModel::onRegisterNameChange,
                    onUsernameChange = authViewModel::onRegisterUsernameChange,
                    onEmailChange = authViewModel::onRegisterEmailChange,
                    onPasswordChange = authViewModel::onRegisterPasswordChange,
                    onRegister = { name, username, email, password ->
                        authViewModel.register(name, username, email, password) {
                            navController.navigate(Routes.Store) {
                                popUpTo(Routes.Register) { inclusive = true }
                            }
                        }
                    },
                    onOpenLogin = {
                        authViewModel.clearAuthError()
                        navController.popBackStack()
                    },
                    authError = authState.authError,
                    isSubmitting = authState.isAuthenticating
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
                    onRemoveItem = cartViewModel::removeFromCart,
                    onContinueShopping = { destinations(GameCoreDestination.STORE) },
                    onCheckout = {
                        val purchasedGames = cartViewModel.checkout()
                        wishlistViewModel.removeGames(purchasedGames)
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
                    currentUser = authState.currentUser,
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
                    currentUser = authState.currentUser,
                    cartCount = cart.size,
                    gamesCount = library.size,
                    wishlistCount = wishlist.count { wish -> !library.any { it.id == wish.id } },
                    onBack = { navController.popBackStack() },
                    onOpenWishlist = { navController.navigate(Routes.Wishlist) },
                    onLogout = {
                        authViewModel.signOut()
                        cartViewModel.clear()
                        navController.navigate(Routes.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Wishlist) {
                WishlistScreen(
                    games = wishlist.filterNot { wish -> library.any { it.id == wish.id } },
                    cartCount = cart.size,
                    onOpenGame = { navController.navigate(Routes.game(it.id)) },
                    onRemove = wishlistViewModel::removeFromWishlist,
                    onBack = { navController.popBackStack() },
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Reviews) { entry ->
                val gameId = entry.arguments?.getString("gameId") ?: storeGames.first().id
                val game = allGames().firstOrNull { it.id == gameId } ?: storeGames.first()
                val gameReviews = reviews.filter { it.gameId == game.id }
                ReviewsScreen(
                    game = game,
                    reviews = gameReviews,
                    cartCount = cart.size,
                    isOwned = library.any { it.id == game.id },
                    onOpen = { navController.navigate(Routes.review(it.id)) },
                    onRemove = reviewViewModel::removeReview,
                    onAdd = { rating, text ->
                        reviewViewModel.addReview(game.id, authState.currentUser.name, rating, text)
                    },
                    currentUser = authState.currentUser,
                    onBack = { navController.popBackStack() },
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Game) { entry ->
                val game = allGames().firstOrNull { it.id == entry.arguments?.getString("gameId") } ?: storeGames.first()
                val isOwned = library.any { it.id == game.id }
                GameScreen(
                    game = game,
                    cartCount = cart.size,
                    onBack = { navController.popBackStack() },
                    onOpenCart = { destinations(GameCoreDestination.CART) },
                    onAddToCart = { cartViewModel.addToCart(game) },
                    onAddToWishlist = { onResult -> wishlistViewModel.addToWishlist(game, isOwned, onResult) },
                    onOpenReviews = { navController.navigate(Routes.reviews(game.id)) },
                    isOwned = isOwned,
                    isInWishlist = wishlist.any { it.id == game.id },
                    reviews = reviews,
                    onDestinationClick = destinations
                )
            }
            composable(Routes.Review) { entry ->
                val reviewId = entry.arguments?.getString("reviewId")?.toIntOrNull()
                LaunchedEffect(reviewId) {
                    reviewId?.let(reviewViewModel::loadReviewForDetail)
                }
                val review = reviewState.reviewForDetail
                if (review != null && review.id == reviewId) {
                    ReviewDetailScreen(
                        review = review,
                        cartCount = cart.size,
                        onSave = reviewViewModel::updateReview,
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
