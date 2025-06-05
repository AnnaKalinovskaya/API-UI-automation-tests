import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

public class Admin {

    private String token;
    private String name;
    private String pass;

    private static Admin admin;

    public static Admin getInstance(){
        if (admin == null){
            admin = new Admin();
        }
        return admin;
    }

    private Admin (){
        this.name = name;
        this.pass = pass;
        this.token = given().
                contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                      {
                        "username": "admin",
                        "password": "admin"
                      }
                      """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .extract()
                .header("Authorization");
    }

    public User createUser(String name, String pass){
        var responseBody = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", this.token)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "%s",
                          "role": "USER"
                        }
                        """, name, pass)).
                post("http://localhost:4111/api/v1/admin/users")
                .then()
                .extract()
                .body();

        int id = responseBody.jsonPath().getInt("id");
        return new User(id, name, pass);
    }

    public boolean deleteUser(User user){
        int statusCode = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", this.token)
                .delete(String.format("http://localhost:4111/api/v1/admin/users/%s", user.getID()))
                .then()
                .extract().statusCode();

        return statusCode == 200;
    }
}
