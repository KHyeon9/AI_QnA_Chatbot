package com.chatbot.qna.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotel")
@CrossOrigin(origins = "http://localhost:5173/")
public class HotelController {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> hotelChatbot(@RequestParam("question") String question) {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        System.out.println("생성된 모델 확인 :" + chatClient);


        /**
         * MediaType.TEXT_EVENT_STREAM_VALUE : data 접두어가 붙음 (SSE표준)
         * MediaType.APPLICATION_NDJSON_VALUE : data 없음. 줄 단위 JSON
         * MediaType.TEXT_PLAIN_VALUE : 그냥 텍스트로 흘려보냄
         */
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest
                        .builder()
                        .query(question)
                        // 유사도 임계값 0.5 이상만 결과로 가져오기
                        .similarityThreshold(0.5)
                        .topK(1)
                        .build()
        );

        System.out.println("VectorStore 유사도 검색 결과 : " + results);

        String template = """
                당신은 어느 호텔 직원입니다. 문맥에 따라서 고객의 질문에 정중하게 답변해주세요.
                컨텍스트가 질문에 대답할 수 없는 경우, "죄송합니다. 모르겠습니다." 라고 대답하세요.
                컨텍스트:
                {context}
                질문:
                {question}
                """;
        /**
         * ChatModel을 사용해 직접 응답 생성
         * return chatModel.stream(template
         *         .replace("{context}", results.toString())
         *         .replace("{question}", question));
         */
        return chatClient
                .prompt()
                .user(
                        promptUserSpec -> promptUserSpec
                                .text(template)
                                .param("context", results)
                                .param("question", question)
                )
                .stream().content();
    }


}
