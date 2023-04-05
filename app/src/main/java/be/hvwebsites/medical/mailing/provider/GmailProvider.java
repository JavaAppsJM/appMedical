package be.hvwebsites.medical.mailing.provider;

import java.util.Properties;

public class GmailProvider implements MailProvider{
    private Properties props;

    public GmailProvider() {
        props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");
    }

    @Override
    public Properties getProperties() {
        return props;
    }
}
