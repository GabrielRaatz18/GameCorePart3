package com.example.gamecore

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
        val viewModel = GameCoreViewModel()
        val game = storeGames.first()

        assertTrue(viewModel.addToCart(game))
        assertFalse(viewModel.addToCart(game))

        viewModel.checkout()

        assertTrue(viewModel.cart.isEmpty())
        assertEquals(listOf(game), viewModel.library)
    }

    @Test
    fun `wishlist prevents games already owned`() {
        val viewModel = GameCoreViewModel()
        val game = storeGames.first()

        assertTrue(viewModel.addToCart(game))
        viewModel.checkout()

        var added: Boolean? = null
        viewModel.addToWishlist(game) { added = it }
        assertFalse(added!!)
    }

    @Test
    fun `GAME10 coupon gives ten percent discount`() {
        assertEquals(0.10, couponDiscountRate(" game10 "), 0.0)
        assertEquals(0.0, couponDiscountRate("INVALID"), 0.0)
    }
}
