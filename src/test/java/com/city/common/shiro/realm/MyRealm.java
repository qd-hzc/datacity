package com.city.common.shiro.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;

/**
 * Created by wxl on 2016/1/11 0011.
 */
public class MyRealm implements Realm {
    @Override
    public String getName() {
        return "myRealm";
    }

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        //支持 用户名-密码方式
        return authenticationToken instanceof UsernamePasswordToken;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String name= (String) token.getPrincipal();//用户名
        String pwd = new String((char[])token.getCredentials());//密码

        if(!name.equals("zhang")){
            throw new UnknownAccountException();
        }
        if(!pwd.equals("123")){
            throw new IncorrectCredentialsException();
        }

        return new SimpleAuthenticationInfo(name,pwd,getName());
    }
}
