package com.rookies4.myspringbootlab.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "books")
@Getter @Setter
public class Book {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)  private String title;
    @Column(nullable = false)  private String author;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;

    // === 여기부터 1:1 매핑 ===
    @OneToOne(mappedBy = "book", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private BookDetail bookDetail;

    /** 연관관계 편의 메서드 */
    public void setBookDetail(BookDetail detail) {
        this.bookDetail = detail;
        if (detail != null) detail.setBook(this);
    }

    // ---- 레거시 호환용(기존 코드가 getDetail()/setDetail()을 썼다면) ----
    @Deprecated public BookDetail getDetail() { return bookDetail; }
    @Deprecated public void setDetail(BookDetail detail) { setBookDetail(detail); }
}
