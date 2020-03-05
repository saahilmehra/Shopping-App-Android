package com.saahil.ssshopping.Model;

public class Users {
    public String name, contact, password;

    public Users() {
    }

    public Users(String name, String contact, String password) {
        this.name = name;
        this.contact = contact;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
