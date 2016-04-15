package be.ttime.core.config;

public class EntitiesBasePackages {

    private final String[] basePackages;

    public EntitiesBasePackages(final String... basePackages) {
        this.basePackages = basePackages;
    }

    public String[] getBasePackages() {
        return basePackages;
    }

}
