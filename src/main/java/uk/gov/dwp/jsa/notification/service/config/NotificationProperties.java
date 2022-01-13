package uk.gov.dwp.jsa.notification.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification.client")
public class NotificationProperties {

    private String apiKey;
    private String mailTemplateIdEnglish;
    private String mailTemplateIdWelsh;
    private String mailProgressTemplateIdEnglish;
    private String mailProgressTemplateIdWelsh;
    private String smsProgressTemplateIdEnglish;
    private String smsProgressTemplateIdWelsh;
    private String mailCountTemplateId;
    private String mailCountAddress;
    private String mailStatsTemplateId;
    private String mailStatsAddress;
    private String smsTemplateIdEnglish;
    private String smsTemplateIdWelsh;
    private String mailSuccessTemplateIdEnglish;
    private String mailSuccessTemplateIdWelsh;
    private String smsSuccessTemplateIdEnglish;
    private String smsSuccessTemplateIdWelsh;
    private String notificationUrl;
    private String proxyHost;
    private String proxyPort;

    public String getMailTemplateIdEnglish() {
        return mailTemplateIdEnglish;
    }

    public void setMailTemplateIdEnglish(final String mailTemplateIdEnglish) {
        this.mailTemplateIdEnglish = mailTemplateIdEnglish;
    }

    public String getMailTemplateIdWelsh() {
        return mailTemplateIdWelsh;
    }

    public void setMailTemplateIdWelsh(final String mailTemplateIdWelsh) {
        this.mailTemplateIdWelsh = mailTemplateIdWelsh;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(final String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(final String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    public String getMailCountTemplateId() {
        return mailCountTemplateId;
    }

    public void setMailCountTemplateId(final String mailCountTemplateId) {
        this.mailCountTemplateId = mailCountTemplateId;
    }

    public String getMailCountAddress() {
        return mailCountAddress;
    }

    public void setMailCountAddress(final String mailCountAddress) {
        this.mailCountAddress = mailCountAddress;
    }

    public String getSmsTemplateIdEnglish() {
        return smsTemplateIdEnglish;
    }

    public void setSmsTemplateIdEnglish(final String smsTemplateIdEnglish) {
        this.smsTemplateIdEnglish = smsTemplateIdEnglish;
    }

    public String getSmsTemplateIdWelsh() {
        return smsTemplateIdWelsh;
    }

    public void setSmsTemplateIdWelsh(final String smsTemplateIdWelsh) {
        this.smsTemplateIdWelsh = smsTemplateIdWelsh;
    }

    public String getMailStatsTemplateId() {
        return mailStatsTemplateId;
    }

    public void setMailStatsTemplateId(final String mailStatsTemplateId) {
        this.mailStatsTemplateId = mailStatsTemplateId;
    }

    public String getMailStatsAddress() {
        return mailStatsAddress;
    }

    public void setMailStatsAddress(final String mailStatsAddress) {
        this.mailStatsAddress = mailStatsAddress;
    }

    public String getMailProgressTemplateIdEnglish() {
        return mailProgressTemplateIdEnglish;
    }

    public void setMailProgressTemplateIdEnglish(final String mailProgressTemplateIdEnglish) {
        this.mailProgressTemplateIdEnglish = mailProgressTemplateIdEnglish;
    }

    public String getMailProgressTemplateIdWelsh() {
        return mailProgressTemplateIdWelsh;
    }

    public void setMailProgressTemplateIdWelsh(final String mailProgressTemplateIdWelsh) {
        this.mailProgressTemplateIdWelsh = mailProgressTemplateIdWelsh;
    }

    public String getSmsProgressTemplateIdEnglish() {
        return smsProgressTemplateIdEnglish;
    }

    public void setSmsProgressTemplateIdEnglish(final String smsProgressTemplateIdEnglish) {
        this.smsProgressTemplateIdEnglish = smsProgressTemplateIdEnglish;
    }

    public String getSmsProgressTemplateIdWelsh() {
        return smsProgressTemplateIdWelsh;
    }

    public void setSmsProgressTemplateIdWelsh(final String smsProgressTemplateIdWelsh) {
        this.smsProgressTemplateIdWelsh = smsProgressTemplateIdWelsh;
    }

    public String getSmsSuccessTemplateIdEnglish() {
        return smsSuccessTemplateIdEnglish;
    }

    public void setSmsSuccessTemplateIdEnglish(final String smsSuccessTemplateIdEnglish) {
        this.smsSuccessTemplateIdEnglish = smsSuccessTemplateIdEnglish;
    }

    public String getSmsSuccessTemplateIdWelsh() {
        return smsSuccessTemplateIdWelsh;
    }

    public void setSmsSuccessTemplateIdWelsh(final String smsSuccessTemplateIdWelsh) {
        this.smsSuccessTemplateIdWelsh = smsSuccessTemplateIdWelsh;
    }

    public String getMailSuccessTemplateIdEnglish() {
        return mailSuccessTemplateIdEnglish;
    }

    public void setMailSuccessTemplateIdEnglish(final String mailSuccessTemplateIdEnglish) {
        this.mailSuccessTemplateIdEnglish = mailSuccessTemplateIdEnglish;
    }

    public String getMailSuccessTemplateIdWelsh() {
        return mailSuccessTemplateIdWelsh;
    }

    public void setMailSuccessTemplateIdWelsh(final String mailSuccessTemplateIdWelsh) {
        this.mailSuccessTemplateIdWelsh = mailSuccessTemplateIdWelsh;
    }
}
