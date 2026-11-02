package Github_Copilot.data;

import java.util.List;

public final class TestData {

    public static final int DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS = 10;
    public static final int DEFAULT_WAIT_TIMEOUT_SECONDS = 10;
    public static final int TERMINAL_OUTCOME_TIMEOUT_SECONDS = 10;
    public static final List<String> INVALID_PASSWORD_MESSAGE_TOKENS = List.of(
            "incorrect password", "password is incorrect", "password you entered for the username");
    // These phrases explicitly tie a missing/unregistered result to an account identifier.
    // Generic "not found", "invalid username", and password/resource errors are intentionally excluded.
    public static final List<String> UNKNOWN_USER_MESSAGE_TOKENS = List.of(
            "unknown username", "unknown email", "unknown account",
            "username does not exist", "email does not exist", "account does not exist", "user does not exist",
            "username is not registered", "email is not registered",
            "account is not registered", "user is not registered",
            "unregistered username", "unregistered email", "unregistered account");
    public static final List<String> REQUIRED_FIELD_MESSAGE_TOKENS = List.of("fill out", "required", "complete");
    public static final List<String> BLANK_USERNAME_MESSAGE_TOKENS = List.of("username", "user name", "required");
    public static final List<String> BLANK_PASSWORD_MESSAGE_TOKENS = List.of("password", "required");

    private TestData() {
    }
}
