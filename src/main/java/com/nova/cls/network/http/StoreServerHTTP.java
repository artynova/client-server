package com.nova.cls.network.http;

import com.nova.cls.data.services.AuthService;
import com.nova.cls.data.services.DatabaseHandler;
import com.nova.cls.data.services.GoodsService;
import com.nova.cls.data.services.UsersService;
import com.nova.cls.network.Server;
import com.nova.cls.network.ServerFailureException;
import com.nova.cls.network.http.auth.Auth;
import com.nova.cls.network.http.auth.LoginRouter;
import com.nova.cls.network.http.goods.GoodRouter;
import com.nova.cls.util.CloseableThreadLocal;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreServerHTTP implements Server {
    private static final int BACKLOG = 100;
    private static final int THREADS = 8;

    static {
        DatabaseHandler.initDatabase();
    }

    private final ExecutorService pool;
    private final HttpServer server;
    private final CloseableThreadLocal<Connection> connectionLocal =
        new CloseableThreadLocal<>(DatabaseHandler::getConnection);
    private final CloseableThreadLocal<AuthService> authServiceLocal =
        new CloseableThreadLocal<>(() -> new AuthService(new UsersService(connectionLocal.get())));
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal =
        new CloseableThreadLocal<>(() -> new GoodsService(connectionLocal.get()));
    private boolean closed = false;

    public StoreServerHTTP() throws IOException {
        pool = Executors.newFixedThreadPool(THREADS);
        server = HttpServer.create(new InetSocketAddress(Constants.ADDRESS, Constants.PORT), BACKLOG);
        server.createContext(LoginRouter.BASE_ROUTE, new LoginRouter(authServiceLocal));
        server.createContext(GoodRouter.BASE_ROUTE, new GoodRouter(goodsServiceLocal))
            .setAuthenticator(new Auth(authServiceLocal));
    }

    public static void main(String[] args) throws IOException {
        StoreServerHTTP serverHTTP = new StoreServerHTTP();
        serverHTTP.start();
    }

    @Override
    public void start() throws ServerFailureException {
        server.setExecutor(pool);
        server.start();
    }

    @Override
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
