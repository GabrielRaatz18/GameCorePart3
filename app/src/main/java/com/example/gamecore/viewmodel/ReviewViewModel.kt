package com.example.gamecore.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamecore.data.local.ReviewData
import com.example.gamecore.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewUiState(
    val reviews: List<ReviewData> = emptyList(),
    val reviewForDetail: ReviewData? = null,
    val isLoading: Boolean = true
)

class ReviewViewModel(private val repository: ReviewRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    init {
        // O Room emite uma nova lista sempre que a tabela avaliacoes muda.
        viewModelScope.launch {
            repository.buscarTodos()
                .catch { error ->
                    Log.e(TAG, "Não foi possível carregar as avaliações.", error)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .collect { reviews ->
                    _uiState.update { it.copy(reviews = reviews, isLoading = false) }
                }
        }
    }

    fun addReview(gameId: String, author: String, rating: Int, text: String) {
        val newReview = ReviewData(
            gameId = gameId,
            author = author,
            rating = rating.coerceIn(1, 5),
            text = text.trim()
        )

        viewModelScope.launch {
            try {
                repository.inserir(newReview)
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível publicar a avaliação.", error)
            }
        }
    }

    fun updateReview(updated: ReviewData) {
        if (_uiState.value.reviewForDetail?.id == updated.id) {
            _uiState.update { it.copy(reviewForDetail = updated) }
        }

        viewModelScope.launch {
            try {
                repository.atualizar(updated)
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível atualizar a avaliação.", error)
            }
        }
    }

    fun removeReview(review: ReviewData) {
        if (_uiState.value.reviewForDetail?.id == review.id) {
            _uiState.update { it.copy(reviewForDetail = null) }
        }

        viewModelScope.launch {
            try {
                repository.deletar(review)
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível remover a avaliação.", error)
            }
        }
    }

    fun loadReviewForDetail(id: Int) {
        _uiState.update { it.copy(reviewForDetail = null) }

        viewModelScope.launch {
            try {
                val savedReview = repository.buscarPorId(id)
                _uiState.update { it.copy(reviewForDetail = savedReview) }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível carregar o detalhe da avaliação.", error)
            }
        }
    }

    private companion object {
        const val TAG = "ReviewViewModel"
    }
}

class ReviewViewModelFactory(
    private val repository: ReviewRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            return ReviewViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
