package cz.tomasdvorak.multitenancy;

import java.lang.reflect.Proxy;

/**
 * Wrapper for any interface. It will automatically configure tenant name on each call of the interface
 * in {@link TenantHolder} and revert it after the call is done. All logic inside the wrapped  {@code implementation}
 * has configured and available tenant name in {@link TenantHolder}. The implementation should not use this information
 * directly and rely on injected EntityManager({@link ProxyEntityManager}, which will route the persistence requests to correct DB schema.
 */
public class TenantWrapper {

    @SuppressWarnings("unchecked")
    public static <T> T wrap(final String tenantName, final Class<T> serviceInterface, final T implementation) {
        return (T)Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class<?>[]{serviceInterface},
                (proxy, method, args) -> {
                    final String oldValue = TenantHolder.getCurrentTenant();
                    try {
                        TenantHolder.setTenant(tenantName);
                        return method.invoke(implementation, args);
                    } finally {
                        if (oldValue != null) {
                            TenantHolder.setTenant(oldValue);
                        } else {
                            TenantHolder.cleanupTenant();
                        }
                    }
                });
    }
}
