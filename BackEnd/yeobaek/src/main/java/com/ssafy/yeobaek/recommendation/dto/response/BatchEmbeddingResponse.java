package com.ssafy.yeobaek.recommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BatchEmbeddingResponse {
    private List<List<Double>> embeddings;
}
