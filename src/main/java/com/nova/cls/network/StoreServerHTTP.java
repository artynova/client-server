package com.nova.cls.network;

import com.nova.cls.exceptions.ServerFailureException;
import com.nova.cls.data.services.AuthService;
import com.nova.cls.data.services.DatabaseHelper;
import com.nova.cls.data.services.GoodsService;
import com.nova.cls.data.services.UsersService;
import com.nova.cls.network.auth.Auth;
import com.nova.cls.network.auth.LoginRouter;
import com.nova.cls.network.constants.Shared;
import com.nova.cls.network.goods.GoodRouter;
import com.nova.cls.util.CloseableThreadLocal;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreServerHTTP {
    private static final int BACKLOG = 100;
    private static final int THREADS = 8;

    private final CloseableThreadLocal<Connection> connectionLocal;
    private final CloseableThreadLocal<AuthService> authServiceLocal;
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal;
    private final ExecutorService pool;
    private final HttpServer server;
    private boolean closed = false;

    public StoreServerHTTP() throws IOException {
        // data and services
        DatabaseHelper.initDatabase();
        connectionLocal = new CloseableThreadLocal<>(DatabaseHelper::getConnection);
        authServiceLocal = new CloseableThreadLocal<>(() -> new AuthService(new UsersService(connectionLocal.get())));
        goodsServiceLocal = new CloseableThreadLocal<>(() -> new GoodsService(connectionLocal.get()));

        // controllers
        pool = Executors.newFixedThreadPool(THREADS);
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), Shared.PORT), BACKLOG);
        server.createContext(LoginRouter.BASE_ROUTE, new LoginRouter(authServiceLocal));
        server.createContext(GoodRouter.BASE_ROUTE, new GoodRouter(goodsServiceLocal))
            .setAuthenticator(new Auth(authServiceLocal));
    }

    public static void main(String[] args) throws IOException {
        StoreServerHTTP serverHTTP = new StoreServerHTTP();
        serverHTTP.start();
    }

    public void start() throws ServerFailureException {
        server.setExecutor(pool);
        server.start();
    }

    public void close() throws Exception {
        if (isClosed()) {
            return;
        }
        server.stop(0);
        connectionLocal.close();
        authServiceLocal.close();
        goodsServiceLocal.close();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
