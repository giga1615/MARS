package com.mars.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Data
@Setter
@Getter
@ToString
@Entity
@Table(name = "friend")
public class FriendDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private int no;

    @Column(name = "myname")
    private String myname;

    @Column(name = "yourname")
    private String yourname;

    @Column(name = "myid")
    private String myid;

    @Column(name = "yourid")
    private String yourid;

    @Column(name="profileimage")
    private String profileimage;

    public void update(String profileimage){
        this.profileimage = profileimage;
    }
}