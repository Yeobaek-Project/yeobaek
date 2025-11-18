package com.ssafy.yeobaek.cafe.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "display_slot")
public class DisplaySlot {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private DisplayWall wall;

    @Column(name = "is_exhibiting", nullable = false)
    @Builder.Default
    private Boolean isExhibiting = false;

    @Column(name = "commission_rate", nullable = false)
    @Builder.Default
    private Double commissionRate = 0.0;

    @Column(name = "requirements")
    private String requirements;

    @Column(name = "requirements_embedding", columnDefinition = "TEXT")
    private String requirementsEmbedding;
}
