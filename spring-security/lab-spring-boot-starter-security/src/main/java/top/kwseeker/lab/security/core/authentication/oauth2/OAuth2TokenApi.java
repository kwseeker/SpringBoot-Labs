package top.kwseeker.lab.security.core.authentication.oauth2;

import org.springframework.web.bind.annotation.*;
import top.kwseeker.lab.security.core.authentication.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import top.kwseeker.lab.security.core.response.CommonResult;

public interface OAuth2TokenApi {

    //@PostMapping(PREFIX + "/create")
    //CommonResult<OAuth2AccessTokenRespDTO> createAccessToken(@Valid @RequestBody OAuth2AccessTokenCreateReqDTO reqDTO);

    CommonResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(@RequestParam("accessToken") String accessToken);

    //@DeleteMapping(PREFIX + "/remove")
    //CommonResult<OAuth2AccessTokenRespDTO> removeAccessToken(@RequestParam("accessToken") String accessToken);
    //
    //@PutMapping(PREFIX + "/refresh")
    //CommonResult<OAuth2AccessTokenRespDTO> refreshAccessToken(@RequestParam("refreshToken") String refreshToken,
    //                                                          @RequestParam("clientId") String clientId);
}