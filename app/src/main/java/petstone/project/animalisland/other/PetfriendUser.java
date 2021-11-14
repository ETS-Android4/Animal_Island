package petstone.project.animalisland.other;

import android.net.Uri;
import android.widget.ImageView;

public class PetfriendUser {
/*
    public PetfriendUser(String uid)
    {
        this.uid = uid;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
 */




    public PetfriendUser(String uid, String nickname, String address, String carrerImgUri) {
        this.uid = uid;
        this.nickname = nickname;
        this.address = address;
        this.carrerImgUri = carrerImgUri;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String uid;
    private String nickname;
    private String address;
    private String carrerImgUri;


    public String getCarrerImgUri() {
        return carrerImgUri;
    }

    public void setCarrerImgUri(String carrerImgUri) {
        this.carrerImgUri = carrerImgUri;
    }
}