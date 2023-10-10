package top.kwseeker.cloud.user.api.oauth2;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.kwseeker.cloud.common.pojo.CommonResult;
import top.kwseeker.cloud.user.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import top.kwseeker.cloud.user.enums.ApiConstants;

@FeignClient(name = "service-user")
public interface OAuth2TokenApi {

    String PREFIX = ApiConstants.PREFIX + "/oauth2/token";

    /**
     * 校验 Token 的 URL 地址，主要是提供给 Gateway 使用
     */
    String URL_CHECK = "http://" + ApiConstants.SERVICE_NAME + PREFIX + "/check";

    @GetMapping(PREFIX + "/check")
    CommonResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(@RequestParam("accessToken") String accessToken);
}
