package com.rookies3.myspringbootlab.repository;


import com.rookies3.myspringbootlab.entity.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BookRepository의 기능을 테스트하는 클래스입니다.
 * @DataJpaTest 어노테이션을 사용하여 JPA 관련 컴포넌트만 로드합니다.
 */
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book testBook;

    /**
     * 각 테스트 실행 전에 기본 테스트 데이터를 설정합니다.
     */
    @BeforeEach
    void setUp() {
        System.out.println(">>> [setUp] BeforeEach 실행: 테스트용 데이터 저장 시작");
        // 테스트용 도서 데이터 생성 및 저장
        testBook = new Book("스프링 부트 입문", "홍길동", "9788956746425", 30000, LocalDate.of(2025, 5, 7));
        bookRepository.save(testBook);
        System.out.println(">>> [setUp] 테스트용 데이터 저장 완료: " + testBook.getTitle());
    }

    /**
     * 새로운 도서를 생성하고 저장하는 테스트입니다.
     */
    @Test
    @DisplayName("도서 등록 테스트")
    void testCreateBook() {
        System.out.println("--- [testCreateBook] 도서 등록 테스트 시작 ---");
        // Given: 새로운 도서 객체 생성
        Book newBook = new Book("JPA 프로그래밍", "박돌리", "9788956746432", 35000, LocalDate.of(2025, 4, 30));

        // When: 새로운 도서 저장
        Book savedBook = bookRepository.save(newBook);
        System.out.println(">>> [testCreateBook] 새로운 도서가 저장되었습니다. ID: " + savedBook.getId());

        // Then: 저장된 도서가 null이 아니고 ID가 할당되었는지 확인
        assertNotNull(savedBook, "저장된 도서는 null이 아니어야 합니다.");
        assertNotNull(savedBook.getId(), "저장된 도서에는 ID가 할당되어야 합니다.");
        assertEquals("JPA 프로그래밍", savedBook.getTitle(), "저장된 도서의 제목이 일치해야 합니다.");
        System.out.println("--- [testCreateBook] 도서 등록 테스트 성공 ---");
    }

    /**
     * ISBN으로 도서를 조회하는 테스트입니다.
     */
    @Test
    @DisplayName("ISBN으로 도서 조회 테스트")
    void testFindByIsbn() {
        System.out.println("--- [testFindByIsbn] ISBN으로 도서 조회 테스트 시작 ---");
        // When: ISBN으로 도서 조회
        Optional<Book> foundBook = bookRepository.findByIsbn(testBook.getIsbn());

        // Then: 도서가 존재하고 ISBN이 일치하는지 확인
        assertTrue(foundBook.isPresent(), "ISBN으로 도서를 찾을 수 있어야 합니다.");
        assertEquals(testBook.getIsbn(), foundBook.get().getIsbn(), "조회된 도서의 ISBN이 일치해야 합니다.");
        System.out.println(">>> [testFindByIsbn] ISBN(" + testBook.getIsbn() + ")으로 도서 조회 성공: " + foundBook.get().getTitle());
        System.out.println("--- [testFindByIsbn] ISBN으로 도서 조회 테스트 성공 ---");
    }

    /**
     * 저자명으로 도서 목록을 조회하는 테스트입니다.
     */
    @Test
    @DisplayName("저자명으로 도서 목록 조회 테스트")
    void testFindByAuthor() {
        System.out.println("--- [testFindByAuthor] 저자명으로 도서 목록 조회 테스트 시작 ---");
        // When: 저자명으로 도서 목록 조회
        List<Book> booksByAuthor = bookRepository.findByAuthor(testBook.getAuthor());

        // Then: 목록이 비어있지 않고, 저자명이 일치하는지 확인
        assertFalse(booksByAuthor.isEmpty(), "저자명으로 도서 목록을 찾을 수 있어야 합니다.");
        assertEquals(1, booksByAuthor.size(), "저자명으로 찾은 도서의 수는 1이어야 합니다.");
        assertEquals(testBook.getAuthor(), booksByAuthor.get(0).getAuthor(), "조회된 도서의 저자명이 일치해야 합니다.");
        System.out.println(">>> [testFindByAuthor] 저자(" + testBook.getAuthor() + ")로 도서 목록 조회 성공. 총 " + booksByAuthor.size() + "권.");
        System.out.println("--- [testFindByAuthor] 저자명으로 도서 목록 조회 테스트 성공 ---");
    }

    /**
     * 도서 정보를 수정하는 테스트입니다.
     */
    @Test
    @DisplayName("도서 정보 수정 테스트")
    void testUpdateBook() {
        System.out.println("--- [testUpdateBook] 도서 정보 수정 테스트 시작 ---");
        // Given: 기존 도서 정보 수정
        System.out.println(">>> [testUpdateBook] 기존 가격: " + testBook.getPrice());
        testBook.setPrice(40000);

        // When: 도서 정보 저장
        Book updatedBook = bookRepository.save(testBook);
        System.out.println(">>> [testUpdateBook] 새로운 가격: " + updatedBook.getPrice());

        // Then: 수정된 정보가 올바르게 반영되었는지 확인
        assertEquals(40000, updatedBook.getPrice(), "도서의 가격이 수정되어야 합니다.");
        System.out.println("--- [testUpdateBook] 도서 정보 수정 테스트 성공 ---");
    }

    /**
     * 도서 정보를 삭제하는 테스트입니다.
     */
    @Test
    @DisplayName("도서 삭제 테스트")
    void testDeleteBook() {
        System.out.println("--- [testDeleteBook] 도서 삭제 테스트 시작 ---");
        // When: 도서 삭제
        bookRepository.delete(testBook);
        System.out.println(">>> [testDeleteBook] 도서 삭제 완료. ID: " + testBook.getId());

        // Then: 삭제 후 도서가 존재하지 않는지 확인
        Optional<Book> deletedBook = bookRepository.findById(testBook.getId());
        assertFalse(deletedBook.isPresent(), "삭제된 도서는 존재하지 않아야 합니다.");
        System.out.println("--- [testDeleteBook] 도서 삭제 테스트 성공 ---");
    }
}
