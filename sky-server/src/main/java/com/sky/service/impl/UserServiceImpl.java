package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.schemas.office.office.STInsetMode;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String WX_LOGIN ="https://api.weixin.qq.com/sns/jscode2session";//临时凭证code的Get请求 URL
    @Autowired
    private WeChatProperties weChatProperties;
    
    @Autowired
    private UserMapper userMapper;
    private String getOpenid(String code){
        //工具类调用方法:URL,请求参数map,
        Map<String, String> para=new HashMap<>();
        para.put("appid", weChatProperties.getAppid());//几个参数分别获取并传入,利用Wechat配置类获取参数,配置类自动注入
        para.put("secret",weChatProperties.getSecret());
        para.put("js_code",code);
        para.put("grant_type","authorization_code");//开发文档中规定了只需这样填写
        String json = HttpClientUtil.doGet(WX_LOGIN, para);//请求的返回结果为json对象,我们需要进一步分离出openid

        JSONObject jsonObject= JSON.parseObject(json);//转换为对象
        String openid=jsonObject.getString("openid");//由对象获取
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);//使用自定义的异常类和自定义的消息常量
        }
        return openid;
    }
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //一:调用微信接口服务，获得当前微信用户的openid ，根据开放文档确定,需要用httpclient向上面的地址发送请求，然后获取openid封装的user对象
        String openid = getOpenid(userLoginDTO.getCode());

        //二:判断是否是已有用户,也就是要查询当前服务器的数据库中user表有没有该用户,所以要创建 User mapper
        User user = userMapper.geByOpenid(openid);//mapper对象自动注入
        //三:自动完成新用户的注册,也就是构造然后插入
        if(user==null ){
            user=User.builder()
                    .avatar(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        return user;
    }
}
