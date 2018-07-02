package org.jboss.seam.example.webassoc.clubs;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import org.richfaces.model.CalendarDataModel;
import org.richfaces.model.CalendarDataModelItem;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Transient;

@Name("clubEventCalendar")
@Scope(ScopeType.SESSION)
@AutoCreate
public class ClubEventCalendar
        implements CalendarDataModel,
           Serializable
{

   private static final long serialVersionUID = 5518553163891684926L;
   @Logger
   private Log log;
   @In
   private EntityManager entityManager;

   public class ClubEventCalendarModelItem
           implements CalendarDataModelItem
   {

      private final Date m_eventStartDate;
      private final String m_tooltip;
      private Map<String, Object> m_data = new HashMap<String, Object>();

      public ClubEventCalendarModelItem(Date hello, List<ClubEvent> events)
      {

         m_eventStartDate = hello;
         m_tooltip = "thetooltip";
         m_data = new HashMap<String, Object>();

         int eventNb = 0;

         for (ClubEvent event : events)
         {

            eventNb++;
            m_data.put("hasEvent_" + eventNb, Boolean.TRUE);
            m_data.put("title_" + eventNb, event.getTitle());
            m_data.put("club_" + eventNb,
               String.valueOf(event.getClub().getId()));
            m_data.put("event_" + eventNb, String.valueOf(event.getId()));
            m_data.put("link_" + eventNb, "/editClubEvent.seam");
            m_data.put("fparamworkaround_" + eventNb,
               "pagesClubId=" + event.getClub().getId() + "&pagesClubEventId="
               + event.getId());

         }

         while (eventNb < 3)
         {

            eventNb++;
            m_data.put("hasEvent_" + eventNb, Boolean.FALSE);
            m_data.put("link_" + eventNb, "/editClubEvent.seam");
            m_data.put("title_" + eventNb, "");
            m_data.put("club_" + eventNb, "0");
            m_data.put("event_" + eventNb, "0");

         }

      }

      public Object getData()
      {

         return m_data;

      }

      public int getDay()
      {

         Calendar c = Calendar.getInstance();
         c.setTime(m_eventStartDate);

         return c.get(Calendar.DAY_OF_MONTH);

      }

      public String getStyleClass()
      {

         return "rel-hol";

      }

      public Object getToolTip()
      {

         return m_tooltip;

      }

      public boolean hasToolTip()
      {

         return true;

      }

      public boolean isEnabled()
      {

         return true;

      }

   }

   protected CalendarDataModelItem createDataModelItem(Date date,
      List<ClubEvent> events)
   {

      ClubEventCalendarModelItem item =
         new ClubEventCalendarModelItem(date, events);

      //      Map data = new HashMap();
      //
      //      //data.put("shortDescription", "Not planed");
      //      item.setIsEmpty(false);
      //
      //      if (detail != null)
      //      {
      //
      //         data.put("shortDescription", detail.getStrEventdescription());
      //         data.put("more", " More...");
      //         item.setIsEmpty(true);
      //
      //      }
      //
      //      DateFormat df = new SimpleDateFormat(CommonConstant.SHORT_DATE_FORMAT);
      //      data.put("description", "");
      //
      //      String val = df.format(date);
      //      data.put("date", val);
      //
      //      Calendar c = Calendar.getInstance();
      //      c.setTime(date);
      //      item.setDay(c.get(Calendar.DAY_OF_MONTH));
      //      item.setEnabled(true);
      //      item.setStyleClass("rel-hol");
      //      item.setData(data);
      return item;

   }

   /**
    *
    * @see org.richfaces.component.CalendarDataModel#getData(java.util.Date[])
    */
   private Date m_selectedDate;
   private CalendarDataModelItem[] m_items;
   private Date currentDate;
   private boolean currentDisabled;

   /**
    * @see org.richfaces.model.CalendarDataModel#getData(java.util.Date[])
    */
   @SuppressWarnings("unchecked")
   public CalendarDataModelItem[] getData(Date[] dateList)
   {

      if (dateList == null)
      {

         return null;

      }

      if (m_items == null)
      {

         Calendar cal = Calendar.getInstance();
         Query query =
            entityManager.createQuery(
               "SELECT c FROM ClubEvent c WHERE ((c.startTime >= :beginday) AND (c.startTime < :endday))");
         m_items = new CalendarDataModelItem[dateList.length];

         for (int i = 0; i < dateList.length; i++)
         {

            cal.setTime(dateList[i]);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            query.setParameter("beginday", cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
            query.setParameter("endday", cal.getTime());
            m_items[i] =
               createDataModelItem(dateList[i],
                  (List<ClubEvent>)(query.getResultList()));

         }

      }

      return m_items;

   }

   /**
    * @see org.richfaces.model.CalendarDataModel#getToolTip(java.util.Date)
    */
   public Object getToolTip(Date date)
   {

      // TODO Auto-generated method stub
      return null;

   }

   /**
    * @return items
    */
   public CalendarDataModelItem[] getItems()
   {

      return m_items;

   }

   /**
    * @param setter for items
    */
   public void setItems(CalendarDataModelItem[] items)
   {

      this.m_items = items;

   }

   /**
    * @param valueChangeEvent handling
    */
   public void valueChanged(ValueChangeEvent event)
   {

      log.info(event.getNewValue() + "selected");
      setCurrentDate((Date)event.getNewValue());

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(getCurrentDate());
      //      setCurrentDescription((String)
      //         ((HashMap)m_items[calendar.get(Calendar.DAY_OF_MONTH) - 1].getData())
      //         .get("description"));
      //      setCurrentShortDescription((String)
      //         ((HashMap)m_items[calendar.get(Calendar.DAY_OF_MONTH) - 1].getData())
      //         .get("shortDescription"));

   }

   /**
    * Storing changes action
    */
   public void storeDayDetails()
   {

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(getCurrentDate());
      //      ((HashMap)m_items[calendar.get(Calendar.DAY_OF_MONTH) - 1].getData()).put(
      //         "shortDescription", getCurrentShortDescription());
      //      ((HashMap)m_items[calendar.get(Calendar.DAY_OF_MONTH) - 1].getData()).put(
      //         "description", getCurrentDescription());

   }

   /**
    * @return currentDisabled
    */
   public boolean isCurrentDisabled()
   {

      return currentDisabled;

   }

   /**
    * @param currentDisabled
    */
   public void setCurrentDisabled(boolean currentDisabled)
   {

      this.currentDisabled = currentDisabled;

   }

   /**
    * @return currentDate
    */
   public Date getCurrentDate()
   {

      return currentDate;

   }

   /**
    * @param currentDate
    */
   public void setCurrentDate(Date currentDate)
   {

      this.currentDate = currentDate;

   }

   //-------------------------------------
   // Used to save the skin
   //-------------------------------------
   public void setSelectedDate(Date selectedDate)
   {

      this.m_selectedDate = selectedDate;

   }

   @Transient
   public Date getSelectedDate()
   {

      if (m_selectedDate == null)
      {

         m_selectedDate = Calendar.getInstance().getTime();

      }

      return m_selectedDate;

   }

}