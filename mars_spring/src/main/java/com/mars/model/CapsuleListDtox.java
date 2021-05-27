package com.mars.model;
//package com.mars.model;
//
//import lombok.*;
//
//import javax.persistence.*;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//@Setter
//@Entity
//@Table(name = "capsule_list")
//public class CapsuleListDto {
//
//
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "list_no")
//    int list_no;
//
//
//
//    @Column(name = "no")
//    int no;
//
//    @Column(name = "id", length = 100)
//    private String id;
//
//    @Column(name = "title", length = 100)
//    private String title;
//
//    //gps정보
//    @Column(name = "gps_x")
//    private double gps_x;
//
//    @Column(name = "gps_y")
//    private double gps_y;
//
//    @Column(name = "created_date", length = 200)
//    private String created_date;
//
//    @Column(name = "open_date", length = 200)
//    private String open_date;
//
//
//    @Lob
//    private String address;
//
//
//    @Lob
//    private String capsule_friends;
//
//    @Override
//    public String toString() {
//        return "{" +
//                "list_no:" + list_no +
//                ", no:" + no +
//                ", id:'" + id + '\'' +
//                ", title:'" + title + '\'' +
//                ", gps_x:" + gps_x +
//                ", gps_y:" + gps_y +
//                ", created_date:'" + created_date + '\'' +
//                ", open_date:'" + open_date + '\'' +
//                ", address:'" + address + '\'' +
//                ", capsule_friends:'" + capsule_friends + '\'' +
//                '}';
//    }
//}
