package com.rookies3.myspringbootlab.controller;


import com.rookies3.myspringbootlab.entity.Book;
import com.rookies3.myspringbootlab.exception.BusinessException;
import com.rookies3.myspringbootlab.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookRestController {
    private final BookRepository bookRepository;

    @PostMapping
    public Book create(@RequestBody Book book){
        return bookRepository.save(book);
    }

    @GetMapping
    public List<Book> getBook(){
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public Book getBookID(@PathVariable Long id){
        Book existBook = getExistBookId(id);
        return existBook;
    }

    @GetMapping("/isbn/{isbn}/")
    public Book getBookISBN(@PathVariable String isbn){
        Book existBook = getExistBookISBN(isbn);

        return existBook;
    }

    @PutMapping("/{id}/")
    public Book putBook(@PathVariable Long id,@RequestBody Book bookDetail){
        Book existBook = getExistBookId(id);

        existBook.setTitle(bookDetail.getTitle());
        existBook.setAuthor(bookDetail.getAuthor());
        existBook.setIsbn(bookDetail.getIsbn());
        existBook.setPublishDate(bookDetail.getPublishDate());

        //DB에 저장
        Book updateBook = bookRepository.save(existBook);
        return updateBook;
    }

    @DeleteMapping("/{id}/")
    public ResponseEntity<?> deleteBook(@PathVariable Long id){
        Book existBook = getExistBookId(id);
        bookRepository.delete(existBook);
        return ResponseEntity.ok("도서가 삭제 되었습니다.");
    }

    private Book getExistBookId(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        Book existBook = optionalBook
                .orElseThrow(() -> new BusinessException("도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        return existBook;
    }
    private Book getExistBookISBN(String isbn) {
        Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);
        Book existBook = optionalBook
                .orElseThrow(() -> new BusinessException("도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        return existBook;
    }
}
