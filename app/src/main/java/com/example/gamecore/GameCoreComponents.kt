package com.example.gamecore

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class GameCoreDestination(
    val label: String,
    @DrawableRes val iconRes: Int
) {
    STORE("Loja", R.drawable.ic_store),
    LIBRARY("Biblioteca", R.drawable.ic_library),
    CART("Carrinho", R.drawable.ic_cart),
    PROFILE("Perfil", R.drawable.ic_person)
}

@Composable
fun GameCorePage(
    selectedDestination: GameCoreDestination?,
    cartCount: Int = 3,
    onDestinationClick: (GameCoreDestination) -> Unit = {},
    topBar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        containerColor = GameCoreColors.Background,
        topBar = topBar,
        bottomBar = {
            GameCoreBottomNavigation(
                selectedDestination = selectedDestination,
                cartCount = cartCount,
                onDestinationClick = onDestinationClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GameCoreColors.Background)
        ) {
            content()
        }
    }
}

@Composable
fun GameCoreTopBar(
    title: String,
    subtitle: String? = null,
    showBack: Boolean = false,
    showSearchAction: Boolean = false,
    showCartAction: Boolean = true,
    showSettingsAction: Boolean = false,
    cartCount: Int = 3,
    @DrawableRes avatarRes: Int? = null,
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
    Surface(
        color = GameCoreColors.BackgroundSecondary,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = GameCoreDimens.ScreenPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showBack) {
                    GameCoreHeaderIconButton(
                        iconRes = R.drawable.ic_arrow_back,
                        contentDescription = "Voltar",
                        onClick = onBackClick
                    )
                    Spacer(Modifier.width(10.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = GameCoreColors.TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            color = GameCoreColors.TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (showSearchAction) {
                    GameCoreHeaderIconButton(
                        iconRes = R.drawable.ic_search,
                        contentDescription = "Pesquisar",
                        onClick = onSearchClick
                    )
                    Spacer(Modifier.width(8.dp))
                }

                if (showCartAction) {
                    GameCoreHeaderIconButton(
                        iconRes = R.drawable.ic_cart,
                        contentDescription = "Carrinho",
                        badge = cartCount.takeIf { it > 0 }?.toString(),
                        onClick = onCartClick
                    )
                    Spacer(Modifier.width(8.dp))
                }

                if (showSettingsAction) {
                    GameCoreHeaderIconButton(
                        iconRes = R.drawable.ic_settings,
                        contentDescription = "Configurações",
                        onClick = onSettingsClick
                    )
                    Spacer(Modifier.width(8.dp))
                }

                GameCoreAvatar(
                    imageRes = avatarRes,
                    onClick = onAvatarClick
                )
            }
            HorizontalDivider(color = GameCoreColors.Border, thickness = 1.dp)
        }
    }
}

