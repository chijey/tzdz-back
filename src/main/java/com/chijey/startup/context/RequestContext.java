package com.chijey.startup.context;

import com.alibaba.fastjson.TypeReference;
import com.chijey.startup.utils.FastJsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 基础服务上下文
 *
 * @Author: yangfeng
 * @Date: 2019/12/12 13:15
 * Email: Feng.Yang@things-matrix.com
 */
@Slf4j
public class RequestContext {

//    private static final String IOT_ADMIN = "iot-admin";

    private static Optional<HttpServletRequest> getHttpServletRequest() {
        return Optional.ofNullable(org.springframework.web.context.request.RequestContextHolder.getRequestAttributes())
                .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes).getRequest());
    }

    public static Optional<Map<String, Object>> getUserRedis() {
        return getHttpServletRequest()
                .map(request -> Optional.ofNullable(request.getHeader("User-Info"))
                        .map(s -> FastJsonUtils.fromJson(s, new TypeReference<Map<String, Object>>(){})))
                .orElse(Optional.empty());
    }

//    public static Optional<Map<String, Object>> getCompany() {
//        return getUserRedis().map(userRedis -> FastJsonUtils.fromJson(Objects.toString(userRedis.get("company")), new TypeReference<Map<String, Object>>(){}));
//    }
//
//    public static String getCompanyId() {
//        return getCompany().map(company -> Objects.toString(company.get("id"), StringUtils.EMPTY)).orElse(StringUtils.EMPTY);
//    }
//
//    public static String getCompanyCode() {
//        return getCompany().map(company -> Objects.toString(company.get("code"), StringUtils.EMPTY)).orElse(StringUtils.EMPTY);
//    }
//
//    public static String getUserId() {
//        return getUserRedis().map(userRedis -> Objects.toString(userRedis.get("id"), StringUtils.EMPTY)).orElse(StringUtils.EMPTY);
//    }

//    public static String getLoginName() {
//        return getUserRedis().map(userRedis -> Objects.toString(userRedis.get("userName"), IOT_ADMIN)).orElse(IOT_ADMIN);
//    }


//    public static List<String> getSharewith() {
//        return getUserRedis().map(userRedis -> FastJsonUtils.fromJsonToList(Objects.toString(userRedis.get("sharewith"), StringUtils.EMPTY), String.class)).orElse(Collections.emptyList());
//    }
}
