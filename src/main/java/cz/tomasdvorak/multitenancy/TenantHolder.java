package cz.tomasdvorak.multitenancy;

/**
 * Thread local storage of the tenant name. This is the only place, where the tenant name is available across all calls
 * and beans.
 * @See cz.tomasdvorak.multitenancy.TenantWrapper
 */
class TenantHolder {

    private static final InheritableThreadLocal<String> currentTenantName = new InheritableThreadLocal<>();

    public static String getCurrentTenant() {
        return currentTenantName.get();
    }

    public static void setTenant(final String tenantName) {
        currentTenantName.set(tenantName);
    }

    public static void cleanupTenant() {
        currentTenantName.remove();
    }

}
