package groupbuy_service.global;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final int status;
    private final String msg;

    @Builder
    public ErrorResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static ErrorResponse from (ErrorCode errorCode) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus())
                .msg(errorCode.getMsg())
                .build();
    }
}
