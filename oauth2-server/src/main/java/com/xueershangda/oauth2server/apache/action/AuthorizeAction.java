package com.xueershangda.oauth2server.apache.action;

import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * oauth 2.0 的 java server端。获取授权码 authorization code
 *
 * @author yinlei
 * @since 2019-8-23 10:39
 */
public class AuthorizeAction {
    //向客户端返回授权许可码 code.  此段代码对应开发步骤2
    @RequestMapping("/responseCode")
    public Object toShowUser(Model model, HttpServletRequest request) {
        System.out.println("----------服务端/responseCode--------------------------------------------------------------");

        try {
            //构建OAuth授权请求
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
          /*oauthRequest.getClientId();
          oauthRequest.getResponseType();
          oauthRequest.getRedirectURI();
          System.out.println(oauthRequest.getClientId());
          System.out.println(oauthRequest.getResponseType());
          System.out.println(oauthRequest.getRedirectURI());*/


            if (!StringUtils.isEmpty(oauthRequest.getClientId())) {
                //设置授权码
                String authorizationCode = "authorizationCode";
                //利用oauth授权请求设置responseType，目前仅支持CODE，另外还有TOKEN
                String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
                //进行OAuth响应构建
                OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                        OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);

                //设置授权码
                builder.setCode(authorizationCode);
                //得到到客户端重定向地址
                String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
                //构建响应
                final OAuthResponse response = builder.location(redirectURI).buildQueryMessage();

                System.out.println("服务端/responseCode内，返回的回调路径：" + response.getLocationUri());
                System.out.println("----------服务端/responseCode--------------------------------------------------------------");
                String responceUri = response.getLocationUri();


                //根据OAuthResponse返回ResponseEntity响应
                HttpHeaders headers = new HttpHeaders();

                try {
                    headers.setLocation(new URI(response.getLocationUri()));
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }

                return "redirect:" + responceUri;

            }


        } catch (OAuthSystemException e) {

            e.printStackTrace();

        } catch (OAuthProblemException e) {

            e.printStackTrace();

        }

        System.out.println("----------服务端/responseCode--------------------------------------------------------------");

        return null;
    }
}