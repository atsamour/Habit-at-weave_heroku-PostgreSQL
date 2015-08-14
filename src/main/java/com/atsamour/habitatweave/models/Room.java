/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */
package com.atsamour.habitatweave.models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Room implements Serializable {
    
    private static final long serialVersionUID = 72234543L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private int user_id;

    public Room(){
    }
    
    public Integer getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
