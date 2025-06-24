package ui.pages;

import lombok.Getter;

public enum BankAlert {

    DEPOSIT_LESS_OR_EQUAL("Please deposit less or equal to 5000$."),
    SELECT_ACCOUNT("Please select an account."),
    ENTER_VALID_AMOUNT("Please enter a valid amount."),
    INVALID_TRANSFER("Invalid transfer: insufficient funds or invalid accounts"),
    FILL_ALL_FIELDS("Please fill all fields and confirm."),
    NAME_UPDATED_SUCCESSFULLY("Name updated successfully!"),
    NAME_MUST_CONTAIN("Name must contain two words with letters only");


    @Getter
    private final String message;

    BankAlert(String message){
        this.message = message;
    };
}
