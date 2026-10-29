package com.example.gamecore.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamecore.data.local.Usuario
import com.example.gamecore.data.model.UserData
import com.example.gamecore.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class AuthUiState(
    val currentUser: UserData = UserData("guest", "", "", ""),
    val loginEmail: String = "",
    val loginPassword: String = "",
    val registerName: String = "",
    val registerUsername: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val authError: String? = null,
    val isAuthenticating: Boolean = false
)

class AuthViewModel(private val repository: UsuarioRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onLoginEmailChange(value: String) = _uiState.update { it.copy(loginEmail = value) }

    fun onLoginPasswordChange(value: String) = _uiState.update { it.copy(loginPassword = value) }

    fun onRegisterNameChange(value: String) = _uiState.update { it.copy(registerName = value) }

    fun onRegisterUsernameChange(value: String) = _uiState.update { it.copy(registerUsername = value) }

    fun onRegisterEmailChange(value: String) = _uiState.update { it.copy(registerEmail = value) }

    fun onRegisterPasswordChange(value: String) = _uiState.update { it.copy(registerPassword = value) }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        _uiState.update { it.copy(authError = null, isAuthenticating = true) }
        val normalizedEmail = normalizeEmail(email)

        viewModelScope.launch {
            try {
                val usuario = repository.buscarLogin(normalizedEmail, password)
                if (usuario != null) {
                    _uiState.value = AuthUiState(currentUser = usuario.toUserData())
                    onSuccess()
                } else {
                    publishAuthError("E-mail ou senha incorretos.")
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível realizar o login.", error)
                publishAuthError("Não foi possível entrar agora. Tente novamente.")
            }
        }
    }

    fun register(
        name: String,
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        _uiState.update { it.copy(authError = null, isAuthenticating = true) }
        val normalizedUsername = normalizeUsername(username)
        val normalizedEmail = normalizeEmail(email)
        val cleanName = name.trim()

        viewModelScope.launch {
            try {
                when {
                    repository.buscarPorEmail(normalizedEmail) != null -> {
                        publishAuthError("Já existe uma conta com este e-mail.")
                    }

                    repository.buscarPorUsuario(normalizedUsername) != null -> {
                        publishAuthError("Este nome de usuário já está em uso.")
                    }

                    else -> {
                        val usuario = Usuario(
                            nome = cleanName,
                            usuario = normalizedUsername,
                            email = normalizedEmail,
                            senha = password
                        )
                        val id = repository.inserir(usuario)
                        if (id > 0) {
                            _uiState.value = AuthUiState(
                                currentUser = usuario.copy(id = id.toInt()).toUserData()
                            )
                            onSuccess()
                        } else {
                            publishAuthError("Não foi possível criar a conta. Tente outro e-mail ou usuário.")
                        }
                    }
                }
            } catch (error: Exception) {
                Log.e(TAG, "Não foi possível cadastrar o usuário.", error)
                publishAuthError("Não foi possível criar a conta. Tente novamente.")
            }
        }
    }

    fun clearAuthError() {
        _uiState.update { it.copy(authError = null) }
    }

    fun signOut() {
        _uiState.value = AuthUiState()
    }

    private fun publishAuthError(message: String) {
        _uiState.update { it.copy(isAuthenticating = false, authError = message) }
    }

    private fun normalizeEmail(value: String): String = value.trim().lowercase(Locale.ROOT)

    private fun normalizeUsername(value: String): String = value
        .trim()
        .removePrefix("@")
        .lowercase(Locale.ROOT)

    private fun Usuario.toUserData(): UserData = UserData(
        id = id.toString(),
        name = nome,
        username = usuario,
        email = email
    )

    private companion object {
        const val TAG = "AuthViewModel"
    }
}

class AuthViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}
