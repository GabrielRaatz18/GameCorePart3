package com.example.gamecore

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamecore.data.AppDatabase
import com.example.gamecore.data.Usuario
import com.example.gamecore.data.WishlistEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class GameCoreViewModel : ViewModel() {
    private var database: AppDatabase? = null
    private var wishlistEntries: List<WishlistEntry> = emptyList()

    var cart by mutableStateOf<List<GameData>>(emptyList())
        private set

    var library by mutableStateOf<List<GameData>>(emptyList())
        private set

    var wishlist by mutableStateOf<List<GameData>>(emptyList())
        private set

    var reviews by mutableStateOf<List<ReviewData>>(emptyList())
        private set

    var reviewForDetail by mutableStateOf<ReviewData?>(null)
        private set

    var currentUser by mutableStateOf(UserData("guest", "Matheus", "MatheusP", "matheus@gmail.com"))
        private set

    var authError by mutableStateOf<String?>(null)
        private set

    var isAuthenticating by mutableStateOf(false)
        private set

    fun initializeDatabase(context: Context) {
        if (database != null) return

        database = AppDatabase.getInstance(context.applicationContext)
        seedDemoAccount()
        loadWishlist()
        loadReviews()
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        val usuarioDao = database?.usuarioDao()
        if (usuarioDao == null) {
            authError = "O banco de usuários ainda não está disponível. Tente novamente."
            return
        }

        authError = null
        isAuthenticating = true
        val normalizedEmail = normalizeEmail(email)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuario = usuarioDao.buscarLogin(normalizedEmail, password)
                withContext(Dispatchers.Main) {
                    isAuthenticating = false
                    if (usuario != null) {
                        currentUser = usuario.toUserData()
                        refreshPersistentLists()
                        onSuccess()
                    } else {
                        authError = "E-mail ou senha incorretos."
                    }
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível realizar o login.", error)
                withContext(Dispatchers.Main) {
                    isAuthenticating = false
                    authError = "Não foi possível entrar agora. Tente novamente."
                }
            }
        }
    }

    fun register(
        name: String,
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        val usuarioDao = database?.usuarioDao()
        if (usuarioDao == null) {
            authError = "O banco de usuários ainda não está disponível. Tente novamente."
            return
        }

        authError = null
        isAuthenticating = true
        val normalizedUsername = normalizeUsername(username)
        val normalizedEmail = normalizeEmail(email)
        val cleanName = name.trim()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                when {
                    usuarioDao.buscarPorEmail(normalizedEmail) != null -> {
                        publishAuthError("Já existe uma conta com este e-mail.")
                    }

                    usuarioDao.buscarPorUsuario(normalizedUsername) != null -> {
                        publishAuthError("Este nome de usuário já está em uso.")
                    }

                    else -> {
                        val usuario = Usuario(
                            nome = cleanName,
                            usuario = normalizedUsername,
                            email = normalizedEmail,
                            senha = password
                        )
                        val id = usuarioDao.inserir(usuario)
                        withContext(Dispatchers.Main) {
                            isAuthenticating = false
                            if (id > 0) {
                                currentUser = usuario.copy(id = id.toInt()).toUserData()
                                refreshPersistentLists()
                                onSuccess()
                            } else {
                                authError = "Não foi possível criar a conta. Tente outro e-mail ou usuário."
                            }
                        }
                    }
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível cadastrar o usuário.", error)
                withContext(Dispatchers.Main) {
                    isAuthenticating = false
                    authError = "Não foi possível criar a conta. Tente novamente."
                }
            }
        }
    }

    fun clearAuthError() {
        authError = null
    }

    fun signOut() {
        cart = emptyList()
        library = emptyList()
        wishlist = emptyList()
        wishlistEntries = emptyList()
        reviewForDetail = null
        currentUser = UserData("guest", "Matheus", "MatheusP", "matheus@gmail.com")
        authError = null
        isAuthenticating = false
    }

    fun addToCart(game: GameData): Boolean = if (cart.any { it.id == game.id }) {
        false
    } else {
        cart = cart + game
        true
    }

    fun addToWishlist(game: GameData): Boolean {
        if (library.any { it.id == game.id } || wishlist.any { it.id == game.id }) return false

        wishlist = wishlist + game

        val wishlistDao = database?.wishlistDao() ?: return true
        val newEntry = WishlistEntry(gameId = game.id)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newId = wishlistDao.inserir(newEntry)
                val entries = wishlistDao.buscarTodos()
                withContext(Dispatchers.Main) {
                    updateWishlist(entries)
                    if (newId == -1L) {
                        Log.w(TAG, "O jogo ${game.id} já estava salvo na lista de desejos.")
                    }
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível adicionar o jogo à lista de desejos.", error)
                reloadWishlistAfterError(wishlistDao)
            }
        }
        return true
    }

    fun removeFromCart(game: GameData) {
        cart = cart.filterNot { it.id == game.id }
    }

    fun removeFromWishlist(game: GameData) {
        val entry = wishlistEntries.firstOrNull { it.gameId == game.id }
        wishlist = wishlist.filterNot { it.id == game.id }
        wishlistEntries = wishlistEntries.filterNot { it.gameId == game.id }

        val wishlistDao = database?.wishlistDao() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val persistedEntry = entry ?: wishlistDao.buscarPorJogoId(game.id)
                if (persistedEntry != null) {
                    wishlistDao.deletar(persistedEntry)
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível remover o jogo da lista de desejos.", error)
                reloadWishlistAfterError(wishlistDao)
            }
        }
    }

    fun checkout() {
        val purchasedGames = cart
        val entriesToRemove = wishlistEntries.filter { entry ->
            purchasedGames.any { game -> game.id == entry.gameId }
        }

        library = (library + purchasedGames).distinctBy { it.id }
        wishlist = wishlist.filterNot { wish -> purchasedGames.any { it.id == wish.id } }
        wishlistEntries = wishlistEntries.filterNot { entry ->
            purchasedGames.any { game -> game.id == entry.gameId }
        }
        cart = emptyList()

        val wishlistDao = database?.wishlistDao() ?: return
        if (purchasedGames.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val persistedEntries = entriesToRemove.ifEmpty {
                    purchasedGames.mapNotNull { game -> wishlistDao.buscarPorJogoId(game.id) }
                }
                persistedEntries.forEach { entry -> wishlistDao.deletar(entry) }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível atualizar a lista de desejos após a compra.", error)
                reloadWishlistAfterError(wishlistDao)
            }
        }
    }

    fun loadReviews() {
        val reviewDao = database?.reviewDao() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val savedReviews = reviewDao.buscarTodos()
                withContext(Dispatchers.Main) {
                    reviews = savedReviews
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível carregar as avaliações.", error)
            }
        }
    }

    fun loadWishlist() {
        val wishlistDao = database?.wishlistDao() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val entries = wishlistDao.buscarTodos()
                withContext(Dispatchers.Main) {
                    updateWishlist(entries)
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível carregar a lista de desejos.", error)
            }
        }
    }

    fun addReview(gameId: String, rating: Int, text: String) {
        val newReview = ReviewData(
            gameId = gameId,
            author = currentUser.name,
            rating = rating.coerceIn(1, 5),
            text = text.trim()
        )
        val reviewDao = database?.reviewDao()

        if (reviewDao == null) {
            val localId = (reviews.minOfOrNull { it.id } ?: 0) - 1
            reviews = reviews + newReview.copy(id = localId)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val id = reviewDao.inserir(newReview).toInt()
                withContext(Dispatchers.Main) {
                    reviews = reviews + newReview.copy(id = id)
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível publicar a avaliação.", error)
            }
        }
    }

    fun updateReview(updated: ReviewData) {
        val previousReviews = reviews
        reviews = reviews.map { review -> if (review.id == updated.id) updated else review }
        if (reviewForDetail?.id == updated.id) reviewForDetail = updated

        val reviewDao = database?.reviewDao() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                reviewDao.atualizar(updated)
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível atualizar a avaliação.", error)
                withContext(Dispatchers.Main) {
                    reviews = previousReviews
                }
            }
        }
    }

    fun removeReview(review: ReviewData) {
        val previousReviews = reviews
        reviews = reviews.filterNot { it.id == review.id }
        if (reviewForDetail?.id == review.id) reviewForDetail = null

        val reviewDao = database?.reviewDao() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                reviewDao.deletar(review)
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível remover a avaliação.", error)
                withContext(Dispatchers.Main) {
                    reviews = previousReviews
                }
            }
        }
    }

    fun loadReviewForDetail(id: Int) {
        reviewForDetail = null
        val reviewDao = database?.reviewDao()
        if (reviewDao == null) {
            reviewForDetail = reviews.firstOrNull { it.id == id }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val savedReview = reviewDao.buscarPorId(id)
                withContext(Dispatchers.Main) {
                    reviewForDetail = savedReview
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível carregar o detalhe da avaliação.", error)
            }
        }
    }

    private fun seedDemoAccount() {
        val usuarioDao = database?.usuarioDao() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (usuarioDao.buscarPorEmail(DEMO_EMAIL) == null) {
                    usuarioDao.inserir(
                        Usuario(
                            nome = "Matheus",
                            usuario = "matheusp",
                            email = DEMO_EMAIL,
                            senha = DEMO_PASSWORD
                        )
                    )
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível preparar a conta de demonstração.", error)
            }
        }
    }

    private fun refreshPersistentLists() {
        loadWishlist()
        loadReviews()
    }

    private suspend fun publishAuthError(message: String) {
        withContext(Dispatchers.Main) {
            isAuthenticating = false
            authError = message
        }
    }

    private fun normalizeEmail(value: String): String = value.trim().lowercase(Locale.ROOT)

    private fun normalizeUsername(value: String): String = value
        .trim()
        .removePrefix("@")
        .lowercase(Locale.ROOT)

    private fun Usuario.toUserData(): UserData = UserData(
        id = id.toString(),
        name = nome,
        username = usuario,
        email = email
    )

    private fun updateWishlist(entries: List<WishlistEntry>) {
        wishlistEntries = entries
        wishlist = entries.mapNotNull { entry ->
            storeGames.firstOrNull { game -> game.id == entry.gameId }
        }
    }

    private suspend fun reloadWishlistAfterError(wishlistDao: com.example.gamecore.data.WishlistDao) {
        try {
            val entries = wishlistDao.buscarTodos()
            withContext(Dispatchers.Main) {
                updateWishlist(entries)
            }
        } catch (error: Exception) {
            Log.e(TAG, "Não foi possível restaurar a lista de desejos.", error)
        }
    }

    private companion object {
        const val TAG = "GameCoreViewModel"
        const val DEMO_EMAIL = "matheus@gmail.com"
        const val DEMO_PASSWORD = "12345678"
    }
}
