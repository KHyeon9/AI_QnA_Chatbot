package com.chatbot.qna.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chatbot.qna.service.HotelEmbeddingService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotel")
public class DocumentUploadController {

    private final HotelEmbeddingService hotelEmbeddingService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadText(@RequestParam("file") MultipartFile file) {
        try {
            hotelEmbeddingService.processUploadText(file);
            return ResponseEntity.ok("Text Upload 및 Embedding 완료");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("처리 중 오류 발생 : " + e.getMessage());
        }
    }
}
