package io.jzheaux.springsecurity.resolutions;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity(name="users")
public class User implements Serializable {
    @Id
    UUID id;

    @Column
    String username;

    @Column
    String password;

    @Column
    boolean enabled = true;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Collection<UserAuthority> userAuthorities = new ArrayList<>();

    public User() {
    }

    public User(String username, String password) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
    }

    public User(User other) {
        this(other.username, other.password);
        this.userAuthorities.addAll(other.userAuthorities);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Collection<UserAuthority> getUserAuthorities() {
        return Collections.unmodifiableCollection(userAuthorities);
    }

    public void grantAuthority(String authority) {
        UserAuthority userAuthority = new UserAuthority(authority, this);
        this.userAuthorities.add(userAuthority);
    }

}
