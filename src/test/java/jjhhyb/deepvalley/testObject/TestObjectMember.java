package jjhhyb.deepvalley.testObject;

import jjhhyb.deepvalley.user.entity.Member;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestObjectMember {
    public static Member createMemberWithUuid(String uuid) {
        return Member.builder()
                .memberId(1L)
                .loginEmail(uuid)
                .name("test")
                .password("test")
                .profileImageUrl("http://example.com/profileimage.jpg")
                .createdDate(LocalDateTime.now())
                .loginDate(LocalDateTime.now())
                .description("test")
                .build();
    }
}
