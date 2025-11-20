package com.ssafy.yeobaeg.presentation.cafe.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown

@Composable
fun ExhibitionSlotSectionHeader(slotCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "전시 슬롯",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF654321)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (slotCount > 0) "총 ${slotCount}개의 슬롯을 확인해보세요"
            else "등록된 전시 슬롯이 없습니다",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ExhibitionSlotEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "등록된 전시 슬롯이 없습니다",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DisplaySlotCard(
    slot: DisplaySlot,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SlotImage(
                    imageUrl = slot.exhibition?.artImgUrl,
                    slotName = slot.wName,
                    status = slot.status
                )

                SlotInfo(
                    slot = slot,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SlotImage(
    imageUrl: String?,
    slotName: String,
    status: SlotStatus
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF5F5F5))
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = slotName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.hallway_24px),
                    contentDescription = null,
                    tint = Color(0xFFBDBDBD),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        StatusBadge(status = status)
    }
}

@Composable
private fun BoxScope.StatusBadge(status: SlotStatus) {
    Surface(
        shape = RoundedCornerShape(bottomEnd = 8.dp),
        color = when (status) {
            SlotStatus.AVAILABLE -> Color(0xFF2196F3)
            SlotStatus.EXHIBITING -> Color(0xFF4CAF50)
            SlotStatus.RESERVED -> Color(0xFF2196F3)
            SlotStatus.UNAVAILABLE -> Color(0xFF9E9E9E)
        },
        modifier = Modifier.align(Alignment.TopStart)
    ) {
        Text(
            text = when (status) {
                SlotStatus.AVAILABLE -> "전시가능"
                SlotStatus.EXHIBITING -> "전시중"
                SlotStatus.RESERVED -> "전시예정"
                SlotStatus.UNAVAILABLE -> "불가능"
            },
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SlotInfo(
    slot: DisplaySlot,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = slot.exhibition?.artTitle ?: slot.wName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (slot.exhibition != null) {
            ExhibitionInfo(
                artist = slot.exhibition.artist,
                startDate = slot.exhibition.startDate,
                endDate = slot.exhibition.endDate
            )
        } else {
            EmptySlotInfo(
                sizeText = slot.sizeText,
                requirements = slot.requirements
            )
        }

        CommissionBadge(commission = slot.commission)
    }
}

@Composable
private fun ExhibitionInfo(
    artist: String,
    startDate: String,
    endDate: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color(0xFF757575),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = artist,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF757575)
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            tint = Color(0xFF757575),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$startDate - $endDate",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF757575)
        )
    }
}

@Composable
private fun EmptySlotInfo(
    sizeText: String,
    requirements: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AspectRatio,
            contentDescription = null,
            tint = Color(0xFF757575),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = sizeText,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF757575),
            fontWeight = FontWeight.Medium
        )
    }

    if (requirements.isNotEmpty()) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF757575),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = requirements,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF757575),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CommissionBadge(commission: Float) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFFFF3E0)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "수수료 ${commission.toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = YeobaegBrown,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
