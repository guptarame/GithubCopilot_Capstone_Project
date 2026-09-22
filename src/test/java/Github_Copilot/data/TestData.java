package Github_Copilot.data;

import java.util.List;

public final class TestData {

    public static final int DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS = 30;
    public static final int DEFAULT_WAIT_TIMEOUT_SECONDS = 12;
    public static final List<String> SUCCESS_MESSAGE_TOKENS = List.of("hello", "log out");
    public static final List<String> ERROR_MESSAGE_TOKENS = List.of("error", "incorrect", "invalid", "required");

    private TestData() {
    }
}
