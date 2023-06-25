package com.sha.smartden;

public class EmployeeData {
    int id;
    String username;
    String email;
    String img;

    public EmployeeData(){}

    public EmployeeData(int id, String username, String email,String img) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

}
