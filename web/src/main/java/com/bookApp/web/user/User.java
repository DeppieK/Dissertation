package com.bookApp.web.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;

@Setter
@Getter
@SpringBootApplication
@ComponentScan("com.bookApp.web")
@Entity
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;

    private String firstname;
    private String lastname;
    private String username;
    private String password;

    private String email;
    private LocalDateTime dateOfRegistration;
    private LocalDateTime lastLogIn;

    //Constructors
    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, String firstname, String lastname, String username, String password, String email, LocalDateTime dateOfRegistration, LocalDateTime lastLogIn) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.dateOfRegistration = dateOfRegistration;
        this.lastLogIn = lastLogIn;
    }

    //ToString

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", dateOfRegistration=" + dateOfRegistration +
                ", lastLogIn=" + lastLogIn +
                '}';
    }
}