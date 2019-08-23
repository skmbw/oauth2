package com.xueershangda.oauth2client.apache.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OAuth 2.0 的 Java客户端
 *
 * @author yinlei
 * @since 2019-8-23 10:19
 */
@Controller
@RequestMapping("/oauth2Client")
public class OAuth2ClientAction {
    private static final Logger LOGGER = LogManager.getLogger(OAuth2ClientAction.class);

    String clientId = null;
    String clientSecret = null;
    String accessTokenUrl = null;
    String userInfoUrl = null;
    String redirectUrl = null;
    String response_type = null;
    String code = null;

    /**
     * 此段代码对应开发步骤1.其中accessTokenUrl是服务端返回code的controller方法映射地址。
     * redirectUrl是告诉服务端，code要传回客户端的一个controller方法，该方法的映射地址就是redirectUrl。
     *
     * @param request
     * @param response
     * @param attr
     * @return
     * @throws OAuthProblemException
     */
    // 提交申请code的请求，向服务端请求授权码code
    @RequestMapping("/requestServerCode")
    public String requestServerCode(HttpServletRequest request, HttpServletResponse response, RedirectAttributes attr) throws OAuthProblemException {

        clientId = "clientId";
        clientSecret = "clientSecret";
        accessTokenUrl = "responseCode";
        redirectUrl = "http://localhost:8081/oauthclient01/server/callbackCode";
        response_type = "code";

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        String requestUrl = null;

        try {
            //构建oauthd的请求。设置请求服务地址（accessTokenUrl）、clientId、response_type、redirectUrl
            OAuthClientRequest accessTokenRequest = OAuthClientRequest
                    .authorizationLocation(accessTokenUrl)
                    .setResponseType(response_type)
                    .setClientId(clientId)
                    .setRedirectURI(redirectUrl)
                    .buildQueryMessage();
            requestUrl = accessTokenRequest.getLocationUri();

            LOGGER.info(requestUrl);
        } catch (OAuthSystemException e) {
            LOGGER.error(e);
        }
        return "redirect:http://localhost:8082/oauthserver/" + requestUrl;
    }

    /**
     * 此方法对应开发步骤3的全部和步骤4的一半，也就是还包括接受服务端返回的access token。
     * 最后的redirect地址不是服务端的地址，只是将此token传进客户端的另一个方法，该方法就是最后的资源请求方法。
     *
     * @param request
     * @return
     * @throws OAuthProblemException
     */
    // 接受客户端返回的code，提交申请access token的请求。申请授权码的回调（授权码服务端会回调）
    // 向服务端请求资源(访问令牌access token)
    @RequestMapping("/callbackCode")
    public Object toLogin(HttpServletRequest request) throws OAuthProblemException {

        System.out.println("-----------客户端/callbackCode--------------------------------------------------------------------------------");

        clientId = "clientId";
        clientSecret = "clientSecret";
        accessTokenUrl = "http://localhost:8082/oauthserver/responseAccessToken";
        userInfoUrl = "userInfoUrl";
        redirectUrl = "http://localhost:8081/oauthclient01/server/accessToken";

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        code = httpRequest.getParameter("code");

        System.out.println(code);

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        try {
            OAuthClientRequest accessTokenRequest = OAuthClientRequest
                    .tokenLocation(accessTokenUrl)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setCode(code)
                    .setRedirectURI(redirectUrl)
                    .buildQueryMessage();

            //去服务端请求access token，并返回响应
            OAuthAccessTokenResponse oAuthResponse = oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST);
            //获取服务端返回过来的access token
            String accessToken = oAuthResponse.getAccessToken();
            //查看access token是否过期
            Long expiresIn = oAuthResponse.getExpiresIn();

            System.out.println("客户端/callbackCode方法的token：：：" + accessToken);
            System.out.println("-----------客户端/callbackCode--------------------------------------------------------------------------------");
            return "redirect:http://localhost:8081/oauthclient01/server/accessToken?accessToken=" + accessToken;
        } catch (OAuthSystemException e) {
            LOGGER.error(e);
        }
        return null;
    }

    /**
     * 利用服务端给的access_token去请求服务端的资源
     *
     * @param accessToken
     * @return
     */
    // 此方法对应开发步骤5的全部和步骤6的一半，也就是还包括接受服务端返回的资源信息。
    // 接受服务端传回来的access token，由此token去请求服务端的资源（用户信息等）
    @RequestMapping("/accessToken")
    public ModelAndView accessToken(String accessToken) {
        System.out.println("---------客户端/accessToken----------------------------------------------------------------------------------");
        userInfoUrl = "http://localhost:8082/oauthserver/userInfo";

        System.out.println("accessToken");

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        try {
            OAuthClientRequest userInfoRequest = new OAuthBearerClientRequest(userInfoUrl)
                    .setAccessToken(accessToken).buildQueryMessage();

            OAuthResourceResponse resourceResponse = oAuthClient.resource(userInfoRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            String username = resourceResponse.getBody();

            System.out.println(username);
            ModelAndView modelAndView = new ModelAndView("usernamePage");
            modelAndView.addObject("username", username);
            System.out.println("---------客户端/accessToken----------------------------------------------------------------------------------");
            return modelAndView;
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }
        System.out.println("---------客户端/accessToken----------------------------------------------------------------------------------");
        return null;
    }
}

