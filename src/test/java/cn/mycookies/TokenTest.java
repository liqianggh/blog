package cn.mycookies;

import cn.mycookies.security.SecurityUserDetail;
import cn.mycookies.utils.JwtTokenUtil;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

/**
 * @author liqiang
 * @datetime 2019/9/6 15:47
 **/
public class TokenTest {

    @Test
    public void test() throws DecoderException {
        SecurityUserDetail securityUserDetail = new SecurityUserDetail();
        securityUserDetail.setId(1L);
        securityUserDetail.setUserName("李强");
        securityUserDetail.setRole("ROLE_ADMIN");

        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        String token = jwtTokenUtil.generateToken(securityUserDetail);

        System.out.println(token);
        System.out.println(jwtTokenUtil.parseTokenToUserDetails(token));
    }
}
