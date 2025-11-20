package com.ssafy.yeobaeg.presentation.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.profile.UserInfo
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.main.LocalUserRole
import com.ssafy.yeobaeg.ui.theme.*
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProfileEditScreen(
    userInfo: UserInfo,
    introduction:String?,
    viewModel: ProfileEditViewModel = hiltViewModel(),
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var nickname by rememberSaveable { mutableStateOf(userInfo.nickname) }
    var introduction by rememberSaveable { mutableStateOf(introduction) }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val userRole = LocalUserRole.current


    // 갤러리에서 이미지 선택
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { selectedImageUri = it }
    }

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "프로필 편집",
                onBackClick = viewModel::navigateBack,
                actionIcon = null,
                onActionClick = {}
            )
        },
        containerColor = YeobaegWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 프로필 이미지 섹션
            ProfileImageSection(
                imageUri = selectedImageUri,
                existingImageUrl = userInfo.profileImageUrl,
                onImageClick = { showBottomSheet = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "프로필 사진 변경",
                fontSize = 14.sp,
                color = YeobaegGray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 닉네임 입력 섹션
            NicknameInputSection(
                nickname = nickname,
                onNicknameChange = { nickname = it }
            )
            Spacer(modifier = Modifier.height(40.dp))

            if (userRole == UserRole.ARTIST) {
                IntroductionInputSection(
                    introduction = introduction,
                    onIntroductionChange = { introduction = it }
                )
                Spacer(modifier = Modifier.height(40.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // 저장하기 버튼
            SaveButton(
                onClick = {
                    viewModel.onSaveProfile(
                        context = context,
                        nickname = nickname,
                        introduction = introduction,
                        imageUri = selectedImageUri,
                    )
                }
            )

        }

        // 바텀시트 (사진 선택용)
        if (showBottomSheet) {
            ImagePickerBottomSheet(
                onDismiss = { showBottomSheet = false },
                onGalleryClick = {
                    showBottomSheet = false
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerBottomSheet(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "프로필 사진 변경",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = jansilFont,
                color = YeobaegBlack
            )

            Spacer(modifier = Modifier.height(24.dp))

            BottomSheetMenuItem(
                icon = R.drawable.ic_upload,
                text = "기기에서 사진 선택",
                onClick = onGalleryClick
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BottomSheetMenuItem(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            tint = YeobaegBrown,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 16.sp,
            color = YeobaegBlack,
            fontFamily = jansilFont,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun ProfileImageSection(
    imageUri: Uri?,
    existingImageUrl: String?,
    onImageClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.clickable(onClick = onImageClick)
    ) {
        when {
            // 새 이미지 선택 시
            imageUri != null -> {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "프로필 이미지",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFE0E0E0), CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // 서버에서 받은 URL 그대로 사용
            !existingImageUrl.isNullOrEmpty() -> {
                AsyncImage(
                    model = existingImageUrl,
                    contentDescription = "프로필 이미지",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFE0E0E0), CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // 기본 이미지
            else -> {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "기본 프로필 이미지",
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        // 카메라 아이콘 버튼
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(YeobaegBrown)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_camera),
                contentDescription = "카메라",
                tint = YeobaegWhite,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun NicknameInputSection(
    nickname: String,
    onNicknameChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "닉네임",
            fontFamily = jansilFont,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "닉네임을 입력하세요.",
                    color = YeobaegTextGray,
                    fontFamily = jansilFont,
                    fontWeight = FontWeight.Normal
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0E0E0),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )
    }
}

@Composable
fun IntroductionInputSection(
    introduction: String?,
    onIntroductionChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "소개글",
            fontFamily = jansilFont,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (introduction != null) {
            OutlinedTextField(
                value = introduction,
                onValueChange = onIntroductionChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                placeholder = {
                    Text(
                        text = "소개글을 입력하세요.",
                        color = YeobaegTextGray,
                        fontFamily = jansilFont,
                        fontWeight = FontWeight.Normal
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(
                    color = Color(0xFF111111),
                    fontFamily = jansilFont,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                maxLines = 5
            )
        }
    }
}


@Composable
fun SaveButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D4C13))
    ) {
        Text(
            text = "저장하기",
            fontSize = 16.sp,
            fontFamily = jansilFont,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ProfileEditScreenPreview() {
//    ProfileEditScreen()
//}
