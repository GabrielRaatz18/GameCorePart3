package com.example.gamecore

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamecore.data.AppDatabase
import com.example.gamecore.data.WishlistEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Centraliza o estado da interface e sincroniza as listas persistentes com o Room.
 * O carrinho e a sessão continuam sendo estados temporários; avaliações e lista de
 * desejos são carregadas novamente do banco sempre que suas telas são abertas.
 */
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

    /** A avaliação exibida no detalhe é buscada pelo id recebido na navegação. */
    var reviewForDetail by mutableStateOf<ReviewData?>(null)
        private set

    var currentUser by mutableStateOf(UserData("guest", "Matheus", "MatheusP", "matheus@gmail.com"))
        private set

    /**
     * É chamado pela Activity uma única vez. A construção do banco não faz I/O;
     * as leituras e escritas abaixo sempre são executadas em Dispatchers.IO.
     */
    fun initializeDatabase(context: Context) {
        if (database != null) return

        database = AppDatabase.getInstance(context.applicationContext)
        loadWishlist()
        loadReviews()
    }

    fun signIn(email: String) {
        val username = email.substringBefore('@').ifBlank { "jogador" }
        currentUser = UserData(username.lowercase(), username, username, email.trim())
        loadWishlist()
        loadReviews()
    }

    fun register(name: String, username: String, email: String) {
        currentUser = UserData(username.lowercase(), name.trim(), username.trim(), email.trim())
        loadWishlist()
        loadReviews()
    }

    fun signOut() {
        cart = emptyList()
        library = emptyList()
        wishlist = emptyList()
        wishlistEntries = emptyList()
        reviewForDetail = null
        currentUser = UserData("guest", "Matheus", "MatheusP", "matheus@gmail.com")
    }

    fun addToCart(game: GameData): Boolean = if (cart.any { it.id == game.id }) {
        false
    } else {
        cart = cart + game
        true
    }

    /** Insere o jogo na lista de desejos persistida no Room. */
    fun addToWishlist(game: GameData): Boolean {
        if (library.any { it.id == game.id } || wishlist.any { it.id == game.id }) return false

        // Atualização otimista para a ação aparecer imediatamente na tela.
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

    /** Remove a mesma linha que aparece na UI da tabela lista_desejos. */
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

    /** Lê a tabela de avaliações para alimentar a LazyColumn da tela. */
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

    /** Lê a tabela de lista de desejos e relaciona cada id ao catálogo estático. */
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

    /** Insere uma nova avaliação no Room e recebe o id autogerado. */
    fun addReview(gameId: String, rating: Int, text: String) {
        val newReview = ReviewData(
            gameId = gameId,
            author = currentUser.name,
            rating = rating.coerceIn(1, 5),
            text = text.trim()
        )
        val reviewDao = database?.reviewDao()

        // Mantém os testes locais e previews utilizáveis sem um Context Android.
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

    /** Atualiza a linha editada na tela de detalhe. */
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

    /** Exclui a avaliação persistida, além de removê-la da lista apresentada. */
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

    /**
     * Cumpre o fluxo de detalhe obrigatório: a avaliação não é passada pronta pela
     * navegação; ela é consultada pelo id diretamente no DAO.
     */
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
    }
}
