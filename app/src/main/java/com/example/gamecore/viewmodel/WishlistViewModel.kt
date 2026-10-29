package com.example.gamecore.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamecore.data.local.WishlistEntry
import com.example.gamecore.data.model.GameData
import com.example.gamecore.data.model.storeGames
import com.example.gamecore.data.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WishlistUiState(
    val entries: List<WishlistEntry> = emptyList(),
    val games: List<GameData> = emptyList(),
    val isLoading: Boolean = true
)

class WishlistViewModel(private val repository: WishlistRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState: StateFlow<WishlistUiState> = _uiState.asStateFlow()
    private val pendingWishlistAdds = mutableSetOf<String>()

    init {
        // O Room emite uma nova lista sempre que a tabela lista_desejos muda.
        viewModelScope.launch {
            repository.buscarTodos()
                .catch { error ->
                    Log.e(TAG, "Não foi possível carregar a lista de desejos.", error)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { entries ->
                    _uiState.update {
                        it.copy(
                            entries = entries,
                            games = entries.mapNotNull { entry ->
                                storeGames.firstOrNull { game -> game.id == entry.gameId }
                            },
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun addToWishlist(game: GameData, isOwned: Boolean, onResult: (Boolean) -> Unit) {
        if (isOwned || _uiState.value.games.any { it.id == game.id } ||
            !pendingWishlistAdds.add(game.id)) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                val newId = repository.inserir(WishlistEntry(gameId = game.id))
                if (newId == -1L) {
                    Log.w(TAG, "O jogo ${game.id} já estava salvo na lista de desejos.")
                }
                onResult(newId > 0)
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível adicionar o jogo à lista de desejos.", error)
                onResult(false)
            } finally {
                pendingWishlistAdds.remove(game.id)
            }
        }
    }

    fun removeFromWishlist(game: GameData) {
        removeGames(listOf(game))
    }

    fun removeGames(games: List<GameData>) {
        if (games.isEmpty()) return
        val entries = _uiState.value.entries

        viewModelScope.launch {
            try {
                games.forEach { game ->
                    val entry = entries.firstOrNull { it.gameId == game.id }
                        ?: repository.buscarPorJogoId(game.id)
                    if (entry != null) {
                        repository.deletar(entry)
                    }
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível remover o jogo da lista de desejos.", error)
            }
        }
    }

    private companion object {
        const val TAG = "WishlistViewModel"
    }
}

class WishlistViewModelFactory(
    private val repository: WishlistRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WishlistViewModel::class.java)) {
            return WishlistViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
