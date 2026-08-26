package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object CategoryIconUtil {
    fun getIcon(name: String): ImageVector {
        return when (name.lowercase()) {
            "restaurant", "food" -> Icons.Rounded.Restaurant
            "shopping_cart", "grocery" -> Icons.Rounded.ShoppingCart
            "directions_bus", "transport" -> Icons.Rounded.DirectionsBus
            "shopping_bag", "shopping" -> Icons.Rounded.ShoppingBag
            "receipt_long", "bills" -> Icons.Rounded.ReceiptLong
            "home", "rent" -> Icons.Rounded.Home
            "medical_services", "health" -> Icons.Rounded.MedicalServices
            "family_restroom", "family" -> Icons.Rounded.FamilyRestroom
            "school", "education" -> Icons.Rounded.School
            "movie", "entertainment" -> Icons.Rounded.Movie
            "volunteer_activism", "donation" -> Icons.Rounded.VolunteerActivism
            "subscriptions", "sub" -> Icons.Rounded.Subscriptions
            "payments", "salary" -> Icons.Rounded.Payments
            "storefront", "business" -> Icons.Rounded.Storefront
            "laptop_mac", "freelance" -> Icons.Rounded.LaptopMac
            "card_giftcard", "bonus" -> Icons.Rounded.CardGiftcard
            "trending_up", "investment" -> Icons.Rounded.TrendingUp
            "apartment", "rental" -> Icons.Rounded.Apartment
            "redeem", "gift" -> Icons.Rounded.Redeem
            "attach_money", "money" -> Icons.Rounded.AttachMoney
            "account_balance", "bank" -> Icons.Rounded.AccountBalance
            "phone_android", "mobile" -> Icons.Rounded.PhoneAndroid
            "savings" -> Icons.Rounded.Savings
            "flag" -> Icons.Rounded.Flag
            "credit_card" -> Icons.Rounded.CreditCard
            else -> Icons.Rounded.Category
        }
    }

    fun parseColor(hex: String, defaultColor: Color = Color(0xFF00695C)): Color {
        return try {
            val cleanHex = hex.removePrefix("#")
            val colorInt = cleanHex.toLong(16)
            if (cleanHex.length == 6) {
                Color(0xFF000000 or colorInt)
            } else if (cleanHex.length == 8) {
                Color(colorInt)
            } else {
                defaultColor
            }
        } catch (e: Exception) {
            defaultColor
        }
    }
}

@Composable
fun CategoryAvatar(
    iconName: String,
    colorHex: String,
    size: Dp = 42.dp,
    iconSize: Dp = 22.dp,
    modifier: Modifier = Modifier
) {
    val bgColor = CategoryIconUtil.parseColor(colorHex).copy(alpha = 0.15f)
    val iconColor = CategoryIconUtil.parseColor(colorHex)

    Box(
        modifier = modifier
            .size(size)
            .background(bgColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = CategoryIconUtil.getIcon(iconName),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
