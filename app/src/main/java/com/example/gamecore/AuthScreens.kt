package com.example.gamecore

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun GameCoreLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(130.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val path = Path().apply {
                moveTo(width * 0.5f, 0f)
                lineTo(width, height * 0.25f)
                lineTo(width, height * 0.75f)
                lineTo(width * 0.5f, height)
                lineTo(0f, height * 0.75f)
                lineTo(0f, height * 0.25f)
                close()
            }
            drawPath(
                path = path,
                color = Color(0xFFFF7A00),
                style = Stroke(width = 10.dp.toPx(), join = StrokeJoin.Round)
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_store),
            contentDescription = "GameCore Logo",
            tint = Color.White,
            modifier = Modifier.size(52.dp)
        )
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onOpenRegister: () -> Unit
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("matheus@gmail.com") }
    var password by remember { mutableStateOf("12345678") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = GameCoreColors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "LOGIN",
                color = GameCoreColors.TextPrimary,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            GameCoreLogo()

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "E-MAIL",
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            text = "Digite seu e-mail",
                            color = GameCoreColors.TextDisabled
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_person),
                            contentDescription = null,
                            tint = GameCoreColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = GameCoreColors.Search,
                        unfocusedContainerColor = GameCoreColors.Search,
                        focusedBorderColor = GameCoreColors.Orange,
                        unfocusedBorderColor = GameCoreColors.Border,
                        focusedTextColor = GameCoreColors.TextPrimary,
                        unfocusedTextColor = GameCoreColors.TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SENHA",
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            text = "Sua senha secreta",
                            color = GameCoreColors.TextDisabled
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_lock),
                            contentDescription = null,
                            tint = GameCoreColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { isPasswordVisible = !isPasswordVisible },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_chevron_down),
                                contentDescription = "Mostrar senha",
                                tint = GameCoreColors.TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = GameCoreColors.Search,
                        unfocusedContainerColor = GameCoreColors.Search,
                        focusedBorderColor = GameCoreColors.Orange,
                        unfocusedBorderColor = GameCoreColors.Border,
                        focusedTextColor = GameCoreColors.TextPrimary,
                        unfocusedTextColor = GameCoreColors.TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Esqueceu a senha?",
                    color = GameCoreColors.Orange,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Instruções enviadas para o e-mail.", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            GameCorePrimaryButton(
                text = "Entrar",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = R.drawable.ic_lock,
                onClick = {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() && password.length >= 6) {
                        Toast.makeText(context, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                        onLoginSuccess(email.trim())
                    } else {
                        Toast.makeText(context, "Informe um e-mail válido e uma senha com ao menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Não tem uma conta? ",
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Cadastrar",
                    color = GameCoreColors.Orange,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onOpenRegister)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: (name: String, username: String, email: String) -> Unit,
    onOpenLogin: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = GameCoreColors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CADASTRAR-SE",
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Crie sua conta",
                        color = GameCoreColors.TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "GameCore",
                        color = GameCoreColors.Orange,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "NOME COMPLETO",
                color = GameCoreColors.TextSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = {
                    Text(
                        text = "Seu nome completo",
                        color = GameCoreColors.TextDisabled
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = null,
                        tint = GameCoreColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GameCoreColors.Search,
                    unfocusedContainerColor = GameCoreColors.Search,
                    focusedBorderColor = GameCoreColors.Orange,
                    unfocusedBorderColor = GameCoreColors.Border,
                    focusedTextColor = GameCoreColors.TextPrimary,
                    unfocusedTextColor = GameCoreColors.TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "USUÁRIO",
                color = GameCoreColors.TextSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = {
                    Text(
                        text = "@username",
                        color = GameCoreColors.TextDisabled
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = null,
                        tint = GameCoreColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GameCoreColors.Search,
                    unfocusedContainerColor = GameCoreColors.Search,
                    focusedBorderColor = GameCoreColors.Orange,
                    unfocusedBorderColor = GameCoreColors.Border,
                    focusedTextColor = GameCoreColors.TextPrimary,
                    unfocusedTextColor = GameCoreColors.TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "E-MAIL",
                color = GameCoreColors.TextSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        text = "seuemail@exemplo.com",
                        color = GameCoreColors.TextDisabled
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_mail),
                        contentDescription = null,
                        tint = GameCoreColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GameCoreColors.Search,
                    unfocusedContainerColor = GameCoreColors.Search,
                    focusedBorderColor = GameCoreColors.Orange,
                    unfocusedBorderColor = GameCoreColors.Border,
                    focusedTextColor = GameCoreColors.TextPrimary,
                    unfocusedTextColor = GameCoreColors.TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SENHA",
                color = GameCoreColors.TextSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        text = "Crie uma senha forte",
                        color = GameCoreColors.TextDisabled
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = null,
                        tint = GameCoreColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { isPasswordVisible = !isPasswordVisible },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_chevron_down),
                            contentDescription = "Mostrar senha",
                            tint = GameCoreColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GameCoreColors.Search,
                    unfocusedContainerColor = GameCoreColors.Search,
                    focusedBorderColor = GameCoreColors.Orange,
                    unfocusedBorderColor = GameCoreColors.Border,
                    focusedTextColor = GameCoreColors.TextPrimary,
                    unfocusedTextColor = GameCoreColors.TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(36.dp))

            GameCorePrimaryButton(
                text = "Cadastrar",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (name.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        Toast.makeText(context, "Informe um e-mail válido.", Toast.LENGTH_SHORT).show()
                    } else if (password.length < 6) {
                        Toast.makeText(context, "A senha deve ter ao menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        onRegisterSuccess(name, username, email)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Já tem conta? ",
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Entrar",
                    color = GameCoreColors.Orange,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onOpenLogin)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
