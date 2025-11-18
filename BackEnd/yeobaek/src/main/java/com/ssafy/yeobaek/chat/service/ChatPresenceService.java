package com.ssafy.yeobaek.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 채팅방 실시간 접속자 상태를 관리하는 서비스
 * In-memory 방식으로 구현
 */
@Slf4j
@Service
public class ChatPresenceService {

    // Key: 채팅방 ID (Long), Value: 해당 채팅방에 접속한 사용자 ID Set (Long)
    private final Map<Long, Set<Long>> roomPresence = new ConcurrentHashMap<>();

    /**
     * 사용자가 채팅방에 입장했음을 기록
     * @param roomId 채팅방 ID
     * @param userId 사용자 ID
     */
    public void userJoined(Long roomId, Long userId) {
        // computeIfAbsent: Key가 없으면 새로운 Set을 생성하고, 있으면 기존 Set을 반환
        roomPresence.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
        log.info("사용자 입장 - 채팅방 ID: {}, 사용자 ID: {}. 현재 접속자: {}", roomId, userId, roomPresence.get(roomId));
    }

    /**
     * 사용자가 채팅방에서 퇴장했음을 기록
     * @param roomId 채팅방 ID
     * @param userId 사용자 ID
     */
    public void userLeft(Long roomId, Long userId) {
        Set<Long> userIds = roomPresence.get(roomId);
        if (userIds != null) {
            userIds.remove(userId);
            // 만약 방에 아무도 없으면 Map에서 해당 방 정보를 삭제하여 메모리 관리
            if (userIds.isEmpty()) {
                roomPresence.remove(roomId);
            }
            log.info("사용자 퇴장 - 채팅방 ID: {}, 사용자 ID: {}. 현재 접속자: {}", roomId, userId, roomPresence.get(roomId));
        }
    }

    /**
     * 특정 사용자가 특정 채팅방에 접속해 있는지 확인
     * @param roomId 채팅방 ID
     * @param userId 사용자 ID
     * @return 접속해 있으면 true, 아니면 false
     */
    public boolean isUserPresent(Long roomId, Long userId) {
        Set<Long> userIds = roomPresence.get(roomId);
        boolean isPresent = userIds != null && userIds.contains(userId);
        log.debug("사용자 접속 상태 확인 - 채팅방 ID: {}, 사용자 ID: {}, 상태: {}", roomId, userId, isPresent);
        return isPresent;
    }
}
