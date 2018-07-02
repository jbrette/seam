package org.jboss.seam.example.webassoc.util;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.Calendar;
import java.util.Date;

@Name("codeList")
@Scope(ScopeType.STATELESS)
public class CodeList
{

   public OccupationCode[] getOccupationCodes()
   {

      return OccupationCode.values();

   }

   public UNCountryCode[] getUNCountryCodes()
   {

      return UNCountryCode.values();

   }

   public USStateCode[] getUSStateCodes()
   {

      return USStateCode.values();

   }

   public String[] getUSStateCodesAsStrings()
   {

      USStateCode[] tmp = USStateCode.values();
      String[] res = new String[tmp.length];

      for (int i = 0; i < tmp.length; i++)
      {

         res[i] = tmp[i].name();

      }

      return res;

   }

   public Date[] getYearList()
   {

      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.DAY_OF_YEAR, 1);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);

      Date[] res = new Date[21];
      cal.set(Calendar.YEAR, 1900);
      res[0] = cal.getTime();

      for (int i = 1; i < res.length; i++)
      {

         cal.set(Calendar.YEAR, 1990 + i);
         res[i] = cal.getTime();

      }

      return res;

   }

}