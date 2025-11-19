package com.example.Backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "PhotoURL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PhotoURLID")
    private Integer id;

    @Column(name = "TeacherID", nullable = false)
    private Integer teacherId;

    @Column(name = "ImageURL", length = 500)
    private String imageUrl;

    @Column(name = "UploadedAt")
    private LocalDateTime uploadedAt;

    // 與 TeacherProfile 的關聯（唯讀關聯，實際寫入靠 teacherId）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TeacherID", insertable = false, updatable = false)
    @JsonIgnore
    private TeacherProfile teacher;
}
