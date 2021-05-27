package com.a403.mars.model;

public class Friend {
    int no;
    String myname;
    String yourname;
    String myid;
    String yourid;
    String profileimage;

    public Friend() {
    }

    public Friend(int no, String myname, String myid, String profileimage) {
        this.no = no;
        this.myname = myname;
        this.myid = myid;
        this.profileimage =profileimage;
    }

    public Friend(int no, String myname, String yourname, String myid, String yourid, String profileimage) {
        this.no = no;
        this.myname = myname;
        this.yourname = yourname;
        this.myid = myid;
        this.yourid = yourid;
        this.profileimage = profileimage;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getMyname() {
        return myname;
    }

    public void setMyname(String myname) {
        this.myname = myname;
    }

    public String getYourname() {
        return yourname;
    }

    public void setYourname(String yourname) {
        this.yourname = yourname;
    }

    public String getMyid() {
        return myid;
    }

    public void setMyid(String myid) {
        this.myid = myid;
    }

    public String getYourid() {
        return yourid;
    }

    public void setYourid(String yourid) {
        this.yourid = yourid;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    @Override
    public String toString() {
        return "Friend{" + "no=" + no + ", myname='" + myname + '\'' + ", yourname='" + yourname + '\'' + ", myid='"
                + myid + '\'' + ", yourid='" + yourid + '\'' + ", profileimage='" + profileimage + '\'' + '}';
    }
}
