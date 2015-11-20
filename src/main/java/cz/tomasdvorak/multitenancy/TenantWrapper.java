package cz.tomasdvorak.multitenancy;

import java.lang.reflect.Proxy;

/**
 * Wrapper for any interface. It will automatically configure tenant name on each call of the interface
 * in {@link TenantHolder} and revert it after the call is done.
 */
public class TenantWrapper {

    public static <T> T wrap(final String tenantName, final Class<T> serviceInterface, final T service) {
        return (T)Proxy.newProxyInstance(service.getClass().getClassLoader(), new Class<?>[] { serviceInterface },
            (proxy, method, args) -> {
                final String oldValue = TenantHolder.getCurrentTenant();
                try {
                    TenantHolder.setTenant(tenantName);
                    return method.invoke(service, args);
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
