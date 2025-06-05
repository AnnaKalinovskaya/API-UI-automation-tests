import io.restassured.http.ContentType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class User {

    private String authToken;
    private int id;
    private String userName;
    private String pass;

    private List<BankAccount> bankAccounts = new LinkedList<>();

    public User (int id, String name, String pass){
        this.id = id;
        this.userName = name;
        this.pass = pass;

        this.authToken = given().
                contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format("""
                        {
                          "username": "%s",
                          "password": "%s"
                        }
                        """, name, pass))
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .extract()
                .header("Authorization");
    }

    public String getAuthToken(){
        return this.authToken;
    }

    public String getUserName(){
        return this.userName;
    }

    public int getID(){
        return this.id;
    }

    public String getCustomerName(){
        return BankRequests.getCustomerProfile(this)
                .extract()
                .body().jsonPath().getString("name");
    }

    public BankAccount createBankAccount(){
        int accountId =BankRequests.createBankAccountRequest(this)
                .extract()
                .body().jsonPath().getInt("id");

        BankAccount newBankAccount = new BankAccount(accountId);
        this.bankAccounts.add(newBankAccount);
        return newBankAccount;
    }

    public BigDecimal getAccountBalance(int bankAccountID){
        String pathToBalanceValue = String.format("find { it.id == %d }.balance", bankAccountID);

        double balanceAsDouble = BankRequests.getBankAccountsRequest(this)
                .extract()
                .body().jsonPath().getDouble(pathToBalanceValue);

        return BigDecimal.valueOf(balanceAsDouble).setScale(2, RoundingMode.HALF_UP);
    }

}
