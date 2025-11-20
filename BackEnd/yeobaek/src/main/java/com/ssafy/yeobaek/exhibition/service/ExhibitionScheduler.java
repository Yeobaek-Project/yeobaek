package com.ssafy.yeobaek.exhibition.service;

import com.ssafy.yeobaek.chat.dto.response.ChatRoomData;
import com.ssafy.yeobaek.chat.service.ChatService;
import com.ssafy.yeobaek.common.enums.ExhibitionState;
import com.ssafy.yeobaek.common.enums.MessageType;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.exhibition.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExhibitionScheduler {

    private final ExhibitionRepository exhibitionRepository;
    private final ChatService chatService;

    // 매일 자정 실행
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void manageExhibitionStatus() {
        log.info("매일 자정 전시 상태 업데이트 스케줄러 실행");
        startExhibitions();
        completeExhibitions();
    }

    private void startExhibitions() {
        LocalDate today = LocalDate.now();
        List<Exhibition> exhibitionsToStart = exhibitionRepository.findAllByStateAndStartDate(ExhibitionState.APPROVED, today);

        log.info("오늘 시작하는 전시 {}개를 확인했습니다.", exhibitionsToStart.size());

        for (Exhibition exhibition : exhibitionsToStart) {
            try {
                // 1. 전시 상태 변경 (APPROVED -> ONGOING)
                exhibition.start();

                // 2. 채팅방에 시스템 메시지 전송
                Long cafeOwnerId = exhibition.getCafe().getId();
                Long artistId = exhibition.getArtwork().getArtist().getId();
                ChatRoomData chatRoomData = chatService.createChatRoom(cafeOwnerId, artistId);
                Long chatRoomId = chatRoomData.getChatRoom().getChatRoomId();

                chatService.sendSystemMessage(chatRoomId, null, MessageType.STARTED, exhibition, null);

                log.info("전시 ID {}가 시작되었습니다.", exhibition.getId());

            } catch (Exception e) {
                log.error("전시 ID {} 시작 처리 중 오류 발생: {}", exhibition.getId(), e.getMessage());
            }
        }
    }

    private void completeExhibitions() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Exhibition> exhibitionsToComplete = exhibitionRepository.findAllByStateAndEndDate(ExhibitionState.ONGOING, yesterday);

        log.info("어제 종료된 전시 {}개를 확인했습니다.", exhibitionsToComplete.size());

        for (Exhibition exhibition : exhibitionsToComplete) {
            try {
                // 1. 전시 상태 변경 (ONGOING -> COMPLETED)
                exhibition.complete();

                // 2. 채팅방에 시스템 메시지 전송
                Long cafeOwnerId = exhibition.getCafe().getId();
                Long artistId = exhibition.getArtwork().getArtist().getId();
                ChatRoomData chatRoomData = chatService.createChatRoom(cafeOwnerId, artistId);
                Long chatRoomId = chatRoomData.getChatRoom().getChatRoomId();

                chatService.sendSystemMessage(chatRoomId, null, MessageType.ENDED, exhibition, null);

                log.info("전시 ID {}가 종료되었습니다.", exhibition.getId());

            } catch (Exception e) {
                log.error("전시 ID {} 종료 처리 중 오류 발생: {}", exhibition.getId(), e.getMessage());
            }
        }
    }
}
