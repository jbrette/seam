package org.jboss.seam.example.webassoc.orders;

import org.jboss.seam.annotations.Name;

import javax.persistence.Entity;

@Entity
@Name("accountOrder")
public class AccountOrder
        extends CoreOrder
{

   private static final long serialVersionUID = 3090209745025136449L;
   private String m_firstName1;
   private String m_firstName2;
   private String m_firstName3;
   private String m_firstName4;
   private String m_firstName5;
   private String m_firstName6;
   private String m_firstName7;

   public String getFirstName()
   {

      return m_firstName1;

   }

   public void setFirstName(String firstName)
   {

      this.m_firstName1 = firstName;

   }

   public String getSpouseFirstName()
   {

      return m_firstName2;

   }

   public void setSpouseFirstName(String firstName)
   {

      this.m_firstName2 = firstName;

   }

   public String getChildFirstName1()
   {

      return m_firstName3;

   }

   public void setChildFirstName1(String firstName)
   {

      this.m_firstName3 = firstName;

   }

   public String getChildFirstName2()
   {

      return m_firstName4;

   }

   public void setChildFirstName2(String firstName)
   {

      this.m_firstName4 = firstName;

   }

   public String getChildFirstName3()
   {

      return m_firstName5;

   }

   public void setChildFirstName3(String firstName)
   {

      this.m_firstName5 = firstName;

   }

   public String getChildFirstName4()
   {

      return m_firstName6;

   }

   public void setChildFirstName4(String firstName)
   {

      this.m_firstName6 = firstName;

   }

   public String getChildFirstName5()
   {

      return m_firstName7;

   }

   public void setChildFirstName5(String firstName)
   {

      this.m_firstName7 = firstName;

   }

}