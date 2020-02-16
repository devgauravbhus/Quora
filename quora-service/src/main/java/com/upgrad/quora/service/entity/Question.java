package com.upgrad.quora.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/*

CREATE TABLE IF NOT EXISTS QUESTION
(id SERIAL,
uuid VARCHAR(200) NOT NULL,
content VARCHAR(500) NOT NULL,
 date TIMESTAMP NOT NULL ,
 user_id INTEGER NOT NULL,
 PRIMARY KEY(id), FOREIGN KEY (user_id)
 REFERENCES USERS(id) ON DELETE CASCADE);

 */
@Entity
@Table(name="QUESTION")

public class Question implements Serializable {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "UUID")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "content")
    @NotNull
    @Size(max = 200)
    private String content;

    @Column(name = "date")
    @NotNull
    @Size(max = 200)
    private String  date;

    @OneToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }





}
