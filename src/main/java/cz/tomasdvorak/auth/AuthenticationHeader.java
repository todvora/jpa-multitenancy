package cz.tomasdvorak.auth;

public class AuthenticationHeader {

    private String tenantName;
    private String password;

    public AuthenticationHeader() {
    }

    public AuthenticationHeader(final String tenantName, final String password) {
        this.tenantName = tenantName;
        this.password = password;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(final String tenantName) {
        this.tenantName = tenantName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthenticationHeader{" +
                "tenantName='" + tenantName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
