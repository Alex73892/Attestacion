package com.example.myapplication;

public class PasswordEntry {
    private long id;
    private String serviceName;
    private String login;
    private String encryptedPassword;
    private String notes;

    public PasswordEntry(long id, String serviceName, String login, String encryptedPassword, String notes) {
        this.id = id;
        this.serviceName = serviceName;
        this.login = login;
        this.encryptedPassword = encryptedPassword;
        this.notes = notes;
    }

    public long getId() { return id; }
    public String getServiceName() { return serviceName; }
    public String getLogin() { return login; }
    public String getEncryptedPassword() { return encryptedPassword; }
    public String getNotes() { return notes; }
}
