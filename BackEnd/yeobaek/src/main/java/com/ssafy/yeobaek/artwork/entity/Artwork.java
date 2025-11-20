package com.ssafy.yeobaek.artwork.entity;

import com.ssafy.yeobaek.common.enums.ArtCategory;
import com.ssafy.yeobaek.user.entity.Artist;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "artwork")
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "description_embedding", columnDefinition = "TEXT")
    private String descriptionEmbedding;

    @Enumerated(EnumType.STRING)
    @Column(name = "art_category", nullable = false, length = 20)
    private ArtCategory artCategory;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "width_cm", nullable = false)
    private Double widthCm;

    @Column(name = "height_cm", nullable = false)
    private Double heightCm;

    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    @Column(name = "production_year", nullable = false)
    private Integer productionYear;

    @Column(name = "is_exhibiting", nullable = false)
    @Builder.Default
    private Boolean isExhibiting = false;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "bookmark_count", nullable = false)
    @Builder.Default
    private Integer bookmarkCount = 0;

    @ElementCollection
    @CollectionTable(name = "artwork_image", joinColumns = @JoinColumn(name = "artwork_id"))
    @Column(name = "image_url")
    @OrderColumn(name = "display_order")
    private List<String> images = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 북마크 수 증가
     */
    public void incrementBookmarkCount() {
        this.bookmarkCount++;
    }

    /**
     * 북마크 수 감소
     */
    public void decrementBookmarkCount() {
        if (this.bookmarkCount > 0) {
            this.bookmarkCount--;
        }
    }
}
