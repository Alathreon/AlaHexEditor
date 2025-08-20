package com.alathreon.alahexeditor.persistence;

public record Persistence(RecentlyOpenedModule recentlyOpened) {
    public static Persistence create() {
        PersistenceAPI persistenceAPI = new PersistenceAPI();
        return new Persistence(new RecentlyOpenedModule(persistenceAPI));
    }
}
