package com.Groupe5.user.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "test", schema = "public", catalog = "postgres")
public class TestEntity {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;

    @Id
    @Basic
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = true, length = -1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestEntity that = (TestEntity) o;
        return id == that.id && Objects.equals(name, that.name);
    }





}