@Composable
fun GameCoreHeaderIconButton(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    badge: String? = null,
    onClick: () -> Unit = {}
) {
    Box {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = GameCoreColors.CardElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, GameCoreColors.Border),
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = contentDescription,
                    tint = GameCoreColors.TextPrimary,
                    modifier = Modifier.size(21.dp)
                )
            }
        }

        if (!badge.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(GameCoreColors.Orange),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge,
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun GameCoreAvatar(
    @DrawableRes imageRes: Int? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = GameCoreColors.CardElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, GameCoreColors.BorderStrong),
        onClick = onClick
    ) {
        if (imageRes != null) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = "Perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_person),
                    contentDescription = "Perfil",
                    tint = GameCoreColors.TextPrimary,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@Composable
fun GameCoreBottomNavigation(
    selectedDestination: GameCoreDestination?,
    cartCount: Int,
    onDestinationClick: (GameCoreDestination) -> Unit
) {
    Surface(
        color = GameCoreColors.BackgroundSecondary,
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            HorizontalDivider(color = GameCoreColors.Border, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(GameCoreDimens.BottomBarHeight),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameCoreDestination.values().forEach { destination ->
                    GameCoreBottomNavigationItem(
                        destination = destination,
                        selected = selectedDestination == destination,
                        badge = if (destination == GameCoreDestination.CART && cartCount > 0) cartCount.toString() else null,
                        onClick = { onDestinationClick(destination) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GameCoreBottomNavigationItem(
    destination: GameCoreDestination,
    selected: Boolean,
    badge: String?,
    onClick: () -> Unit
) {
    val color = if (selected) GameCoreColors.Orange else GameCoreColors.NavInactive

    Box(
        modifier = Modifier
            .width(82.dp)
            .height(GameCoreDimens.BottomBarHeight)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(34.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomEnd = 3.dp, bottomStart = 3.dp))
                    .background(GameCoreColors.Orange)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                Icon(
                    painter = painterResource(destination.iconRes),
                    contentDescription = destination.label,
                    tint = color,
                    modifier = Modifier.size(23.dp)
                )

                if (!badge.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 10.dp, y = (-7).dp)
                            .size(17.dp)
                            .clip(CircleShape)
                            .background(GameCoreColors.Orange),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = badge,
                            color = Color.Black,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = destination.label,
                color = color,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun GameCoreSearchBar(
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    placeholder: String = "Pesquisar jogos",
    showFilter: Boolean = true,
    onFilterClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = GameCoreColors.Search,
        shape = RoundedCornerShape(GameCoreDimens.SearchRadius),
        border = androidx.compose.foundation.BorderStroke(1.dp, GameCoreColors.Border)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxSize(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = GameCoreColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = GameCoreColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onSearchQueryChange("") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = "Limpar",
                            tint = GameCoreColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else if (showFilter) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(onClick = onFilterClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_filter),
                            contentDescription = "Filtrar",
                            tint = GameCoreColors.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = GameCoreColors.Orange,
                focusedTextColor = GameCoreColors.TextPrimary,
                unfocusedTextColor = GameCoreColors.TextPrimary
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun GameCoreSectionHeader(
    title: String,
    action: String? = null,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = GameCoreColors.TextPrimary,
            style = MaterialTheme.typography.titleLarge
        )
        if (!action.isNullOrBlank()) {
            Text(
                text = action,
                color = GameCoreColors.Orange,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable(onClick = onActionClick)
            )
        }
    }
}

@Composable
fun GameCoreCategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit = {}
) {
    Surface(
        color = if (selected) GameCoreColors.Orange else GameCoreColors.Card,
        shape = RoundedCornerShape(20.dp),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, GameCoreColors.Border),
        onClick = onClick
    ) {
        Text(
            text = text,
            color = if (selected) Color.Black else GameCoreColors.TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}

@Composable
fun GameCoreImage(
    @DrawableRes imageRes: Int?,
    modifier: Modifier = Modifier,
    label: String = "IMAGEM",
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Int = 14
) {
    val shape = RoundedCornerShape(cornerRadius.dp)

    if (imageRes != null) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = label,
            contentScale = contentScale,
            modifier = modifier.clip(shape)
        )
    } else {
        Box(
            modifier = modifier
                .clip(shape)
                .background(GameCoreColors.CardElevated)
                .border(1.dp, GameCoreColors.Border, shape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(R.drawable.ic_image),
                    contentDescription = null,
                    tint = GameCoreColors.Orange,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = label,
                    color = GameCoreColors.TextDisabled,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun GameCorePrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIcon: Int? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(GameCoreDimens.ButtonHeight),
        shape = RoundedCornerShape(GameCoreDimens.ButtonRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = GameCoreColors.Orange,
            contentColor = Color.Black
        )
    ) {
        if (leadingIcon != null) {
            Icon(
                painter = painterResource(leadingIcon),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(19.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun GameCoreSecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIcon: Int? = null,
    iconTint: Color = GameCoreColors.TextPrimary,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.height(GameCoreDimens.ButtonHeight),
        color = GameCoreColors.CardElevated,
        shape = RoundedCornerShape(GameCoreDimens.ButtonRadius),
        border = androidx.compose.foundation.BorderStroke(1.dp, GameCoreColors.BorderStrong),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = GameCoreColors.TextPrimary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun GameCoreInfoChip(text: String) {
    Surface(
        color = GameCoreColors.Card,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GameCoreColors.Border)
    ) {
        Text(
            text = text,
            color = GameCoreColors.TextSecondary,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun GameCorePrice(
    price: String,
    color: Color = GameCoreColors.TextPrimary,
    fontSize: Int = 20
) {
    Text(
        text = price,
        color = color,
        fontFamily = GameCoreFonts.Body,
        fontWeight = FontWeight.SemiBold,
        fontSize = fontSize.sp
    )
}

@Composable
fun GameCoreCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(GameCoreDimens.CardRadius),
        colors = CardDefaults.cardColors(containerColor = GameCoreColors.Card),
        border = androidx.compose.foundation.BorderStroke(1.dp, GameCoreColors.Border)
    ) {
        content()
    }
}

fun heroOverlayBrush(): Brush = Brush.verticalGradient(
    colors = listOf(Color.Transparent, Color(0x550B0D10), Color(0xF20B0D10))
)
