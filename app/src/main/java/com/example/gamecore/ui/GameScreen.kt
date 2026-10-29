package com.example.gamecore.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gamecore.R
import com.example.gamecore.data.local.ReviewData
import com.example.gamecore.data.model.GameData
import com.example.gamecore.data.model.toBrazilianPrice
import com.example.gamecore.data.model.storeGames
import com.example.gamecore.ui.theme.GameCoreTheme
import java.util.Locale

@Composable
fun GameScreen(
    game: GameData,
    cartCount: Int = 0,
    onBack: () -> Unit = {},
    onOpenCart: () -> Unit = {},
    onAddToCart: () -> Boolean = { false },
    onAddToWishlist: ((Boolean) -> Unit) -> Unit = { it(false) },
    onOpenReviews: () -> Unit = {},
    isOwned: Boolean = false,
    isInWishlist: Boolean = false,
    reviews: List<ReviewData> = emptyList(),
    onDestinationClick: (GameCoreDestination) -> Unit = {}
) {
    val context = LocalContext.current

    var isAddingToWishlist by remember(game.id) { mutableStateOf(false) }

    val gameReviews = remember(reviews, game.id) {
        reviews.filter { it.gameId == game.id }
    }
    val avgRating = if (gameReviews.isNotEmpty()) gameReviews.map { it.rating }.average() else 5.0
    val ratingText = String.format(Locale.US, "%.1f", avgRating)
    val reviewCountText = if (gameReviews.isNotEmpty()) {
        "${gameReviews.size} avaliações da comunidade"
    } else {
        "Sem avaliações ainda"
    }

    GameCorePage(
        selectedDestination = null,
        cartCount = cartCount,
        onDestinationClick = onDestinationClick,
        topBar = {
            GameCoreTopBar(
                title = "GameCore",
                showBack = true,
                showSearchAction = false,
                showCartAction = true,
                cartCount = cartCount,
                onBackClick = onBack,
                onCartClick = onOpenCart,
                onAvatarClick = { onDestinationClick(GameCoreDestination.PROFILE) }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            GameHeroBanner(imageRes = game.imageRes)

            Column(
                modifier = Modifier.padding(GameCoreDimens.ScreenPadding)
            ) {
                Text(
                    text = game.name.uppercase(),
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = game.category,
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) {
                        Icon(
                            painter = painterResource(R.drawable.ic_star),
                            contentDescription = null,
                            tint = GameCoreColors.Orange,
                            modifier = Modifier.size(17.dp)
                        )

                        Spacer(modifier = Modifier.width(2.dp))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = ratingText,
                        color = GameCoreColors.TextPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = reviewCountText,
                        color = GameCoreColors.TextDisabled,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isOwned) {
                    GameCorePrimaryButton(
                        text = "Jogar",
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = R.drawable.ic_play,
                        onClick = {
                            Toast.makeText(
                                context,
                                "Iniciando ${game.name}...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    GameCoreSecondaryButton(
                        text = "Ver / Escrever avaliação",
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = R.drawable.ic_star,
                        onClick = onOpenReviews
                    )
                } else {
                    GameCorePrice(
                        price = game.price.toBrazilianPrice(),
                        fontSize = 22
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    GameCorePrimaryButton(
                        text = "Adicionar ao carrinho",
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = R.drawable.ic_cart,
                        onClick = {
                            val added = onAddToCart()
                            val message = if (added) {
                                "Jogo adicionado ao carrinho!"
                            } else {
                                "Este jogo já está no carrinho."
                            }

                            Toast.makeText(
                                context,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    GameCoreSecondaryButton(
                        text = if (isInWishlist) "Jogo já está na lista de desejos" else "Adicionar à lista de desejos",
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = R.drawable.ic_favorite,
                        iconTint = if (isInWishlist) GameCoreColors.Orange else GameCoreColors.TextPrimary,
                        onClick = {
                            if (isInWishlist) {
                                Toast.makeText(
                                    context,
                                    "Este jogo já está na sua lista de desejos.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (!isAddingToWishlist) {
                                isAddingToWishlist = true
                                onAddToWishlist { added ->
                                    isAddingToWishlist = false
                                    val message = if (added) "Adicionado à lista de desejos!" else "Não foi possível adicionar à lista de desejos."
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    GameCoreSecondaryButton(
                        text = "Ver avaliações",
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = R.drawable.ic_star,
                        onClick = onOpenReviews
                    )
                }

                Spacer(modifier = Modifier.height(GameCoreDimens.SectionSpacing))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameCoreInfoChip("Single Player")
                    GameCoreInfoChip("Multiplayer")
                    GameCoreInfoChip("4K Ultra HD")
                    GameCoreInfoChip("Português")
                }

                Spacer(modifier = Modifier.height(GameCoreDimens.SectionSpacing))

                GameCoreSectionHeader(title = "Sobre este jogo")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = game.description,
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (game.screenshots.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(GameCoreDimens.SectionSpacing))

                    GameCoreSectionHeader(title = "Capturas de tela")

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        game.screenshots.forEachIndexed { index, screenshot ->
                            GameCoreImage(
                                imageRes = screenshot,
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(124.dp),
                                label = "CAPTURA ${index + 1}",
                                cornerRadius = 12
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(GameCoreDimens.SectionSpacing))

                GameCoreSectionHeader(title = "Informações")

                Spacer(modifier = Modifier.height(10.dp))

                GameInfoCard(game = game)

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GameHeroBanner(imageRes: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        GameCoreImage(
            imageRes = imageRes,
            modifier = Modifier.fillMaxSize(),
            label = "BANNER DO JOGO",
            cornerRadius = 0
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(heroOverlayBrush())
        )

        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .size(58.dp),
            shape = CircleShape,
            color = Color(0xCC151A20),
            border = BorderStroke(
                width = 1.dp,
                color = GameCoreColors.Border
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_play),
                    contentDescription = "Assistir trailer",
                    tint = GameCoreColors.Orange,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

@Composable
private fun GameInfoCard(game: GameData) {
    GameCoreCard {
        Column(modifier = Modifier.padding(14.dp)) {
            GameInfoRow(
                label = "Desenvolvedora",
                value = game.developer
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameInfoRow(
                label = "Lançamento",
                value = game.release
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameInfoRow(
                label = "Classificação",
                value = game.ageRating
            )

            Spacer(modifier = Modifier.height(10.dp))

            GameInfoRow(
                label = "Plataforma",
                value = game.platforms
            )
        }
    }
}

@Composable
private fun GameInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = GameCoreColors.TextSecondary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            color = GameCoreColors.TextPrimary,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun GamePreview() {
    GameCoreTheme {
        GameScreen(game = storeGames[4])
    }
}
