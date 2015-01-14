/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import com.sun.mail.smtp.SMTPTransport;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author pzielins
 */
public class Email {

    public static boolean sendInfoAboutPassReset(String login, String pwd, String email) throws UnknownHostException, IOException {
        try {
            //Set the host smtp address
            Properties props = new Properties();
            props.put("mail.smtp.host", Configuration.getConfigParameter("SMTPHost"));

            // create some properties and get the default Session
            Session session = Session.getDefaultInstance(props, null);

            // create a message
            Message msg = new MimeMessage(session);

            // set the from and to address
            InternetAddress addressFrom = new InternetAddress(Configuration.getConfigParameter("senderAddr"));
            msg.setFrom(addressFrom);
            InternetAddress mail = new InternetAddress(email.toLowerCase());
            msg.setRecipient(Message.RecipientType.TO, mail);
            // Setting the Subject and Content Type
            msg.setSubject("Twoje hasło w Systemie Obsługi Formularzy Penvision zostało zresetowane");
            String txt = "Witaj!"
                    + "\nW systemie Sevenet System Obsługi Formularzy Penvision twoje hasło zostało zresetowane. Poniżej podane jest twoje nowe hasło."
                    + "\n"
                    + "\nLogin: " + login
                    + "\nAdres email: " + email
                    + "\nHasło: " + pwd;

            txt += "\n\nAby zalogować się do aplikacji, kliknij poniższy link: \n"
                    + getAppUrl();
            //    msg.setContent(txt, "text/plain; charset=UTF-8");

//            MimeBodyPart messagePart = new MimeBodyPart();
//            messagePart.setText(txt);
//
//            //
//            // Set the email attachment file
//            //
//
//
//            Multipart multipart = new MimeMultipart();
//            multipart.addBodyPart(messagePart);
            msg.setContent(txt, "text/plain; charset=UTF-8");

            SMTPTransport transportSMTP = new SMTPTransport(session, null);
            Socket soc = new Socket(Configuration.getConfigParameter("SMTPHost"), Integer.parseInt(Configuration.getConfigParameter("SMTPPort")));
            transportSMTP.connect(soc);
            transportSMTP.sendMessage(msg, msg.getAllRecipients());
            transportSMTP.close();
            soc.close();
            //Transport.send(msg); //msg
        } catch (MessagingException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;

    }

    public static String getAppUrl() {
        String url = "";
//        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String appCtx = "/ObslugaFormularzyPenvision";
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
            String ip = inetAddress.getHostAddress();
            //  String ip = "81.219.199.162";

            url += "http://" + ip + ":8080" + appCtx + "/system/index.xhtml";

        } catch (UnknownHostException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        }
        return url;
    }
}
