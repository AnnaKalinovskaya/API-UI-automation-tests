package api.skelethon.requests;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {

    CREATE_ACCOUNT("/accounts", BaseModel.class, BankAccountModel.class),
    USERS ("/admin/users", CreateUserRequestModel.class, UserProfileModel.class),
    UPDATE_CUSTOMER_PROFILE("/customer/profile", BaseModel.class, CustomerNameResponseModel.class),
    GET_CUSTOMER_PROFILE("/customer/profile", BaseModel.class, UserProfileModel.class),
    CUSTOMER_ACCOUNTS("/customer/accounts", BaseModel.class, BankAccountModel.class),
    AUTH_LOGIN("auth/login", CreateUserRequestModel.class, BaseModel.class),
    TRANSFER("/accounts/transfer", TransferRequestModel.class, TransferResponseModel.class),
    DEPOSIT("/accounts/deposit", DepositRequestModel.class, BankAccountModel.class);


    private final String endpoint;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;


}
