package com.rookies4.myspringbootlab.controller;

import com.rookies4.myspringbootlab.controller.dto.BookDTO;
import com.rookies4.myspringbootlab.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // 전체 목록
    @GetMapping
    public ResponseEntity<List<BookDTO.Response>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    // ISBN 조회
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO.Response> getByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getByIsbn(isbn));
    }

    // 저자 검색
    @GetMapping("/search/author")
    public ResponseEntity<List<BookDTO.Response>> searchByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookService.searchByAuthor(author));
    }

    // 제목 검색
    @GetMapping("/search/title")
    public ResponseEntity<List<BookDTO.Response>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchByTitle(title));
    }

    // 생성
    @PostMapping
    public ResponseEntity<BookDTO.Response> create(@RequestBody BookDTO.Request request) {
        return ResponseEntity.ok(bookService.create(request));
    }

    // 전체 수정
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO.Response> update(@PathVariable Long id,
                                                   @RequestBody BookDTO.Request request) {
        return ResponseEntity.ok(bookService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookDTO.Response> patch(
            @PathVariable Long id,
            @RequestBody Map<String, Object> fields) {
        return ResponseEntity.ok(bookService.patch(id, fields));
    }

    @PatchMapping("/{id}/detail")
    public ResponseEntity<BookDTO.Response> patchDetail(
            @PathVariable Long id,
            @RequestBody Map<String, Object> fields) {
        return ResponseEntity.ok(bookService.patchDetail(id, fields));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("도서가 삭제되었습니다.");
    }
}
