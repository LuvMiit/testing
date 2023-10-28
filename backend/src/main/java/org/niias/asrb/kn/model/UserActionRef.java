package org.niias.asrb.kn.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.niias.asrb.model.User;

import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public class UserActionRef {

    public UserActionRef(){}


    public UserActionRef(UserKn user){
        this(user.getUser());
    }

    public UserActionRef(User user){
        this.id = user.getId();
        this.name = user.getFio();
    }

    public UserActionRef(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    private Integer id;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate date;

    public Integer getId() {
        return id;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                '}';
    }
}
