package com.example.prize;

public class User {

    private String name;         /* שם המשתמש */
    private String email;        /* כתובת אימייל */
    private String phone_num;    /* מספר טלפון */
    private String password;     /* סיסמה */

    // בנאי (Constructor)
    public User(String name, String email, String phone_num, String password) {
        this.name = name;
        this.email = email;
        this.phone_num = phone_num;
        this.password = password;
    }

    // === Getters & Setters ===

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone_num() { return phone_num; }
    public void setPhone_num(String phone_num) { this.phone_num = phone_num; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}



