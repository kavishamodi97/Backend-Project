package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name="customer_auth")
@NamedQueries({@NamedQuery(name = "customerAuthByToken", query = "SELECT a FROM CustomerAuthEntity a WHERE a.accessToken =:accessToken")})
public class CustomerAuthEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "login_at")
    @NotNull
    private ZonedDateTime loginAt;

    @Column(name = "logout_at")
    private ZonedDateTime logoutAt;

    @Column(name = "expires_at")
    @NotNull
    private ZonedDateTime expiresAt;

   public CustomerAuthEntity(){

   }

    public CustomerAuthEntity(String uuid, CustomerEntity customer, String accessToken, ZonedDateTime loginAt, ZonedDateTime logoutAt, ZonedDateTime expiresAt) {
        this.uuid = uuid;
        this.customer = customer;
        this.accessToken = accessToken;
        this.loginAt = loginAt;
        this.logoutAt = logoutAt;
        this.expiresAt = expiresAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(ZonedDateTime loginAt) {
        this.loginAt = loginAt;
    }

    public ZonedDateTime getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(ZonedDateTime logoutAt) {
        this.logoutAt = logoutAt;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "CustomerAuthEntity{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", customer=" + customer +
                ", accessToken='" + accessToken + '\'' +
                ", loginAt=" + loginAt +
                ", logoutAt=" + logoutAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
