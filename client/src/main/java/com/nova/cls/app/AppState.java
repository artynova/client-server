package com.nova.cls.app;

import com.nova.cls.network.Session;

import java.util.HashMap;
import java.util.Map;

public class AppState {
    private final Map<String, String> queryParams = new HashMap<>();
    private final Breadcrumbs breadcrumbs = new Breadcrumbs();
    private final Session session; // one instance of the app binds to the session
    // stores only ids because only ids don't invalidate (unless entry deleted entirely),
    // everything else might update by requests of other clients
    private Long currentGroupId;
    private Long currentGoodId;

    public AppState(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public Long getCurrentGroupId() {
        return currentGroupId;
    }

    public void setCurrentGroupId(Long currentGroupId) {
        this.currentGroupId = currentGroupId;
    }

    public Long getCurrentGoodId() {
        return currentGoodId;
    }

    public void setCurrentGoodId(Long currentGoodId) {
        this.currentGoodId = currentGoodId;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Breadcrumbs getBreadcrumbs() {
        return this.breadcrumbs;
    }
}
