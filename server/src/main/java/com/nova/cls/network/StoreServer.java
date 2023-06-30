package com.nova.cls.network;

import com.nova.cls.exceptions.ServerFailureException;
import com.nova.cls.network.auth.Auth;
import com.nova.cls.network.auth.LoginRouter;
import com.nova.cls.network.goods.GoodsRouter;
import com.nova.cls.network.groups.GroupsRouter;
import com.nova.cls.services.AuthService;
import com.nova.cls.services.DatabaseHelper;
import com.nova.cls.services.GoodsService;
import com.nova.cls.services.GroupsService;
import com.nova.cls.services.UsersService;
import com.nova.cls.util.CloseableThreadLocal;
import com.nova.cls.util.Decryptor;
import com.nova.cls.util.Encryptor;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreServer {
    private static final int BACKLOG = 100;
    private static final int THREADS = 8;

    private final CloseableThreadLocal<Connection> connectionLocal;
    private final CloseableThreadLocal<AuthService> authServiceLocal;
    private final CloseableThreadLocal<GroupsService> groupsServiceLocal;
    private final CloseableThreadLocal<GoodsService> goodsServiceLocal;
    private final ExecutorService pool;
    private final HttpServer server;
    private boolean closed = false;
    private final Encryptor encryptor;
    private final Decryptor decryptor;

    public StoreServer() throws IOException {
        // data and services
        DatabaseHelper.initDatabase();
        connectionLocal = new CloseableThreadLocal<>(DatabaseHelper::getConnection);
        authServiceLocal = new CloseableThreadLocal<>(() -> new AuthService(new UsersService(connectionLocal.get())));
        groupsServiceLocal = new CloseableThreadLocal<>(() -> new GroupsService(connectionLocal.get()));
        goodsServiceLocal = new CloseableThreadLocal<>(() -> new GoodsService(connectionLocal.get()));

        this.encryptor = new Encryptor();
        this.decryptor = new Decryptor();

        // controllers
        pool = Executors.newFixedThreadPool(THREADS);
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(),
            Constants.SERVER_EXTERNAL_ADDRESS.getPort()), BACKLOG);
        server.createContext(LoginRouter.BASE_ROUTE, new LoginRouter(authServiceLocal, encryptor, decryptor));
        server.createContext(GroupsRouter.BASE_ROUTE, new GroupsRouter(groupsServiceLocal, encryptor, decryptor))
            .setAuthenticator(new Auth(authServiceLocal));
        server.createContext(GoodsRouter.BASE_ROUTE, new GoodsRouter(goodsServiceLocal, encryptor, decryptor))
            .setAuthenticator(new Auth(authServiceLocal));
    }

    public static void main(String[] args) throws IOException {
        StoreServer storeServer = new StoreServer();
        storeServer.start();
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
