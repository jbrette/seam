package org.jboss.seam.example.webassoc.mail;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;

// @Entity
// @Name("cachedmsg")
public class CachedMsg
        implements Serializable
{

   @Logger
   private Log log;
   @In
   private FacesMessages facesMessages;

   /**
    *
    */
   private static final long serialVersionUID = -4281086271117516903L;
   private Integer m_msgId;
   private String m_subject;
   private Date m_sentDate;
   private Date m_receivedDate;
   private List<String> m_from;
   private List<String> m_to;
   private List<String> m_cc;
   private String m_body;

   // @Id @GeneratedValue
   public Integer getMsgId()
   {

      return m_msgId;

   }

   public void setMsgId(Integer msgId)
   {

      this.m_msgId = msgId;

   }

   public void setSubject(String subject)
   {

      this.m_subject = subject;

   }

   public String getSubject()
   {

      return m_subject;

   }

   public void setFrom(List<String> from)
   {

      this.m_from = from;

   }

   public List<String> getFrom()
   {

      return m_from;

   }

   public void setBody(String body)
   {

      this.m_body = body;

   }

   public String getBody()
   {

      return m_body;

   }

   public void setTo(List<String> to)
   {

      this.m_to = to;

   }

   public List<String> getTo()
   {

      return m_to;

   }

   public void setCc(List<String> cc)
   {

      this.m_cc = cc;

   }

   public List<String> getCc()
   {

      return m_cc;

   }

   public void setSentDate(Date sentDate)
   {

      m_sentDate = sentDate;

   }

   public Date getSentDate()
   {

      return m_sentDate;

   }

   public void setReceivedDate(Date receivedDate)
   {

      m_receivedDate = receivedDate;

   }

   public Date getReceivedDate()
   {

      return m_receivedDate;

   }

   /**
    *
    * @param msg
    * @throws IOException
    */
   public CachedMsg(Message msg)
           throws IOException
   {

      try
      {

         parseMessageHeaders(msg);

         Object o = msg.getContent();

         if (msg.isMimeType("text/plain"))
         {

            setBody("<pre>" + (String)o + "</pre>");

         }
         else if (msg.isMimeType("text/html"))
         {

            setBody((String)o);

         }
         else if (msg.isMimeType("multipart/*"))
         {

            Multipart mp = (Multipart)o;
            int cnt = mp.getCount();

            for (int i = 0; i < cnt; i++)
            {

               parsePart(mp.getBodyPart(i));

            }

         }
         else
         {

            setBody("");

         }

      }
      catch (MessagingException e)
      {

         facesMessages.add("Parsing e-mail failed for [" + "" + "]");
         log.error("Error parsing e-mail for [" + "" + "]", e);
         setBody("");

      }

   }

   /**
    *
    * @param part
    * @throws IOException
    */
   private void parsePart(Part part)
   {

      try
      {

         String sct = part.getContentType();

         if (sct == null)
         {

            return;

         }

         ContentType ct = new ContentType(sct);

         // out.println("<b>Attachment Type:</b> " + ct.getBaseType() + "<br>");
         if (ct.match("text/plain"))
         {

            setBody("<pre>" + (String)part.getContent() + "</pre>");

         }
         else
         {

            // generate a url for this part
            //            String s;
            //
            //            if ((s = part.getFileName()) != null)
            //            {
            //
            //               // out.println("<b>Filename:</b> " + s + "<br>");
            //            }
            //
            //            s = null;
            //
            //            if ((s = part.getDescription()) != null)
            //            {
            //
            //               // out.println("<b>Description:</b> " + s + "<br>");
            //            }
            //         res.setContentType(ct.getBaseType());
            //
            //         InputStream is = part.getInputStream();
            //         int i;
            //
            //         while ((i = is.read()) != -1)
            //         {
            //
            //            out.write(i);
            //
            //         }
         }

      }
      catch (IOException e)
      {

         facesMessages.add("Parsing e-mail failed for [" + "" + "]");
         log.error("Error parsing e-mail for [" + "" + "]", e);

      }
      catch (MessagingException e)
      {

         facesMessages.add("Parsing e-mail failed for [" + "" + "]");
         log.error("Error parsing e-mail for [" + "" + "]", e);

      }

   }

   /**
    *
    * @param msg
    * @throws IOException
    */
   private void parseMessageHeaders(Message msg)
   {

      try
      {

         m_sentDate = msg.getSentDate();
         m_receivedDate = msg.getReceivedDate();

         Address[] fr = msg.getFrom();

         if (fr != null)
         {

            m_from = new ArrayList<String>();

            for (int i = 0; i < fr.length; i++)
            {

               m_from.add(parseAddress(fr[i]));

            }

         }

         Address[] to = msg.getRecipients(Message.RecipientType.TO);

         if (to != null)
         {

            m_to = new ArrayList<String>();

            for (int i = 0; i < to.length; i++)
            {

               m_to.add(parseAddress(to[i]));

            }

         }

         Address[] cc = msg.getRecipients(Message.RecipientType.CC);

         if (cc != null)
         {

            m_cc = new ArrayList<String>();

            for (int i = 0; i < cc.length; i++)
            {

               m_to.add(parseAddress(cc[i]));

            }

         }

         String subject = msg.getSubject();
         setSubject((subject != null) ? subject : "");

      }
      catch (MessagingException e)
      {

         facesMessages.add("Parsing e-mail failed for [" + "" + "]");
         log.error("Error parsing e-mail for [" + "" + "]", e);

      }

   }

   /**
    *
    * @param a
    * @return
    */
   private String parseAddress(Address a)
   {

      String pers = null;
      String addr = null;

      if ((a instanceof InternetAddress)
         && ((pers = ((InternetAddress)a).getPersonal()) != null))
      {

         addr = pers + "  " + "<" + ((InternetAddress)a).getAddress() + ">";

      }
      else
      {

         addr = a.toString();

      }

      return addr;

   }

}