package com.rookies4.myspringbootlab.service;

import com.rookies4.myspringbootlab.controller.dto.BookDTO;
import com.rookies4.myspringbootlab.entity.Book;
import com.rookies4.myspringbootlab.entity.BookDetail;
import com.rookies4.myspringbootlab.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    /** 전체 목록 */
    public List<BookDTO.Response> getAll() {
        return bookRepository.findAll()
                .stream().map(BookDTO.Response::fromEntity).toList();
    }

    /** ISBN 단건 조회 */
    public BookDTO.Response getByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("ISBN으로 책을 찾을 수 없습니다."));
        return BookDTO.Response.fromEntity(book);
    }

    /** 저자 검색 */
    public List<BookDTO.Response> searchByAuthor(String author) {
        return bookRepository.findByAuthor(author)
                .stream().map(BookDTO.Response::fromEntity).toList();
    }

    /** 제목 검색(부분 일치) */
    public List<BookDTO.Response> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream().map(BookDTO.Response::fromEntity).toList();
    }

    /** 생성 */
    @Transactional
    public BookDTO.Response create(BookDTO.Request request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException("이미 사용 중인 ISBN 입니다: " + request.getIsbn());
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setPublishDate(request.getPublishDate());

        Book saved = bookRepository.save(book);
        return BookDTO.Response.fromEntity(saved);
    }

    /** 전체 수정 (PUT) */
    @Transactional
    public BookDTO.Response update(Long id, BookDTO.Request request) {
        Book book = getExistBookById(id);

        // ISBN 변경 시에만 중복 체크
        if (!book.getIsbn().equals(request.getIsbn())
                && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException("이미 사용 중인 ISBN 입니다: " + request.getIsbn());
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setPublishDate(request.getPublishDate());

        return BookDTO.Response.fromEntity(book);
    }

    /** 삭제 */
    @Transactional
    public void deleteBook(Long id) {
        Book book = getExistBookById(id);
        bookRepository.delete(book);
    }

    private Book getExistBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 도서를 찾을 수 없습니다."));
    }

    @Transactional
    public BookDTO.Response patch(Long id, Map<String, Object> fields) {
        // Book 가져오기 (지연로딩으로 detail 접근 가능)
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 도서를 찾을 수 없습니다."));

        // ---- Book 필드들 부분 업데이트 ----
        if (fields.containsKey("title"))        book.setTitle((String) fields.get("title"));
        if (fields.containsKey("author"))       book.setAuthor((String) fields.get("author"));
        if (fields.containsKey("price"))        book.setPrice(toInteger(fields.get("price")));
        if (fields.containsKey("publishDate"))  book.setPublishDate(toLocalDate(fields.get("publishDate")));

        if (fields.containsKey("isbn")) {
            String newIsbn = (String) fields.get("isbn");
            if (newIsbn != null && !newIsbn.equals(book.getIsbn())
                    && bookRepository.existsByIsbn(newIsbn)) {
                throw new IllegalArgumentException("이미 사용 중인 ISBN 입니다: " + newIsbn);
            }
            book.setIsbn(newIsbn);
        }

        // ---- BookDetail 일부 업데이트 (중첩 JSON: bookDetail:{...}) ----
        if (fields.containsKey("bookDetail") && fields.get("bookDetail") instanceof Map<?,?> m) {
            @SuppressWarnings("unchecked")
            Map<String, Object> d = (Map<String, Object>) m;

            BookDetail detail = book.getBookDetail();
            if (detail == null) {
                detail = new BookDetail();
                book.setBookDetail(detail); // 양방향 연결
            }

            if (d.containsKey("description"))    detail.setDescription((String) d.get("description"));
            if (d.containsKey("language"))       detail.setLanguage((String) d.get("language"));
            if (d.containsKey("pageCount"))      detail.setPageCount(toInteger(d.get("pageCount")));
            if (d.containsKey("publisher"))      detail.setPublisher((String) d.get("publisher"));
            if (d.containsKey("coverImageUrl"))  detail.setCoverImageUrl((String) d.get("coverImageUrl"));
            if (d.containsKey("edition"))        detail.setEdition((String) d.get("edition"));
        }

        return BookDTO.Response.fromEntity(book);
    }

    /** (선택) BookDetail만 부분 수정하는 전용 PATCH */
    @Transactional
    public BookDTO.Response patchDetail(Long id, Map<String, Object> fields) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 도서를 찾을 수 없습니다."));

        BookDetail detail = book.getBookDetail();
        if (detail == null) {
            detail = new BookDetail();
            book.setBookDetail(detail);
        }

        if (fields.containsKey("description"))    detail.setDescription((String) fields.get("description"));
        if (fields.containsKey("language"))       detail.setLanguage((String) fields.get("language"));
        if (fields.containsKey("pageCount"))      detail.setPageCount(toInteger(fields.get("pageCount")));
        if (fields.containsKey("publisher"))      detail.setPublisher((String) fields.get("publisher"));
        if (fields.containsKey("coverImageUrl"))  detail.setCoverImageUrl((String) fields.get("coverImageUrl"));
        if (fields.containsKey("edition"))        detail.setEdition((String) fields.get("edition"));

        return BookDTO.Response.fromEntity(book);
    }

    /* ---------- 변환 헬퍼 ---------- */
    private Integer toInteger(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s && !s.isBlank()) return Integer.parseInt(s);
        return null;
    }
    private LocalDate toLocalDate(Object v) {
        if (v == null) return null;
        if (v instanceof LocalDate d) return d;
        if (v instanceof String s && !s.isBlank()) return LocalDate.parse(s); // "yyyy-MM-dd"
        throw new IllegalArgumentException("publishDate 형식이 올바르지 않습니다.(yyyy-MM-dd)");
    }
}
