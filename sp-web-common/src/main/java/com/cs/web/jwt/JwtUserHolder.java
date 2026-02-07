package com.cs.web.jwt;

/**
 * @author sb
 * @date 2023/5/30 20:08
 */
public class JwtUserHolder {
    private final static ThreadLocal<JwtUser> HOLDER = new ThreadLocal<>();

    private JwtUserHolder() {
    }

    public static void set(JwtUser user) {
        HOLDER.set(user);
    }

    public static JwtUser get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
