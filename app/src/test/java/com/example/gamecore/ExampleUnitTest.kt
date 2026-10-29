package com.example.gamecore

import com.example.gamecore.data.model.storeGames
import com.example.gamecore.ui.couponDiscountRate
import com.example.gamecore.viewmodel.CartViewModel
import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun `cart prevents duplicate games and checkout moves game to library`() {
        val viewModel = CartViewModel()
        val game = storeGames.first()

        assertTrue(viewModel.addToCart(game))
        assertFalse(viewModel.addToCart(game))

        viewModel.checkout()

        assertTrue(viewModel.uiState.value.cart.isEmpty())
        assertEquals(listOf(game), viewModel.uiState.value.library)
    }

    @Test
    fun `checkout returns purchased games so they leave the wishlist`() {
        val viewModel = CartViewModel()
        val game = storeGames.first()

        assertTrue(viewModel.addToCart(game))

        assertEquals(listOf(game), viewModel.checkout())
        assertTrue(viewModel.checkout().isEmpty())
    }

    @Test
    fun `GAME10 coupon gives ten percent discount`() {
        assertEquals(0.10, couponDiscountRate(" game10 "), 0.0)
        assertEquals(0.0, couponDiscountRate("INVALID"), 0.0)
    }
}
