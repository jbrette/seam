package org.jboss.seam.example.webassoc.util;

import java.io.Serializable;

public class FormEntry
        implements Serializable
{

   private static final long serialVersionUID = -6763442495367992921L;
   private String nb;
   private String name;
   private String value;

   public FormEntry()
   {

   }

   public FormEntry(String nb, String name, String value)
   {

      this.nb = nb;
      this.name = name;
      this.value = value;

   }

   public void setNb(String nb)
   {

      this.nb = nb;

   }

   public String getNb()
   {

      return nb;

   }

   public void setName(String name)
   {

      this.name = name;

   }

   public String getName()
   {

      return name;

   }

   public void setValue(String value)
   {

      this.value = value;

   }

   public String getValue()
   {

      return value;

   }

}