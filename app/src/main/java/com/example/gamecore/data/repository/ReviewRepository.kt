package com.example.gamecore.data.repository

import com.example.gamecore.data.local.ReviewDao
import com.example.gamecore.data.local.ReviewData
import kotlinx.coroutines.flow.Flow

class ReviewRepository(private val reviewDao: ReviewDao) {
    fun buscarTodos(): Flow<List<ReviewData>> = reviewDao.buscarTodos()

    suspend fun buscarPorId(id: Int): ReviewData? = reviewDao.buscarPorId(id)

    suspend fun inserir(avaliacao: ReviewData): Long = reviewDao.inserir(avaliacao)

    suspend fun atualizar(avaliacao: ReviewData): Int = reviewDao.atualizar(avaliacao)

    suspend fun deletar(avaliacao: ReviewData): Int = reviewDao.deletar(avaliacao)
}
