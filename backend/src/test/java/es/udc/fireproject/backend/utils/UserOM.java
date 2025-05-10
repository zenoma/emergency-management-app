package es.udc.fireproject.backend.utils;

import es.udc.fireproject.backend.model.entities.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserOM {

  public static User withDefaultValues() {
    return new User("email@email.com",
        "password",
        "FirstName",
        "LastName",
        "12345678A",
        123456789);
  }

  public static List<User> withRandomNames(int itemNumber) {

    List<User> userList = new ArrayList<>();
    for (int i = 0; i < itemNumber; i++) {
      String randomString = usingUUID();
      User user = new User(randomString.substring(0, 8) + "@email.com",
          "password",
          "FirstName",
          "LastName",
          "12345678A",
          123456789);
      userList.add(user);
    }
    return userList;
  }


  public static User withInvalidValues() {
    return new User("",
        "",
        "",
        "",
        "",
        -1);
  }

  private static String usingUUID() {
    UUID randomUUID = UUID.randomUUID();
    return randomUUID.toString().replaceAll("-", "");
  }
}
