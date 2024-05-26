package com.Groupe5.user.model;
import java.io.Serializable;
import jakarta.persistence.*;


@Entity
@Table(name = "user", schema = "public", catalog = "postgres")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
    @Basic
    @Column(name = "name", nullable = false, length = 32)
     private String name;
    @Basic
    @Column(name = "email")
    private String email="";

    @Basic
    @Column(name = "password")
    private String password="";

    @Basic
    @Column(name = "dateCreation")
    private String dateCreation="";

    public User(String name, String email, String password, String dateCreation, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.dateCreation = dateCreation;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Basic
    @Column(name = "role")
    private String role="";

    public User(String name, String email) {

        this.name = name;
        this.email = email;
    }

    public User() {}

    public Long getId() {
        return id;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public String getMel() {
        return email;
    }

    public void setMel(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Identite {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
