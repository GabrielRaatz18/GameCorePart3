package com.example.gamecore.data.repository

import com.example.gamecore.data.local.Usuario
import com.example.gamecore.data.local.UsuarioDAO
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDAO) {
    fun buscarTodos(): Flow<List<Usuario>> = usuarioDao.buscarTodos()

    suspend fun buscarPorEmail(email: String): Usuario? = usuarioDao.buscarPorEmail(email)

    suspend fun buscarPorUsuario(nomeUsuario: String): Usuario? = usuarioDao.buscarPorUsuario(nomeUsuario)

    suspend fun buscarLogin(email: String, senha: String): Usuario? = usuarioDao.buscarLogin(email, senha)

    suspend fun inserir(usuario: Usuario): Long = usuarioDao.inserir(usuario)

    suspend fun atualizar(usuario: Usuario): Int = usuarioDao.atualizar(usuario)

    suspend fun deletar(usuario: Usuario): Int = usuarioDao.deletar(usuario)
}
