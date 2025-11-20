package com.ssafy.yeobaek.recommendation.controller;

import com.ssafy.yeobaek.recommendation.service.EmbeddingBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/embeddings/batch")
@RequiredArgsConstructor
@Tag(name = "Embedding Batch (Admin)", description = "관리자용 임베딩 배치 생성 API")
public class EmbeddingBatchController {

    private final EmbeddingBatchService embeddingBatchService;

    @PostMapping("/artworks")
    @Operation(summary = "작품 임베딩 배치 생성", description = "임베딩이 없는 모든 작품에 대해 배치로 임베딩을 생성합니다.")
    public ResponseEntity<Map<String, Object>> batchGenerateArtworkEmbeddings() {
        int processedCount = embeddingBatchService.batchGenerateArtworkEmbeddings();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Artwork embeddings generated successfully");
        response.put("processedCount", processedCount);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/slots")
    @Operation(summary = "슬롯 임베딩 배치 생성", description = "임베딩이 없는 모든 디스플레이 슬롯에 대해 배치로 임베딩을 생성합니다.")
    public ResponseEntity<Map<String, Object>> batchGenerateSlotEmbeddings() {
        int processedCount = embeddingBatchService.batchGenerateSlotEmbeddings();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Slot embeddings generated successfully");
        response.put("processedCount", processedCount);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/all")
    @Operation(summary = "전체 임베딩 배치 생성", description = "임베딩이 없는 모든 작품과 슬롯에 대해 배치로 임베딩을 생성합니다.")
    public ResponseEntity<Map<String, Object>> batchGenerateAllEmbeddings() {
        int artworkCount = embeddingBatchService.batchGenerateArtworkEmbeddings();
        int slotCount = embeddingBatchService.batchGenerateSlotEmbeddings();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "All embeddings generated successfully");
        response.put("artworkCount", artworkCount);
        response.put("slotCount", slotCount);
        response.put("totalCount", artworkCount + slotCount);

        return ResponseEntity.ok(response);
    }
}
