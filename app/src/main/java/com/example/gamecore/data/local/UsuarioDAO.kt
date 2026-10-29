package com.example.gamecore.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDAO {
    @Insert
    suspend fun inserir(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios")
    fun buscarTodos(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE usuario = :nomeUsuario LIMIT 1")
    suspend fun buscarPorUsuario(nomeUsuario: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE email = :email AND senha = :senha LIMIT 1")
    suspend fun buscarLogin(email: String, senha: String): Usuario?

    @Update
    suspend fun atualizar(usuario: Usuario): Int

    @Delete
    suspend fun deletar(usuario: Usuario): Int
}
