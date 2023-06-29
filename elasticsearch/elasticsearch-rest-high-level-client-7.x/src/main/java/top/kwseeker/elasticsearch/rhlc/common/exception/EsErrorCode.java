package top.kwseeker.elasticsearch.rhlc.common.exception;

import lombok.Getter;

@Getter
public enum EsErrorCode {
    SUCCESS("0", "success"),
    FAILURE("1000", "something wrong"),
    ;

    private final String code;
    private final String message;

    EsErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
