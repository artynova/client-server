package com.nova.cls.app;

import java.util.ArrayList;

public class Breadcrumbs {
    private final ArrayList<Runnable> breadcrumbs = new ArrayList<>();

    /**
     * Executes the breadcrumb action and appends it to the list.
     *
     * @param breadcrumb Action to be executed.
     */
    public void visit(Runnable breadcrumb) {
        breadcrumb.run();
        breadcrumbs.add(breadcrumb);
    }

    /**
     * Removes the last breadcrumb and executes the second-to-last.
     * On the example of navigation, this will open the previous visited window.
     */
    public void back() {
        if (breadcrumbs.size() <= 1) throw new UnsupportedOperationException("Navigating back when already at root");
        breadcrumbs.remove(breadcrumbs.size() - 1);
        breadcrumbs.get(breadcrumbs.size() - 1).run();
    }

    /**
     * Repeats the latest task (e.g. if tasks are navigations, refreshes current one).
     */
    public void refresh() {
        if (breadcrumbs.isEmpty()) throw new UnsupportedOperationException("Refreshing with empty breadcrumbs");
        breadcrumbs.get(breadcrumbs.size() - 1).run();
    }

    public void reset() {
        breadcrumbs.clear();
    }

    public boolean atRoot() {
        return breadcrumbs.size() == 1;
    }
}
