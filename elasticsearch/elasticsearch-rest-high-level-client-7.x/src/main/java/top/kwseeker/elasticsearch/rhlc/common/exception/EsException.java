package top.kwseeker.elasticsearch.rhlc.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EsException extends RuntimeException {

    private String code;
    private String message;

    public EsException(EsErrorCode esErrorCode, Throwable cause) {
        super(esErrorCode.getMessage(), cause);
        this.code = esErrorCode.getCode();
        this.message = esErrorCode.getMessage();
    }

    public EsException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}


