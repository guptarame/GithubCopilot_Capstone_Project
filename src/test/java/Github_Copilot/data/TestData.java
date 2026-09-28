package Github_Copilot.data;

import java.util.List;

public final class TestData {

    public static final int DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS = 10;
    public static final int DEFAULT_WAIT_TIMEOUT_SECONDS = 10;
    public static final int TERMINAL_OUTCOME_TIMEOUT_SECONDS = 10;
    public static final List<String> SUCCESS_MESSAGE_TOKENS = List.of("hello", "log out");
    public static final List<String> INVALID_PASSWORD_MESSAGE_TOKENS = List.of("incorrect password", "password is incorrect");
    public static final List<String> UNKNOWN_USER_MESSAGE_TOKENS = List.of("unknown username", "unknown email", "invalid username", "not registered", "not found");
    public static final List<String> REQUIRED_FIELD_MESSAGE_TOKENS = List.of("fill out", "required", "complete");

    private TestData() {
    }
}
