package top.kwseeker.springboot.lab03.springsecurity.common.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.kwseeker.lab.security.core.authentication.user.IntArrayValuable;

import java.util.Arrays;

/**
 * 错误码的类型枚举
 *
 * @author dylan
 */
@AllArgsConstructor
@Getter
public enum ErrorCodeTypeEnum implements IntArrayValuable {

    /**
     * 自动生成
     */
    AUTO_GENERATION(1),
    /**
     * 手动编辑
     */
    MANUAL_OPERATION(2);

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(ErrorCodeTypeEnum::getType).toArray();

    /**
     * 类型
     */
    private final Integer type;

    @Override
    public int[] array() {
        return ARRAYS;
    }

}
