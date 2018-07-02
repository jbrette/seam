package org.jboss.seam.example.webassoc.mail;

import java.io.Serializable;

public class MailProcessorCmd
        implements Serializable
{

   private static final long serialVersionUID = 6640826656317341061L;
   private final CMDOrder m_order;
   private final Long m_mailingControllerId;

   public enum CMDOrder
   {

      setNewsLetterToSending(0), setNewsLetterToSent(1), setVoteToSending(2),
      setVoteToSent(3), setClubEventToAnnouncing(4),
      setClubEventToAnnounced(5), setClubEventToReminding(6),
      setClubEventToReminded(7), setClubEventToRemindingAgain(8),
      setClubEventToRemindedAgain(9), setClubEventToRemindingFinal(10),
      setClubEventToRemindedFinal(11);

      private int id;

      private CMDOrder(int id)
      {

         this.id = id;

      }

      public int getId()
      {

         return id;

      }

   }

   public MailProcessorCmd(CMDOrder order, Long mailingControllerId)
   {

      m_order = order;
      m_mailingControllerId = mailingControllerId;

   }

   public CMDOrder getOrder()
   {

      return m_order;

   }

   public Long getMailingControllerId()
   {

      return m_mailingControllerId;

   }

}