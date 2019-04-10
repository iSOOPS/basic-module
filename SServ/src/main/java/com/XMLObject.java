package com;

import java.io.Serializable;

public class XMLObject implements Serializable {
    private String AppId;
    private String Encrypt;

    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public String getEncrypt() {
        return Encrypt;
    }

    public void setEncrypt(String encrypt) {
        Encrypt = encrypt;
    }
}
