package com.cmsr.onebase.framework.connectivity;

import com.cmsr.onebase.framework.remote.dto.HttpRestResultDTO;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * 连通性探测 API：获取当前登录用户信息（DolphinScheduler 3.3.1）。
 *
 * 说明：通过请求头 token 鉴权；本项目的 OkHttp 拦截器会自动附加。
 * 该接口无需 projectCode 等业务参数，适合作为最小化连通性检测。
 *
 * @author mat
 */
public interface HealthApi {

    /**
     * 获取当前登录用户信息。
     *
     * @return 当前登录用户信息
     */
    @GET("users/get-user-info")
    Call<HttpRestResultDTO<Object>> getCurrentUser();
}
