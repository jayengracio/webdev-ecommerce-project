package app;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import java.io.Serializable;

import java.util.Date;

public class User implements java.io.Serializable{
    private int id;
    private String username;
    private String password;
    private Product[] cart;

    public User(){}

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Product[] getCart() { return cart; }

    public void setCart(Product[] cart) { this.cart = cart; }


}
