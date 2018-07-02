package org.jboss.seam.example.webassoc.util;

public enum OccupationCode
{

   ___(0), // Not Specified
   Home(1), // Stays home
   Sponsor_Compagny(2), // Employed by one of the sponsor
   Public_Sector(3), // Local public sector
   Private_Compagny(2), // Local compagny
   International_Compagny(6), // Foreign company
   Foreign_Public_Sector(7), // Foreign public sector
   Other(8), // Other
   International_School(9), // Student in the foreign school
   Public_School(10), // Student in a public school
   Private_School(11), // Student in a private school
   Charter_School(12), // Student in a charter school
   US_University(13), French_University(14), Canadia_University(15);

   private int id;

   private OccupationCode(int id)
   {

      this.id = id;

   }

   public int getId()
   {

      return id;

   }

}