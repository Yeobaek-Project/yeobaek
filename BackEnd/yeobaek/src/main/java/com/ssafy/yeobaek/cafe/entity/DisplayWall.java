package com.ssafy.yeobaek.cafe.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "display_wall")
public class DisplayWall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @Column(name = "wall_name", nullable = false)
    private String wallName;

    @Column(name = "start_x", nullable = false)
    private Double startX;

    @Column(name = "start_y", nullable = false)
    private Double startY;

    @Column(name = "end_x", nullable = false)
    private Double endX;

    @Column(name = "end_y", nullable = false)
    private Double endY;

    @Column(name = "width_cm", nullable = false)
    private Double widthCm;

    @Column(name = "height_cm")
    private Double heightCm;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @OneToOne(mappedBy = "wall", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private DisplaySlot slot;
}
