package cz.tomasdvorak.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class AuthenticationHeader {

    private String tenantName;
    private String password;

    private AuthenticationHeader() {
    }

    public AuthenticationHeader(final String tenantName, final String password) {
        this.tenantName = tenantName;
        this.password = password;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getPassword() {
        return password;
    }

}
