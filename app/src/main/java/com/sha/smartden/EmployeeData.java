package com.sha.smartden;

public class EmployeeData {

    int Minerid;
    String username;
    String email;
    String img;
    public EmployeeData(){}
    public EmployeeData(int minerid, String username, String email,String img) {
        this.Minerid = minerid;
        this.username = username;
        this.email = email;
        this.img = img;
    }


    public int getMinerid() {
        return Minerid;
    }

    public void setMinerid(int minerid) {
        Minerid = minerid;
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
