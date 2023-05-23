package top.kwseeker.springboot.lab03.springsecurity.oauth2.mq;

import org.springframework.stereotype.Component;

/**
 * OAuth 2.0 客户端相关消息的 Producer
 */
@Component
//public class OAuth2ClientProducer extends AbstractBusProducer {
public class OAuth2ClientProducer {

    /**
     * 发送 {@link OAuth2ClientRefreshMessage} 消息
     */
    public void sendOAuth2ClientRefreshMessage() {
        //publishEvent(new OAuth2ClientRefreshMessage(this, getBusId(), selfDestinationService()));
    }

}
