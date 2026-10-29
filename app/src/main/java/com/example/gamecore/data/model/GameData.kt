package com.example.gamecore.data.model

import androidx.annotation.DrawableRes
import com.example.gamecore.R
import java.text.NumberFormat
import java.util.Locale

data class GameData(
    val name: String,
    val category: String,
    val price: Double,
    @DrawableRes val imageRes: Int,
    val oldPrice: Double? = null,
    val discount: String? = null,
    val description: String,
    val developer: String,
    val release: String,
    val ageRating: String,
    val platforms: String,
    val screenshots: List<Int> = emptyList(),
    val id: String = name.lowercase().replace(" ", "-")
)

data class UserData(
    val id: String,
    val name: String,
    val username: String,
    val email: String
)

fun Double.toBrazilianPrice(): String {
    return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)
}

val storeGames = listOf(
    GameData(
        name = "Elden Ring",
        category = "Ação • RPG",
        price = 249.90,
        imageRes = R.drawable.elden,
        description = "Explore um vasto mundo de fantasia, enfrente inimigos poderosos e descubra os segredos das Terras Intermédias.",
        developer = "FromSoftware",
        release = "2022",
        ageRating = "16 anos",
        platforms = "PC, PlayStation, Xbox",
        screenshots = listOf(
            R.drawable.elden,
            R.drawable.read,
            R.drawable.last
        )
    ),
    GameData(
        name = "Red Dead Redemption 2",
        category = "Ação • Mundo Aberto",
        price = 74.90,
        oldPrice = 299.90,
        discount = "-75%",
        imageRes = R.drawable.read,
        description = "Viva a história de Arthur Morgan e da gangue Van der Linde em um mundo aberto marcado por escolhas e sobrevivência.",
        developer = "Rockstar Games",
        release = "2018",
        ageRating = "18 anos",
        platforms = "PC, PlayStation, Xbox",
        screenshots = listOf(
            R.drawable.read,
            R.drawable.elden,
            R.drawable.gta6_cap1
        )
    ),
    GameData(
        name = "Call of Duty: Black Ops 6",
        category = "FPS • Ação",
        price = 306.00,
        oldPrice = 339.90,
        discount = "-10%",
        imageRes = R.drawable.call,
        description = "Entre em missões de ação, combates rápidos e partidas multiplayer no universo de Black Ops.",
        developer = "Treyarch",
        release = "2024",
        ageRating = "18 anos",
        platforms = "PC, PlayStation, Xbox",
        screenshots = listOf(
            R.drawable.call,
            R.drawable.cs2,
            R.drawable.gta6_cap2
        )
    ),
    GameData(
        name = "The Last of Us",
        category = "Ação • Aventura",
        price = 138.90,
        oldPrice = 249.90,
        discount = "-44%",
        imageRes = R.drawable.last,
        description = "Acompanhe Joel e Ellie em uma jornada intensa por um mundo transformado por uma epidemia.",
        developer = "Naughty Dog",
        release = "2023",
        ageRating = "18 anos",
        platforms = "PC, PlayStation",
        screenshots = listOf(
            R.drawable.last,
            R.drawable.elden,
            R.drawable.read
        )
    ),
    GameData(
        name = "Grand Theft Auto VI",
        category = "Ação • Crime • Mundo Aberto",
        price = 550.90,
        imageRes = R.drawable.gta6_banner,
        description = "Grand Theft Auto VI viaja para o estado de Leonida, lar das ruas ensolaradas de Vice City e além.",
        developer = "Rockstar Games",
        release = "2026",
        ageRating = "18 anos",
        platforms = "PlayStation 5, Xbox Series X/S",
        screenshots = listOf(
            R.drawable.gta6_cap1,
            R.drawable.gta6_cap2,
            R.drawable.gta6_cap3
        )
    ),
    GameData(
        name = "Counter-Strike 2",
        category = "FPS • Ação",
        price = 74.90,
        imageRes = R.drawable.cs2,
        description = "Participe de partidas competitivas em equipes, com estratégia, precisão e diferentes mapas.",
        developer = "Valve",
        release = "2023",
        ageRating = "16 anos",
        platforms = "PC",
        screenshots = listOf(
            R.drawable.cs2,
            R.drawable.call,
            R.drawable.gta6_cap3
        )
    ),
    GameData(
        name = "EA SPORTS FC™ 27",
        category = "Esporte • Simulação",
        price = 299.00,
        imageRes = R.drawable.fc27,
        description = "Monte seu time, dispute campeonatos e viva partidas de futebol em diferentes modos de jogo.",
        developer = "EA Sports",
        release = "2026",
        ageRating = "Livre",
        platforms = "PC, PlayStation, Xbox",
        screenshots = listOf(
            R.drawable.fc27,
            R.drawable.call,
            R.drawable.read
        )
    )
)
