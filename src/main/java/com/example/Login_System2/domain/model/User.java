package com.example.Login_System2.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Assignment ilişkileri
    @OneToMany(mappedBy = "assignedBy", cascade = CascadeType.ALL)
    private List<TaskAssignment> assignmentsGiven = new ArrayList<>();

    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL)
    private List<TaskAssignment> assignmentsReceived = new ArrayList<>();

    @OneToMany(mappedBy = "currentAssignee", cascade = CascadeType.ALL)
    private List<Task> currentlyAssignedTasks = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id &&
               Objects.equals(email, user.email) &&
               Objects.equals(password, user.password);            
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password);
    }
}
