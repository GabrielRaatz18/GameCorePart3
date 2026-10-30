package com.example.gamecore.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gamecore.data.model.GameData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private val paymentMethods = listOf("Cartão de crédito", "PIX", "Boleto")

internal fun couponDiscountRate(code: String): Double = when (code.trim().uppercase()) {
    "GAME10" -> 0.10
    else -> 0.0
}

// Carrinho e biblioteca ficam só em memória, como já era no Trabalho 3.
data class CartUiState(
    val cart: List<GameData> = emptyList(),
    val library: List<GameData> = emptyList(),
    val couponCode: String = "",
    val paymentMethodIndex: Int = 0
) {
    val discountRate: Double get() = couponDiscountRate(couponCode)
    val paymentMethod: String get() = paymentMethods[paymentMethodIndex]
}

class CartViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun onCouponCodeChange(value: String) = _uiState.update { it.copy(couponCode = value) }

    fun nextPaymentMethod() = _uiState.update {
        it.copy(paymentMethodIndex = (it.paymentMethodIndex + 1) % paymentMethods.size)
    }

    fun resetCheckoutForm() {
        _uiState.update { it.copy(couponCode = "", paymentMethodIndex = 0) }
    }

    fun addToCart(game: GameData): Boolean {
        if (_uiState.value.cart.any { it.id == game.id }) return false
        _uiState.update { it.copy(cart = it.cart + game) }
        return true
    }

    fun removeFromCart(game: GameData) {
        _uiState.update { state -> state.copy(cart = state.cart.filterNot { it.id == game.id }) }
    }

    /** Move os jogos do carrinho para a biblioteca e devolve o que foi comprado. */
    fun checkout(): List<GameData> {
        val purchasedGames = _uiState.value.cart
        _uiState.update { state ->
            CartUiState(library = (state.library + purchasedGames).distinctBy { it.id })
        }
        return purchasedGames
    }

    fun clear() {
        _uiState.value = CartUiState()
    }
}
