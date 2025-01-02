package com.restapi.styleswap.security;

import com.restapi.styleswap.exception.ApiException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider{

    @Value("ad165b11320bc91501ab08613cc3a48a62a6caca4d5c8b14ca82cc313b3b96cd")
    private String jwtSecret;

    @Value("259200000")
    private long jwtExpirationTime;

    //generate JWT token
    public String generateToken(Authentication authentication){
        String usernameOrEmail = authentication.getName();

        Date currentDate = new Date();

        Date expiredDate = new Date(currentDate.getTime()+jwtExpirationTime);

        return Jwts.builder()
                .setSubject(usernameOrEmail)
                .setIssuedAt(new Date())
                .setExpiration(expiredDate)
                .signWith(key())
                .compact();
    }

//    get username from JwtToken
    public String getUsername(String token){

        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                //username is in body of token:
                .getBody()
                //earlier to add username, method setSubject(String s) was used - now getter
                .getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    //parse method - throws many exceptions, instead of Handle all of them individual
                    //better catch them and send ApiException - which we handled before
                    .parse(token);

            return true;
        }
        catch (MalformedJwtException ex){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid JWT Token");
        } catch (ExpiredJwtException ex){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Expired JWT Token");
        } catch (UnsupportedJwtException ex){
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported JWT Token");
        } catch (IllegalArgumentException ex){
            throw new ApiException(HttpStatus.BAD_REQUEST, "JWT claims string is empty");
        }
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

}
