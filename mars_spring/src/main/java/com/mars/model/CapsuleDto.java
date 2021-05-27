package com.mars.model;


import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "capsule")
public class CapsuleDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    int no;

    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "musicTitle", length = 200)
    private String music_title;

    @Column(name = "memo", length = 200)
    private String memo;

    //사진, 영상, 음성,
    @Lob
    private String photo_url;

    @Lob
    private String voice_url;

    @Lob
    private String video_url;

    //gps정보
    @Column(name = "gps_x")
    private double gps_x;

    @Column(name = "gps_y")
    private double gps_y;

    @Column(name = "created_date", length = 200)
    private String created_date;

    @Column(name = "open_date", length = 200)
    private String open_date;

    @Lob
    private String address;

    @Lob
    private String capsule_friends;
    
    @Lob
    private String capusle_frineds_by_name;

}
