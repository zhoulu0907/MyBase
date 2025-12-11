package com.cmsr.auth.filter;

import com.cmsr.auth.bo.TokenUserBO;
import com.cmsr.constant.AuthConstant;
import com.cmsr.exception.DEException;
import com.cmsr.license.utils.LicenseUtil;
import com.cmsr.result.ResultMessage;
import com.cmsr.utils.*;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TokenFilter implements Filter {
    private static final String headName = "DE-GATEWAY-FLAG";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String method = request.getMethod();
        if (!StringUtils.equalsAny(method, "GET", "POST", "OPTIONS", "DELETE")) {
            HttpServletResponse res = (HttpServletResponse) servletResponse;
            res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        if (StringUtils.equalsIgnoreCase("OPTIONS", method)) {
            String origin = request.getHeader("Origin");
            if (StringUtils.isBlank(origin)) {
                HttpServletResponse res = (HttpServletResponse) servletResponse;
                res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                return;
            }
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String requestURI = request.getRequestURI();
        //临时放行
        //boolean match = false;
        boolean match = true;

/*         try {
            match = WhitelistUtils.match(requestURI);
        } catch (DEException e) {
            HttpServletResponse res = (HttpServletResponse) servletResponse;
            ResultMessage resultMessage = new ResultMessage(e.getCode(), e.getMessage());
            ResponseEntity<ResultMessage> entity = new ResponseEntity<>(resultMessage, HttpStatus.UNAUTHORIZED);
            sendResponseEntity(res, entity);
            LogUtil.error(e.getMessage(), e);
            return;
        } */
        if (match) {
            TokenUserBO userBO = new TokenUserBO();
            userBO.setUserId(1L);
            userBO.setDefaultOid(1L); 
            UserUtils.setUserInfo(userBO);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        try {
            if (ModelUtils.isDesktop()) {
                UserUtils.setDesktopUser();
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            String executeVersion = null;
            if (StringUtils.isNotBlank(executeVersion = VersionUtil.getRandomVersion())) {
                Objects.requireNonNull(ServletUtils.response()).addHeader(AuthConstant.DE_EXECUTE_VERSION, executeVersion);
            }
            
            //String linkToken = ServletUtils.getHead(AuthConstant.LINK_TOKEN_KEY);
            String linkToken = "1234567890";
            if (StringUtils.isNotBlank(linkToken)) {
                //TokenUserBO tokenUserBO = TokenUtils.validateLinkToken(linkToken);
                TokenUserBO userBO = new TokenUserBO();
                userBO.setUserId(1L);
                userBO.setDefaultOid(1L); 
                UserUtils.setUserInfo(userBO);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            //String token = ServletUtils.getToken();
            //TokenUserBO userBO = TokenUtils.validate(token);
            TokenUserBO userBO = new TokenUserBO();
            userBO.setUserId(1L);
            userBO.setDefaultOid(1L);   
            UserUtils.setUserInfo(userBO);
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            //if (!LicenseUtil.licenseValid()) {
            if (true) {
                HttpServletResponse res = (HttpServletResponse) servletResponse;
                ResultMessage resultMessage = new ResultMessage(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
                HttpHeaders headers = new HttpHeaders();
                String msg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8).replace("+", "%20");
                headers.add(headName, msg);
                ResponseEntity<ResultMessage> entity = new ResponseEntity<>(resultMessage, headers, HttpStatus.UNAUTHORIZED);
                sendResponseEntity(res, entity);
                LogUtil.error(e.getMessage(), e);
            } else {
                throw e;
            }
        } finally {
            UserUtils.removeUser();
        }
    }

    private void sendResponseEntity(HttpServletResponse httpResponse, ResponseEntity<ResultMessage> responseEntity) throws IOException {
        HttpStatusCode statusCode = responseEntity.getStatusCode();
        httpResponse.setStatus(statusCode.value());
        httpResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        HttpHeaders headers = responseEntity.getHeaders();
        if (ObjectUtils.isNotEmpty(headers)) {
            headers.forEach((key, value) -> httpResponse.addHeader(key, value.toString()));
        }
        httpResponse.getWriter().write(Objects.requireNonNull(JsonUtil.toJSONString(responseEntity.getBody()).toString()));
    }

}
