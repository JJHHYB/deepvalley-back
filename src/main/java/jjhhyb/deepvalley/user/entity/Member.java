package jjhhyb.deepvalley.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@Table(name = "MEMBER")
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MEMBER_ID")
    private Long memberId;

    @Column(name="LOGIN_EMAIL")
    private String loginEmail;

    @Column(name="NAME")
    private String name;

    @Column(name="PASSWORD")
    private String password;

    @Column(name="PROFILE_IMAGE_URL")
    private String profileImageUrl;

    @CreatedDate
    @Column(name="CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name="LOGIN_DATE")
    private LocalDateTime loginDate;

    @Column(name="DESCRIPTION")
    private String description;
}

