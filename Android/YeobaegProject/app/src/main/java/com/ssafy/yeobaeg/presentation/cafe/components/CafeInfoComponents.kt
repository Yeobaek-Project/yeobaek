package com.ssafy.yeobaeg.presentation.cafe.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.cafe.CafeDetail
import com.ssafy.yeobaeg.domain.model.cafeowner.CafeOwner
import com.ssafy.yeobaeg.presentation.arcore.plan.FloorPlanPreview

@Composable
fun FloorPlanSection(
    floorPlanSlots: List<DisplaySlot>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "카페 도면",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF654321)
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF654321),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "슬롯 위치 안내",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF654321),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (floorPlanSlots.isEmpty()) {
                FloorPlanEmptyState()
            } else {
                FloorPlanPreview(
                    initialWalls = floorPlanSlots,
                    modifier = Modifier.fillMaxSize(),
                    readOnly = true
                )
            }
        }
    }
}

@Composable
private fun FloorPlanEmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Map,
            contentDescription = "도면 없음",
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "도면 정보가 없습니다",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF9E9E9E),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "카페에서 제공한 도면이 없어요",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFBDBDBD)
        )
    }
}

@Composable
fun InfoTabContent(
    cafeDetail: CafeDetail
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        CafeDescription(description = cafeDetail.description)

        CafeOwnerSection(cafeOwner = cafeDetail.cafeOwner)

        HorizontalDivider()

        CafeInfoList(cafeDetail = cafeDetail)
    }
}

@Composable
private fun CafeDescription(description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "카페 소개",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CafeOwnerSection(cafeOwner: CafeOwner) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "카페 사장 정보",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        CafeOwnerCard(cafeOwner = cafeOwner)
    }
}

@Composable
fun CafeOwnerCard(cafeOwner: CafeOwner) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F1ED))
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            AsyncImage(
                model = cafeOwner.profileImageUrl,
                contentDescription = "사장님 프로필",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = cafeOwner.nickname,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RatingBadge(rating = cafeOwner.ratingStats.averageRating.toFloat())

                Text(
                    text = "리뷰 ${cafeOwner.ratingStats.totalReviews}개",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
private fun RatingBadge(rating: Float) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFFFF9E6)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFB800),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "$rating",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}

@Composable
private fun CafeInfoList(cafeDetail: CafeDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        InfoRow(
            icon = Icons.Default.Star,
            iconTint = Color(0xFFFFC107),
            text = "${cafeDetail.cafeOwner.ratingStats.averageRating} (${cafeDetail.cafeOwner.ratingStats.totalReviews})"
        )

        InfoRow(
            icon = Icons.Default.LocationOn,
            iconTint = Color(0xFFE53935),
            text = cafeDetail.address
        )

        InfoRow(
            icon = Icons.Default.AccessTime,
            iconTint = MaterialTheme.colorScheme.primary,
            text = "평일 ${cafeDetail.businessHoursStart}-${cafeDetail.businessHoursEnd}"
        )

        InfoRow(
            icon = Icons.Default.Phone,
            iconTint = MaterialTheme.colorScheme.primary,
            text = cafeDetail.contact
        )
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    iconTint: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
