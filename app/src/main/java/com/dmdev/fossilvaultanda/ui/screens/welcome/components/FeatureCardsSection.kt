package com.dmdev.fossilvaultanda.ui.screens.welcome.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultRadius
import com.dmdev.fossilvaultanda.ui.theme.AccentGreenLight
import com.dmdev.fossilvaultanda.ui.theme.AccentBlueLight
import com.dmdev.fossilvaultanda.ui.theme.LogoGradientStart
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

data class FeatureData(
    val icon: ImageVector,
    val iconColor: Color,
    val title: String,
    val subtitle: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeatureCardsSection(
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { 3 })
    
    val features = listOf(
        FeatureData(
            icon = Icons.Default.Lock,
            iconColor = AccentGreenLight,
            title = "Secure Collection",
            subtitle = "Professional cataloging with detailed metadata"
        ),
        FeatureData(
            icon = Icons.Default.Home,
            iconColor = AccentBlueLight,
            title = "Access Anywhere",
            subtitle = "Sync across all devices automatically"
        ),
        FeatureData(
            icon = Icons.Default.Share,
            iconColor = LogoGradientStart,
            title = "Share & Export",
            subtitle = "Export data or share discoveries"
        )
    )
    
    // Auto-scroll effect
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000) // 4-second intervals as per spec
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }
    
    // Trigger entrance animation
    LaunchedEffect(Unit) {
        delay(500) // Staggered delay as per spec
        isVisible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "feature_cards_alpha"
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.lg)
    ) {
        // Horizontal pager for feature cards
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .semantics {
                    contentDescription = "Feature showcase - swipe to see different app features"
                }
        ) { page ->
            FeatureCard(
                feature = features[page],
                isActive = page == pagerState.currentPage,
                modifier = Modifier.padding(horizontal = FossilVaultSpacing.md)
            )
        }
        
        // Page indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                PageIndicator(
                    isActive = index == pagerState.currentPage
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    feature: FeatureData,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_alpha"
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.lg)
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(feature.iconColor.copy(alpha = 0.15f))
                .border(
                    width = 1.dp,
                    color = feature.iconColor.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                modifier = Modifier.size(32.dp),
                tint = feature.iconColor
            )
        }
        
        // Text content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm)
        ) {
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = feature.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = FossilVaultSpacing.md)
            )
        }
    }
}

@Composable
private fun PageIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicator_scale"
    )
    
    Box(
        modifier = modifier
            .size(8.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isActive) LogoGradientStart else MaterialTheme.colorScheme.outline
            )
    )
}

@Preview(showBackground = true)
@Composable
fun FeatureCardsSectionPreview() {
    FossilVaultTheme {
        FeatureCardsSection(
            modifier = Modifier.padding(FossilVaultSpacing.lg)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeatureCardsSectionDarkPreview() {
    FossilVaultTheme(darkTheme = true) {
        FeatureCardsSection(
            modifier = Modifier.padding(FossilVaultSpacing.lg)
        )
    }
}