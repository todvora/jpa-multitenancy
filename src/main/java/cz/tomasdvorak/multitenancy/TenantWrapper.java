package cz.tomasdvorak.multitenancy;

import java.lang.reflect.Proxy;

public class TenantWrapper {

    public static <T> T wrap(final String tenantName, Class<T> serviceInterface, T service) {
        return (T)Proxy.newProxyInstance(service.getClass().getClassLoader(), new Class<?>[]{serviceInterface}, (proxy, method, args) -> {
                try {
                    TenantHolder.setTenant(tenantName);
                    return method.invoke(service, args);
                } finally {
                    TenantHolder.cleanupTenant();
                }
            }
        );
    }
}
