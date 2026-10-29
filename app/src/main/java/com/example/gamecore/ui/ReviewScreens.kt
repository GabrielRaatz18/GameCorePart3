package com.example.gamecore.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gamecore.R
import com.example.gamecore.data.local.ReviewData
import com.example.gamecore.data.model.GameData
import com.example.gamecore.data.model.UserData

@Composable
fun ReviewsScreen(
    game: GameData,
    reviews: List<ReviewData>,
    cartCount: Int,
    isOwned: Boolean = false,
    onOpen: (ReviewData) -> Unit,
    onRemove: (ReviewData) -> Unit,
    onAdd: (Int, String) -> Unit,
    currentUser: UserData,
    onBack: () -> Unit,
    onDestinationClick: (GameCoreDestination) -> Unit
) {
    var text by remember(game.id) { mutableStateOf("") }
    var selectedRating by remember(game.id) { mutableStateOf(5) }

    val myReview = remember(reviews, game.id) {
        reviews.firstOrNull {
            it.gameId == game.id && it.author.equals(currentUser.name, ignoreCase = true)
        }
    }

    GameCorePage(
        selectedDestination = null,
        cartCount = cartCount,
        onDestinationClick = onDestinationClick,
        topBar = {
            GameCoreTopBar(
                title = "Avaliações",
                subtitle = game.name,
                showBack = true,
                cartCount = cartCount,
                onBackClick = onBack,
                onCartClick = { onDestinationClick(GameCoreDestination.CART) },
                onAvatarClick = { onDestinationClick(GameCoreDestination.PROFILE) }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                if (isOwned) {
                    if (myReview != null) {
                        GameCoreCard {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Sua avaliação",
                                        color = GameCoreColors.TextPrimary,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "★ ${myReview.rating}.0",
                                        color = GameCoreColors.Orange,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = myReview.text,
                                    color = GameCoreColors.TextSecondary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                GameCoreSecondaryButton(
                                    text = "Editar sua avaliação",
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { onOpen(myReview) }
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Avaliar ${game.name}",
                            color = GameCoreColors.TextPrimary,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Publicando como ${currentUser.name} (@${currentUser.username})",
                            color = GameCoreColors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Sua nota: ", color = GameCoreColors.TextSecondary)
                            (1..5).forEach { star ->
                                Text(
                                    text = "★",
                                    color = if (star <= selectedRating) GameCoreColors.Orange else GameCoreColors.TextDisabled,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .clickable { selectedRating = star }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("O que achou do jogo?") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        GameCorePrimaryButton(
                            text = "Publicar avaliação",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (text.isNotBlank()) {
                                    onAdd(selectedRating, text)
                                    text = ""
                                }
                            }
                        )
                    }
                } else {
                    GameCoreCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_lock),
                                contentDescription = null,
                                tint = GameCoreColors.Orange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Avaliação restrita a proprietários",
                                    color = GameCoreColors.TextPrimary,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Adquira o jogo para publicar sua avaliação.",
                                    color = GameCoreColors.TextSecondary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Avaliações da comunidade (${reviews.size})",
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            if (reviews.isEmpty()) {
                item {
                    GameCoreCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nenhuma avaliação para este jogo ainda.",
                                color = GameCoreColors.TextPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isOwned) "Seja o primeiro a publicar sua opinião acima!" else "Adquira o jogo para publicar uma opinião!",
                                color = GameCoreColors.TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(reviews, key = { it.id }) { review ->
                    ReviewCard(
                        review = review,
                        isMyReview = review.author.equals(currentUser.name, ignoreCase = true),
                        onClick = { onOpen(review) },
                        onRemove = { onRemove(review) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun ReviewCard(
    review: ReviewData,
    isMyReview: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {

    GameCoreCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isMyReview, onClick = onClick)
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = review.author,
                    color = GameCoreColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "★ ${review.rating}.0",
                    color = GameCoreColors.Orange,
                    fontWeight = FontWeight.Bold
                )
                if (isMyReview) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Remover",
                        color = GameCoreColors.Error,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.clickable(onClick = onRemove)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = review.text,
                color = GameCoreColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
        }
    }
}

@Composable
fun ReviewDetailScreen(
    review: ReviewData,
    cartCount: Int,
    onSave: (ReviewData) -> Unit,
    onBack: () -> Unit,
    onDestinationClick: (GameCoreDestination) -> Unit
) {
    var text by remember(review.id) { mutableStateOf(review.text) }
    var rating by remember(review.id) { mutableStateOf(review.rating.toString()) }

    GameCorePage(
        selectedDestination = null,
        cartCount = cartCount,
        onDestinationClick = onDestinationClick,
        topBar = {
            GameCoreTopBar(
                title = "Detalhe da avaliação",
                showBack = true,
                cartCount = cartCount,
                onBackClick = onBack,
                onCartClick = { onDestinationClick(GameCoreDestination.CART) },
                onAvatarClick = { onDestinationClick(GameCoreDestination.PROFILE) }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = review.author,
                color = GameCoreColors.TextPrimary,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Edite sua opinião e a nota",
                color = GameCoreColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it.filter(Char::isDigit).take(1) },
                label = { Text("Nota (1 a 5)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Avaliação") },
                minLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Caracteres: ${text.length} • Útil para ${review.helpful} pessoas",
                color = GameCoreColors.TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(18.dp))
            GameCorePrimaryButton(
                text = "Salvar alterações",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSave(
                        review.copy(
                            text = text,
                            rating = rating.toIntOrNull()?.coerceIn(1, 5) ?: review.rating
                        )
                    )
                    onBack()
                }
            )
        }
    }
}
