package com.ssafy.yeobaek.exhibition.entity;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.common.enums.ExhibitionState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exhibition")
public class Exhibition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "display_slot_id", nullable = false)
    private DisplaySlot displaySlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id", nullable = false)
    private Artwork artwork;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "commission_rate", nullable = false)
    private Double commissionRate;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    @Builder.Default
    private ExhibitionState state = ExhibitionState.PENDING;

    @Column(name = "cafe_agreed", nullable = false)
    @Builder.Default
    private Boolean cafeAgreed = false;

    @Column(name = "artist_agreed", nullable = false)
    @Builder.Default
    private Boolean artistAgreed = false;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * 양쪽 모두 동의했는지 확인
     */
    public boolean isBothAgreed() {
        return cafeAgreed && artistAgreed;
    }

    /**
     * 전시 승인 처리 (양쪽 모두 동의 시 자동 호출)
     * Exhibition: PENDING → APPROVED
     * Artwork/DisplaySlot: 예약 상태는 Exhibition의 날짜로 관리
     */
    public void approve() {
        if (isBothAgreed()) {
            this.state = ExhibitionState.APPROVED;
            this.agreedAt = LocalDateTime.now();
        }
    }

    /**
     * 전시 시작 처리
     * Exhibition: APPROVED → ONGOING
     * Artwork/DisplaySlot: 전시 중으로 변경
     */
    public void start() {
        if (this.state == ExhibitionState.APPROVED) {
            this.state = ExhibitionState.ONGOING;
            this.displaySlot.setIsExhibiting(true);
            this.artwork.setIsExhibiting(true);
        }
    }

    /**
     * 전시 종료 처리
     * Exhibition: ONGOING → COMPLETED
     * Artwork/DisplaySlot: 사용 가능 상태로 변경
     */
    public void complete() {
        if (this.state == ExhibitionState.ONGOING) {
            this.state = ExhibitionState.COMPLETED;
            this.displaySlot.setIsExhibiting(false);
            this.artwork.setIsExhibiting(false);
        }
    }

    /**
     * 전시 취소 처리
     * Exhibition: PENDING/APPROVED → CANCELLED
     * Artwork/DisplaySlot: 사용 가능 상태로 변경
     */
    public void cancel() {
        if (this.state.isCancellable()) {
            this.state = ExhibitionState.CANCELLED;
            this.displaySlot.setIsExhibiting(false);
            this.artwork.setIsExhibiting(false);
        }
    }

    /**
     * 전시 제안 거절 처리
     * Exhibition: PENDING → REJECTED
     * Artwork/DisplaySlot: 사용 가능 상태로 변경
     */
    public void reject() {
        if (this.state == ExhibitionState.PENDING) {
            this.state = ExhibitionState.REJECTED;
            this.displaySlot.setIsExhibiting(false);
            this.artwork.setIsExhibiting(false);
        }
    }

    /**
     * 카페 사장이 동의 처리
     */
    public void agreeByCafe() {
        this.cafeAgreed = true;
        if (isBothAgreed()) {
            approve();
        }
    }

    /**
     * 작가가 동의 처리
     */
    public void agreeByArtist() {
        this.artistAgreed = true;
        if (isBothAgreed()) {
            approve();
        }
    }
}
