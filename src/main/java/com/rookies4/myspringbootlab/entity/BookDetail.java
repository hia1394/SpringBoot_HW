package com.rookies4.myspringbootlab.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book_details")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "book_id", unique = true) // 외래키 + 유니크 → 1:1 보장
    private Book book;

    @Column(length = 2000) private String description;
    private String language;
    private Integer pageCount;
    private String publisher;
    private String coverImageUrl;
    private String edition;
}
