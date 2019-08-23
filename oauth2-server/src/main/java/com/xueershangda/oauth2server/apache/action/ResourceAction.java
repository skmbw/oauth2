package com.xueershangda.oauth2server.apache.action;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * oauth 2 获取最终的用户资源。向客户端返回请求资源
 *
 * @author yinlei
 * @since 2019-8-23 10:56
 */
@Controller
public class ResourceAction {
    // 此代码对应开发步骤6的前一半。即服务端验证access token、并将资源信息给客户端
    @RequestMapping("/userInfo")
    public HttpEntity userInfo(HttpServletRequest request) throws OAuthSystemException {
        System.out.println("-----------服务端/userInfo-------------------------------------------------------------");
        try {
            //获取客户端传来的OAuth资源请求
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);
            //获取Access Token
            String accessToken = oauthRequest.getAccessToken();
            System.out.println("accessToken");
            //验证Access Token
  /*if (accessToken==null||accessToken=="") {
 // 如果不存在/过期了，返回未验证错误，需重新验证

  OAuthResponse oauthResponse = OAuthRSResponse
 .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
 .setError(OAuthError.ResourceResponse.INVALID_TOKEN)
 .buildHeaderMessage();

 HttpHeaders headers = new HttpHeaders();
 headers.add(OAuth.HeaderType.WWW_AUTHENTICATE,
 oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
  return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);

  } */
            //返回用户名
//            User user = userService.selectByPrimaryKey(1);
            String username = accessToken + "---" + Math.random() + "----" + "dafa"; //user.getUname();

            System.out.println(username);
            System.out.println("服务端/userInfo::::::ppp");
            System.out.println("-----------服务端/userInfo----------------------------------------------------------");
            return new ResponseEntity(username, HttpStatus.OK);
        } catch (OAuthProblemException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
//检查是否设置了错误码
            String errorCode = e.getError();

            if (OAuthUtils.isEmpty(errorCode)) {
                OAuthResponse oauthResponse = OAuthRSResponse
                        .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                        .buildHeaderMessage();


                HttpHeaders headers = new HttpHeaders();

                headers.add(OAuth.HeaderType.WWW_AUTHENTICATE,
                        oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));

                return new ResponseEntity(headers, HttpStatus.UNAUTHORIZED);
            }

            OAuthResponse oauthResponse = OAuthRSResponse
                    .errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                    .setError(e.getError())
                    .setErrorDescription(e.getDescription())
                    .setErrorUri(e.getUri())
                    .buildHeaderMessage();


            HttpHeaders headers = new HttpHeaders();

            headers.add(OAuth.HeaderType.WWW_AUTHENTICATE,

                    oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));

            System.out.println("-----------服务端/userInfo------------------------------------------------------------------------------");

            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        }
    }
}


