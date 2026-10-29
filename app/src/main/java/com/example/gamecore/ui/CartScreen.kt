package com.example.gamecore.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gamecore.R
import com.example.gamecore.data.model.GameData
import com.example.gamecore.data.model.toBrazilianPrice
import com.example.gamecore.data.model.storeGames
import com.example.gamecore.ui.theme.GameCoreTheme

@Composable
fun CartScreen(
    cart: List<GameData> = emptyList(),
    onRemoveItem: (GameData) -> Unit = {},
    onContinueShopping: () -> Unit = {},
    onCheckout: () -> Unit = {},
    onDestinationClick: (GameCoreDestination) -> Unit = {},
    onOpenProfile: () -> Unit = {}
) {
    val context = LocalContext.current

    var couponCode by remember {
        mutableStateOf("")
    }
    var paymentMethodIndex by remember { mutableStateOf(0) }
    val paymentMethods = listOf("Cartão de crédito", "PIX", "Boleto")
    val discountRate = couponDiscountRate(couponCode)

    GameCorePage(
        selectedDestination = GameCoreDestination.CART,
        cartCount = cart.size,
        onDestinationClick = onDestinationClick,
        topBar = {
            GameCoreTopBar(
                title = "Carrinho",
                subtitle = "${cart.size} itens",
                showBack = true,
                showCartAction = false,
                onBackClick = onContinueShopping,
                onAvatarClick = onOpenProfile
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GameCoreDimens.ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Seu carrinho",
                color = GameCoreColors.TextPrimary,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Revise seus jogos antes de finalizar a compra.",
                color = GameCoreColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (cart.isEmpty()) {
                EmptyCart()
            } else {
                cart.forEach { game ->
                    CartItem(
                        game = game,
                        onRemove = {
                            onRemoveItem(game)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "← Continuar comprando",
                color = GameCoreColors.Orange,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clickable(onClick = onContinueShopping)
                    .padding(vertical = 8.dp)
            )

            if (cart.isNotEmpty()) {
                Spacer(modifier = Modifier.height(GameCoreDimens.SectionSpacing))

                GameCoreSectionHeader(title = "Resumo")

                Spacer(modifier = Modifier.height(10.dp))

                CartSummaryCard(cart = cart, discountRate = discountRate)

                Spacer(modifier = Modifier.height(GameCoreDimens.SectionSpacing))

                GameCoreSectionHeader(title = "Cupom")

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = couponCode,
                    onValueChange = {
                        couponCode = it
                    },
                    label = {
                        Text(text = "Código do cupom")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GameCoreColors.Orange,
                        unfocusedBorderColor = GameCoreColors.Border,
                        focusedLabelColor = GameCoreColors.Orange,
                        unfocusedLabelColor = GameCoreColors.TextSecondary,
                        focusedTextColor = GameCoreColors.TextPrimary,
                        unfocusedTextColor = GameCoreColors.TextPrimary
                    )
                )

                if (couponCode.isNotBlank()) {
                    Text(
                        text = if (discountRate > 0) "Cupom aplicado: ${(discountRate * 100).toInt()}% de desconto." else "Cupom inválido. Use GAME10 para 10% de desconto.",
                        color = if (discountRate > 0) GameCoreColors.Success else GameCoreColors.Error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(GameCoreDimens.SectionSpacing))

                GameCoreSectionHeader(title = "Pagamento")

                Spacer(modifier = Modifier.height(10.dp))

                CartPaymentMethod(
                    paymentMethod = paymentMethods[paymentMethodIndex],
                    onClick = { paymentMethodIndex = (paymentMethodIndex + 1) % paymentMethods.size }
                )

                Spacer(modifier = Modifier.height(GameCoreDimens.SectionSpacing))

                GameCorePrimaryButton(
                    text = "Pagar e adicionar à biblioteca",
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = R.drawable.ic_lock,
                    onClick = {
                        onCheckout()
                        Toast.makeText(context, "Pagamento concluído! Jogos adicionados à biblioteca.", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EmptyCart() {
    GameCoreCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_cart),
                contentDescription = null,
                tint = GameCoreColors.TextDisabled,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Seu carrinho está vazio",
                color = GameCoreColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Adicione jogos pela loja.",
                color = GameCoreColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CartItem(
    game: GameData,
    onRemove: () -> Unit
) {
    GameCoreCard {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameCoreImage(
                imageRes = game.imageRes,
                modifier = Modifier.size(
                    width = 82.dp,
                    height = 104.dp
                ),
                label = "CAPA",
                cornerRadius = 10
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.name,
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = game.category,
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(10.dp))

                GameCorePrice(
                    price = game.price.toBrazilianPrice(),
                    fontSize = 16
                )
            }

            Surface(
                onClick = onRemove,
                modifier = Modifier.size(36.dp),
                color = Color(0xFF211719),
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Remover",
                        tint = GameCoreColors.Error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CartSummaryCard(cart: List<GameData>, discountRate: Double) {
    val total = cart.sumOf { game ->
        game.price
    }

    val discount = total * discountRate
    val finalTotal = total - discount

    GameCoreCard {
        Column(modifier = Modifier.padding(16.dp)) {
            CartPriceRow(
                label = "Itens",
                value = cart.size.toString()
            )

            Spacer(modifier = Modifier.height(10.dp))

            CartPriceRow(
                label = "Subtotal",
                value = total.toBrazilianPrice()
            )

            if (discount > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                CartPriceRow(
                    label = "Desconto",
                    value = "-${discount.toBrazilianPrice()}"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = GameCoreColors.Border)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )

                GameCorePrice(
                    price = finalTotal.toBrazilianPrice(),
                    color = GameCoreColors.Orange,
                    fontSize = 21
                )
            }
        }
    }
}

@Composable
private fun CartPriceRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = GameCoreColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            color = GameCoreColors.TextPrimary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CartPaymentMethod(paymentMethod: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = GameCoreColors.CardElevated,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = GameCoreColors.Border
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = null,
                tint = GameCoreColors.Orange,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = paymentMethod,
                color = GameCoreColors.TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(R.drawable.ic_chevron_down),
                contentDescription = "Selecionar pagamento",
                tint = GameCoreColors.TextSecondary,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun CartPreview() {
    GameCoreTheme {
        CartScreen(
            cart = listOf(
                storeGames[0],
                storeGames[4]
            )
        )
    }
}

internal fun couponDiscountRate(code: String): Double = when (code.trim().uppercase()) {
    "GAME10" -> 0.10
    else -> 0.0
}
