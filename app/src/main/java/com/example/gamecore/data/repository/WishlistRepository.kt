package com.example.gamecore.data.repository

import com.example.gamecore.data.local.WishlistDao
import com.example.gamecore.data.local.WishlistEntry
import kotlinx.coroutines.flow.Flow

class WishlistRepository(private val wishlistDao: WishlistDao) {
    fun buscarTodos(): Flow<List<WishlistEntry>> = wishlistDao.buscarTodos()

    suspend fun buscarPorJogoId(gameId: String): WishlistEntry? = wishlistDao.buscarPorJogoId(gameId)

    suspend fun inserir(item: WishlistEntry): Long = wishlistDao.inserir(item)

    suspend fun atualizar(item: WishlistEntry): Int = wishlistDao.atualizar(item)

    suspend fun deletar(item: WishlistEntry): Int = wishlistDao.deletar(item)
}
