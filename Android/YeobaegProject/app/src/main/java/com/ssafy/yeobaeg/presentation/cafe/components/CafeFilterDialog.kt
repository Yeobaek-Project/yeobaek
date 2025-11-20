package com.ssafy.yeobaeg.presentation.cafe.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssafy.yeobaeg.domain.model.cafe.CafeFilter
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown

@Composable
fun CafeFilterDialog(
    currentFilter: CafeFilter,
    onDismiss: () -> Unit,
    onApply: (CafeFilter) -> Unit,
    onClear: () -> Unit
) {
    var commissionRange by remember {
        mutableStateOf(
            (currentFilter.minCommission?.toFloat() ?: 0f)..(currentFilter.maxCommission?.toFloat()  ?: 15f)
        )
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = { }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
            // 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "필터",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기"
                    )
                }
            }
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 수수료 섹션
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "수수료 범위",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "${commissionRange.start.toInt()}% - ${commissionRange.endInclusive.toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = YeobaegBrown
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RangeSlider(
                            value = commissionRange,
                            onValueChange = { range ->
                                commissionRange = range.start.toInt().toFloat()..range.endInclusive.toInt().toFloat()
                            },
                            valueRange = 0f..15f,
                            steps = 14,
                            colors = SliderDefaults.colors(
                                thumbColor = YeobaegBrown,
                                activeTrackColor = YeobaegBrown,
                                inactiveTrackColor = Color(0xFFE0E0E0)
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "0%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "15%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // 하단 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        commissionRange = 0f..15f
                        onClear()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = YeobaegBrown
                    ),
                    border = BorderStroke(1.dp, YeobaegBrown)
                ) {
                    Text("초기화")
                }

                Button(
                    onClick = {
                        val filter = CafeFilter(
                            minWidth = null,
                            maxWidth = null,
                            minHeight = null,
                            maxHeight = null,
                            minCommission = commissionRange.start.toInt().toDouble(),
                            maxCommission = commissionRange.endInclusive.toInt().toDouble()
                        )
                        onApply(filter)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =YeobaegBrown
                    )
                ) {
                    Text("적용하기")
                }
            }
        }
    }
    }
}

