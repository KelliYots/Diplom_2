package stellar_burgers;

import com.github.javafaker.Faker;
import lombok.Getter;

@Getter
public class User {
    static Faker fucker = new Faker();
    private String name;
    private String email;
    private String password;

    public User createFuckerUser() {
        name = fucker.pokemon().name();
        email = fucker.internet().emailAddress().toLowerCase();
        password = "makeLoveNotWar";
        return this;
    }
}
