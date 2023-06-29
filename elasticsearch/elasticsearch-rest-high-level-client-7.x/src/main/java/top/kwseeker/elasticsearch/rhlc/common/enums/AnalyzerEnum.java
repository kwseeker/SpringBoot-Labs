package top.kwseeker.elasticsearch.rhlc.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnalyzerEnum {

    IK_SMART("ik_smart"),
    IK_MAX_WORD("ik_max_word");

    private final String analyzerName;
}
