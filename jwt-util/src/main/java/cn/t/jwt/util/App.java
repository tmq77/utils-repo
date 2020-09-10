package cn.t.jwt.util;

import cn.t.jwt.util.token.Token;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws InterruptedException
    {
        Token token = JwtUtil.createTokenByHS256(1200);
        System.out.println(token.getToken());
        Thread.sleep(1100);
        System.out.println(JwtUtil.verifyTokenByHS256(token.getToken()));
    }
}
