package com.cmi.bache24.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by omar on 11/25/15.
 */
public class User implements Parcelable {

    private String id;
    private String name;
    private String email;
    private String password;
    private String picture;
    private String registerType;
    private String phone;
    private String firtsLastName;
    private String secondLastName;
    private String fbUsername;
    private String fbToken;
    private String twUsername;
    private String twToken;
    private int idDelegacion;
    private String hashToken;
    private String pictureUrl;
    private int userType;
    private String oldPassword;
    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeString(picture);
        parcel.writeString(registerType);
        parcel.writeString(phone);


        parcel.writeString(firtsLastName);
        parcel.writeString(secondLastName);
        parcel.writeString(fbToken);
        parcel.writeString(twToken);
        parcel.writeInt(idDelegacion);
        parcel.writeString(token);
        parcel.writeString(fbUsername);
        parcel.writeString(twUsername);

    }

    public static final Parcelable.Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel parcel) {
            User user = new User();

            user.setId(parcel.readString());
            user.setName(parcel.readString());
            user.setEmail(parcel.readString());
            user.setPassword(parcel.readString());
            user.setPicture(parcel.readString());
            user.setRegisterType(parcel.readString());
            user.setPhone(parcel.readString());

            user.setFirtsLastName(parcel.readString());
            user.setSecondLastName(parcel.readString());
            user.setFbToken(parcel.readString());
            user.setTwToken(parcel.readString());
            user.setIdDelegacion(parcel.readInt());
            user.setToken(parcel.readString());
            user.setFbUsername(parcel.readString());
            user.setTwUsername(parcel.readString());

            return user;
        }

        @Override
        public User[] newArray(int i) {
            return new User[i];
        }
    };

    public String getFirtsLastName() {
        return firtsLastName;
    }

    public void setFirtsLastName(String firtsLastName) {
        this.firtsLastName = firtsLastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

    public String getTwToken() {
        return twToken;
    }

    public void setTwToken(String twToken) {
        this.twToken = twToken;
    }

    public int getIdDelegacion() {
        return idDelegacion;
    }

    public void setIdDelegacion(int idDelegacion) {
        this.idDelegacion = idDelegacion;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFbUsername() {
        return fbUsername;
    }

    public void setFbUsername(String fbUsername) {
        this.fbUsername = fbUsername;
    }

    public String getTwUsername() {
        return twUsername;
    }

    public void setTwUsername(String twUsername) {
        this.twUsername = twUsername;
    }

    public String getHashToken() {
        return hashToken;
    }

    public void setHashToken(String hashToken) {
        this.hashToken = hashToken;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
