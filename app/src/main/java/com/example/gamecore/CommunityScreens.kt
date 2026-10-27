package com.example.gamecore

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun LibraryScreen(
    games: List<GameData>,
    cartCount: Int,
    onOpenGame: (GameData) -> Unit,
    onDestinationClick: (GameCoreDestination) -> Unit,
    onProfile: () -> Unit
) {
    GameCorePage(
        selectedDestination = GameCoreDestination.LIBRARY,
        cartCount = cartCount,
        onDestinationClick = onDestinationClick,
        topBar = {
            GameCoreTopBar(
                title = "Biblioteca",
                subtitle = "${games.size} jogos",
                cartCount = cartCount,
                onCartClick = { onDestinationClick(GameCoreDestination.CART) },
                onAvatarClick = onProfile
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Minha biblioteca",
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Jogos comprados aparecem aqui após o pagamento.",
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            items(games, key = { it.id }) { game ->
                GameListCard(
                    game = game,
                    action = null,
                    onClick = { onOpenGame(game) },
                    onAction = null,
                    showPrice = false
                )
            }
            if (games.isEmpty()) {
                item {
                    EmptyMessage(
                        title = "Sua biblioteca está vazia",
                        subtitle = "Adicione jogos ao carrinho e conclua o pagamento."
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
fun WishlistScreen(
    games: List<GameData>,
    cartCount: Int,
    onOpenGame: (GameData) -> Unit,
    onRemove: (GameData) -> Unit,
    onBack: () -> Unit = {},
    onDestinationClick: (GameCoreDestination) -> Unit
) {
    GameCorePage(
        selectedDestination = null,
        cartCount = cartCount,
        onDestinationClick = onDestinationClick,
        topBar = {
            GameCoreTopBar(
                title = "Lista de Desejos",
                subtitle = "Acompanhe suas próximas compras",
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
            items(games, key = { it.id }) { game ->
                GameListCard(
                    game = game,
                    action = "Remover",
                    onClick = { onOpenGame(game) },
                    onAction = { onRemove(game) }
                )
            }
            if (games.isEmpty()) {
                item {
                    EmptyMessage(
                        title = "Sua lista de desejos está vazia",
                        subtitle = "Abra um jogo e adicione-o à sua lista."
                    )
                }
            }
        }
    }
}

@Composable
private fun GameListCard(
    game: GameData,
    action: String?,
    onClick: () -> Unit,
    onAction: (() -> Unit)?,
    showPrice: Boolean = true
) {
    GameCoreCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameCoreImage(
                imageRes = game.imageRes,
                modifier = Modifier.size(72.dp, 88.dp),
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
                Text(
                    text = game.category,
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                if (showPrice) {
                    Spacer(modifier = Modifier.height(8.dp))
                    GameCorePrice(
                        price = game.price.toBrazilianPrice(),
                        fontSize = 16
                    )
                }
            }
            if (action != null && onAction != null) {
                Surface(
                    onClick = onAction,
                    color = Color(0xFF211719),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = action,
                        color = GameCoreColors.Error,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    library: List<GameData>,
    cartCount: Int,
    onOpenGame: (GameData) -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onDestinationClick: (GameCoreDestination) -> Unit
) {
    val userLevel = library.size * 2

    GameCorePage(
        selectedDestination = GameCoreDestination.PROFILE,
        cartCount = cartCount,
        onDestinationClick = onDestinationClick,
        topBar = {
            GameCoreTopBar(
                title = "Perfil",
                showCartAction = false,
                showSettingsAction = true,
                onSettingsClick = onOpenSettings,
                onAvatarClick = onOpenSettings
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            GameCoreCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GameCoreAvatar(onClick = onOpenSettings)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Matheus",
                            color = GameCoreColors.TextPrimary,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "@MatheusP",
                            color = GameCoreColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$userLevel   Nível     ${library.size}   Jogos     0   Horas",
                            color = GameCoreColors.Orange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Atividade recente",
                color = GameCoreColors.TextPrimary,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (library.isEmpty()) {
                GameCoreCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nenhuma atividade recente",
                            color = GameCoreColors.TextPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Adquira jogos na loja para começar sua coleção!",
                            color = GameCoreColors.TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                library.take(3).forEach { game ->
                    GameListCard(
                        game = game,
                        action = "Abrir",
                        onClick = { onOpenGame(game) },
                        onAction = { onOpenGame(game) },
                        showPrice = false
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                GameCoreSecondaryButton(
                    text = "Ver biblioteca completa",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenLibrary
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    cartCount: Int,
    gamesCount: Int,
    wishlistCount: Int,
    onBack: () -> Unit,
    onOpenWishlist: () -> Unit = {},
    onLogout: () -> Unit = {},
    onDestinationClick: (GameCoreDestination) -> Unit
) {
    val context = LocalContext.current

    GameCorePage(
        selectedDestination = GameCoreDestination.PROFILE,
        cartCount = cartCount,
        onDestinationClick = onDestinationClick,
        topBar = {
            GameCoreTopBar(
                title = "Configurações",
                showBack = true,
                showCartAction = false,
                onBackClick = onBack,
                onAvatarClick = {}
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                color = GameCoreColors.CardElevated,
                border = BorderStroke(2.dp, GameCoreColors.Orange)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = "Perfil",
                        tint = GameCoreColors.TextPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Matheus",
                color = GameCoreColors.TextPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "@matheus",
                color = GameCoreColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = GameCoreColors.CardElevated,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, GameCoreColors.Border),
                modifier = Modifier.clickable {
                    Toast.makeText(context, "Editar perfil", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text(
                    text = "Editar perfil",
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            GameCoreCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$gamesCount",
                            color = GameCoreColors.Orange,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Jogos",
                            color = GameCoreColors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(GameCoreColors.Border)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$wishlistCount",
                            color = GameCoreColors.Orange,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Favoritos",
                            color = GameCoreColors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(GameCoreColors.Border)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "0 h",
                            color = GameCoreColors.Orange,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Jogadas",
                            color = GameCoreColors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Minha conta",
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsMenuItem(
                    iconRes = R.drawable.ic_person,
                    title = "Dados pessoais",
                    onClick = { Toast.makeText(context, "Dados pessoais", Toast.LENGTH_SHORT).show() }
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingsMenuItem(
                    iconRes = R.drawable.ic_mail,
                    title = "E-mail e conta",
                    onClick = { Toast.makeText(context, "E-mail e conta", Toast.LENGTH_SHORT).show() }
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingsMenuItem(
                    iconRes = R.drawable.ic_lock,
                    title = "Segurança",
                    onClick = { Toast.makeText(context, "Segurança", Toast.LENGTH_SHORT).show() }
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingsMenuItem(
                    iconRes = R.drawable.ic_payment,
                    title = "Métodos de pagamento",
                    onClick = { Toast.makeText(context, "Métodos de pagamento", Toast.LENGTH_SHORT).show() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "GameCore",
                    color = GameCoreColors.TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsMenuItem(
                    iconRes = R.drawable.ic_favorite,
                    title = "Lista de desejos",
                    onClick = onOpenWishlist
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingsMenuItem(
                    iconRes = R.drawable.ic_trophy,
                    title = "Conquistas",
                    onClick = { Toast.makeText(context, "Conquistas", Toast.LENGTH_SHORT).show() }
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingsMenuItem(
                    iconRes = R.drawable.ic_settings,
                    title = "Configurações",
                    onClick = { Toast.makeText(context, "Configurações da aplicação", Toast.LENGTH_SHORT).show() }
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingsMenuItem(
                    iconRes = R.drawable.ic_help,
                    title = "Ajuda e suporte",
                    onClick = { Toast.makeText(context, "Ajuda e suporte", Toast.LENGTH_SHORT).show() }
                )

                Spacer(modifier = Modifier.height(28.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    color = Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GameCoreColors.Error),
                    onClick = {
                        Toast.makeText(context, "Saindo da conta...", Toast.LENGTH_SHORT).show()
                        onLogout()
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Sair da conta",
                            color = GameCoreColors.Error,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SettingsMenuItem(
    @DrawableRes iconRes: Int,
    title: String,
    onClick: () -> Unit
) {
    GameCoreCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = title,
                tint = GameCoreColors.Orange,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                color = GameCoreColors.TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = GameCoreColors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun EmptyMessage(title: String, subtitle: String) {
    GameCoreCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = GameCoreColors.TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                color = GameCoreColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
