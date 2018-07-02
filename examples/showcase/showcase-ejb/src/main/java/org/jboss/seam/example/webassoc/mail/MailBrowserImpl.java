package org.jboss.seam.example.webassoc.mail;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import javax.ejb.Stateless;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Stateless
@Name("mailBrowser")
public class MailBrowserImpl
        implements MailBrowser
{

   @Logger
   private Log log;
   @In
   private FacesMessages facesMessages;
   @Resource(name = "Mail", mappedName = "java:/Mail")
   private Session mailSession = null;

   public CachedMsg[] getMessages(String user)
   {

      try
      {

         String protocol = "imap";
         String mbox = "INBOX";
         String passwd = ""; // req.getParameter("password");
         URLName url =
            new URLName(protocol, Cts.IPs.UGLYSERVERNAME, -1, mbox, user,
               passwd);
         Store store = mailSession.getStore(url);
         store.connect();

         Folder folder = store.getDefaultFolder();

         if (folder == null)
         {

            throw new MessagingException("No default folder");

         }

         folder = folder.getFolder(mbox);

         if (folder == null)
         {

            throw new MessagingException("Invalid folder");

         }

         folder.open(Folder.READ_WRITE);

         int totalMessages = folder.getMessageCount();
         Message[] msgs = folder.getMessages();
         FetchProfile fp = new FetchProfile();
         fp.add(FetchProfile.Item.ENVELOPE);
         folder.fetch(msgs, fp);
         folder.close(false);
         store.close();

         // for each message, show its headers
         List<CachedMsg> cached = new ArrayList<CachedMsg>();

         // Method 2: for (int i = 1; i <= totalMessages; i++)
         for (int i = 0; i <= totalMessages; i++)
         {

            // Method 2: Message m = f.getMessage(i);
            Message m = msgs[i];

            // if message has the DELETED flag set, don't display it
            if (m.isSet(Flags.Flag.DELETED))
            {

               continue;

            }

            CachedMsg newMsg = new CachedMsg(m);
            cached.add(newMsg);

         }

         return (CachedMsg[])(cached.toArray(new CachedMsg[0]));

      }
      catch (Exception e)
      {

         facesMessages.add("Fetching e-mails failed for [" + user + "]");
         log.error("Error fetching e-mails for [" + user + "]", e);

         return new CachedMsg[0];

      }

   }

   public void deleteMail(Long newObjectId)
   {

      // TODO Auto-generated method stub
   }

   public void replayMail(Long newObjectId)
   {

      // TODO Auto-generated method stub
   }

   public void forwardMail(Long newObjectId)
   {

      // TODO Auto-generated method stub
   }

   public void sendMail(String to, String cc, String msgSubject,
      String msgBody)
   {

      try
      {

         MimeMessage msg = new MimeMessage(mailSession);
         msg.setFrom();

         InternetAddress[] destAddrs = InternetAddress.parse(to, false);
         msg.setRecipients(Message.RecipientType.TO, destAddrs);

         if (cc != null)
         {

            InternetAddress[] ccAddrs = InternetAddress.parse(cc, false);
            msg.setRecipients(Message.RecipientType.CC, ccAddrs);

         }

         if (msgSubject != null)
         {

            msg.setSubject(msgSubject);

         }

         msg.setSentDate(new Date());
         msg.setHeader("X-Mailer", "JavaMailer");
         msg.setContent(msgBody, "text/plain");

         // Transport.send(msg);
         Transport trans = mailSession.getTransport("smtp");
         trans.connect(Cts.IPs.UGLYSERVERNAME, "jDore", "pswrd");
         trans.sendMessage(msg, msg.getAllRecipients());
         trans.close();

      }
      catch (Exception e)
      {

         facesMessages.add("Sending e-mail failed for [" + to + "]");
         log.error("Error sending " + msgSubject + " e-mail for [" + to + "]",
            e);
         ;

      }

   }

}