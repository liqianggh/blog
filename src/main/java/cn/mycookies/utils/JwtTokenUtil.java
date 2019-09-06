package cn.mycookies.utils;

import cn.mycookies.security.SecurityUserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.*;

/**
 * token工具类
 *
 * @author liqiang
 * @datetime 2019-09-06 15:19:05
 */
@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = 1601851093876003653L;

    private static final String CLAIM_KEY_CREATEDATE = "created";
    private static final String CLAIM_KEY_USERNAME = "hello";
    private static final String CLAIM_KEY_ID = "sub1";
    private static final String CLAIM_KEY_ROLE = "world";
    private static final String CLAIM_KEY_PASSWORD = "sub2";
    private static final String CLAIM_KEY_RANDOM = "r";
    private static final String CLAIM_KEY_IV = "sub3";
    private static final String CLAIM_KEY_EMAIL = "email";


    @Value("${jwt.secret}")
    private String secret;

    @Value(value = "${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.cbcSecret}")
    private String aesKey;

    private SecretKey key;

    @Resource
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() throws DecoderException {
        byte[] keyBytes = Hex.decodeHex(secret.toCharArray());
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Optional<String> getUsernameFromToken(String authToken) {
        Optional<Claims> claimsOptional = getClaimsFromToken(authToken);
        if (claimsOptional.isPresent()) {
            final Claims claims = claimsOptional.get();
            return Optional.of(claims.getSubject());
        } else {
            return Optional.empty();
        }
    }

    public Optional<Date> getExpirationDateFromToken(String token) {
        Optional<Claims> claimsOptional = getClaimsFromToken(token);
        if (claimsOptional.isPresent()) {
            final Claims claims = claimsOptional.get();
            return Optional.of(claims.getExpiration());
        } else {
            return Optional.empty();
        }

    }

    /**
     * 从token中获得自定义内容
     *
     * @param token
     * @param key
     * @return
     */
    public Optional<String> getCustomFromToken(String token, String key) {
        Optional<Claims> claimsOptional = getClaimsFromToken(token);
        if (claimsOptional.isPresent()) {
            final Claims claims = claimsOptional.get();
            return Optional.of(String.valueOf(claims.get(key)));
        } else {
            return Optional.empty();
        }
    }

    private Optional<Claims> getClaimsFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(key)
                    .parseClaimsJws(token).getBody();
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    public boolean validateToken(String authToken, UserDetails userDetails) {
        SecurityUserDetail user = (SecurityUserDetail) userDetails;
        Optional<String> usernameOptional = getUsernameFromToken(authToken);
        if (!usernameOptional.isPresent()) {
            return false;
        }
        String username = usernameOptional.get();
        boolean validateUsername = username.equals(user.getUsername());
        boolean validateExpired = !isTokenExpired(authToken);
        boolean validatePassword = true;
        return  validateUsername && validateExpired && validatePassword;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, ((SecurityUserDetail) userDetails).getUserName());
        claims.put(CLAIM_KEY_CREATEDATE, new Date());
        claims.put(CLAIM_KEY_ID, ((SecurityUserDetail) userDetails).getId());
        claims.put(CLAIM_KEY_ROLE, ((SecurityUserDetail) userDetails).getRole());
        claims.put(CLAIM_KEY_RANDOM, "1");
//        claims.put(CLAIM_KEY_PASSWORD, passwordEncoder.encode(userDetails.getPassword()));
        String iv = AESCbcUtils.genHexIv();
        claims.put(CLAIM_KEY_IV, iv);
        return generatreToken(claims);
    }

    public UserDetails parseTokenToUserDetails(String token){
        Optional<String> uid = getCustomFromToken(token, CLAIM_KEY_ID);
        Optional<String> role = getCustomFromToken(token, CLAIM_KEY_ROLE);
        Optional<String> userName = getCustomFromToken(token, CLAIM_KEY_USERNAME);
        Optional<String> email = getCustomFromToken(token, CLAIM_KEY_EMAIL);

        SecurityUserDetail securityUserDetail = new SecurityUserDetail();
        if (uid.isPresent()) {
            String uidStr = uid.get();
            if (StringUtils.isNotEmpty(uidStr) && !Objects.equals(uidStr,"null")){
                securityUserDetail.setId(Long.valueOf(uid.get()));
            }
        }

        if (role.isPresent()) {
            securityUserDetail.setRole(role.get());
        }
        if (userName.isPresent()) {
            securityUserDetail.setUserName(userName.get());
        }
        return securityUserDetail;
    }

    String generatreToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(key)
                .compact();
    }

    private Boolean isTokenExpired(String token) {
        Optional<Date> expirationOptional = getExpirationDateFromToken(token);
        if (expirationOptional.isPresent()) {
            Date expiration = expirationOptional.get();
            return expiration.before(new Date());
        } else {
            return true;
        }

    }

    private boolean isPasswordValid(String token, UserDetails userDetails) {
        Optional<String> passwordOpt = getCustomFromToken(token, CLAIM_KEY_PASSWORD);
        Optional<String> ivOpt = getCustomFromToken(token, CLAIM_KEY_IV);
        String password = "";
        String iv = "";
        if (passwordOpt.isPresent()) {
            password = passwordOpt.get();
        }
        if (ivOpt.isPresent()) {
            iv = ivOpt.get();
        }
        return passwordEncoder.matches(password, userDetails.getPassword());
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }


    public Optional<String> getRolesFromToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return Optional.empty();
        }
        return getCustomFromToken(token, CLAIM_KEY_ROLE);
    }
}
