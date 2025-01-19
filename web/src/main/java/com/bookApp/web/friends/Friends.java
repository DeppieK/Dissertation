package com.bookApp.web.friends;


import ch.qos.logback.core.status.Status;
import com.bookApp.web.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SpringBootApplication
@ComponentScan("com.bookApp.web")
@Entity
@Table(name = "friends", uniqueConstraints = @UniqueConstraint(columnNames = {"sender_id", "receiver_id"}))
public class Friends {
    @Id
    @SequenceGenerator(
            name = "friends_sequence",
            sequenceName = "friends_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "friends_sequence"
    )

    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private LocalDateTime dateUpdated;

    @Override
    public String toString() {
        return "Friends{" +
                "id=" + id +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", status=" + status +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                '}';
    }

    public enum Status {
        PENDING, ACCEPTED, DECLINED
    }

    public String getAmountOfTime(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeSent = dateUpdated;

        long years = java.time.temporal.ChronoUnit.YEARS.between(timeSent, now);
        if (years > 0){
            return years + (years == 1 ? " year ago" : " years ago");
        }

        long months = java.time.temporal.ChronoUnit.MONTHS.between(timeSent, now);
        if (months > 0){
            return months + (months == 1 ? " month ago" : " months ago");
        }

        long days = java.time.temporal.ChronoUnit.DAYS.between(timeSent, now);
        if (days > 0){
            return days + (days == 1 ? " day ago" : " days ago");
        }

        long hours = java.time.temporal.ChronoUnit.HOURS.between(timeSent, now);
        if (hours > 0){
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }

        return "Just now";
    }

}
