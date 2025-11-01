package com.alathreon.alahexeditor.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;

public record Persistence(ObjectMapper mapper, RecentlyOpenedModule recentlyOpened, RecentlyOpenedModule recentlyOpenedTemplates, AutoTemplateModule autoTemplate) {
    public static Persistence create() {
        PersistenceAPI persistenceAPI = new PersistenceAPI();
        ObjectMapper objectMapper = new ObjectMapper();
        return new Persistence(objectMapper,
                new RecentlyOpenedModule(persistenceAPI, null),
                new RecentlyOpenedModule(persistenceAPI, "template"),
                new AutoTemplateModule(persistenceAPI, objectMapper));
    }
}
