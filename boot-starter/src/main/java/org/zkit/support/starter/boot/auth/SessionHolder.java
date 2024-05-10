package org.zkit.support.starter.boot.auth;

import io.netty.util.concurrent.FastThreadLocal;
import org.zkit.support.starter.boot.entity.SessionUser;

public class SessionHolder {

    private static final FastThreadLocal<SessionUser> holder = new FastThreadLocal<>();

    public static void set(SessionUser user) {
        holder.set(user);
    }

    public static SessionUser get() {
        return holder.get();
    }

    public static void remove() {
        holder.remove();
    }
}
