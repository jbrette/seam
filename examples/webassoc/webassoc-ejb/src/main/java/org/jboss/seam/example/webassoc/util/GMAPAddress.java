package org.jboss.seam.example.webassoc.util;

public class GMAPAddress
{

   public String address;
   public String fullAddress;
   public String zipCode;
   public String city;
   public String state;
   public double lat;
   public double lng;

   public String getAddress()
   {

      return address;

   }

   public void setAddress(String address)
   {

      this.address = address;

   }

   public String getFullAddress()
   {

      return fullAddress;

   }

   public void setFullAddress(String fullAddress)
   {

      this.fullAddress = fullAddress;

   }

   public String getZipCode()
   {

      return zipCode;

   }

   public void setZipCode(String zipCode)
   {

      this.zipCode = zipCode;

   }

   public String getCity()
   {

      return city;

   }

   public void setCity(String city)
   {

      this.city = city;

   }

   public String getState()
   {

      return state;

   }

   public void setState(String state)
   {

      this.state = state;

   }

   public double getLat()
   {

      return lat;

   }

   public void setLat(double lat)
   {

      this.lat = lat;

   }

   public double getLng()
   {

      return lng;

   }

   public void setLng(double lng)
   {

      this.lng = lng;

   }

   @Override
   public int hashCode()
   {

      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((address == null) ? 0 : address.hashCode());
      result = (prime * result) + ((city == null) ? 0 : city.hashCode());
      result =
         (prime * result)
         + ((fullAddress == null) ? 0 : fullAddress.hashCode());

      long temp;
      temp = Double.doubleToLongBits(lat);
      result = (prime * result) + (int)(temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(lng);
      result = (prime * result) + (int)(temp ^ (temp >>> 32));
      result = (prime * result) + ((state == null) ? 0 : state.hashCode());
      result = (prime * result) + ((zipCode == null) ? 0 : zipCode.hashCode());

      return result;

   }

   @Override
   public boolean equals(Object obj)
   {

      if (this == obj)
      {

         return true;

      }

      if (obj == null)
      {

         return false;

      }

      if (getClass() != obj.getClass())
      {

         return false;

      }

      GMAPAddress other = (GMAPAddress)obj;

      if (address == null)
      {

         if (other.address != null)
         {

            return false;

         }

      }
      else if (!address.equals(other.address))
      {

         return false;

      }

      if (city == null)
      {

         if (other.city != null)
         {

            return false;

         }

      }
      else if (!city.equals(other.city))
      {

         return false;

      }

      if (fullAddress == null)
      {

         if (other.fullAddress != null)
         {

            return false;

         }

      }
      else if (!fullAddress.equals(other.fullAddress))
      {

         return false;

      }

      if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
      {

         return false;

      }

      if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng))
      {

         return false;

      }

      if (state == null)
      {

         if (other.state != null)
         {

            return false;

         }

      }
      else if (!state.equals(other.state))
      {

         return false;

      }

      if (zipCode == null)
      {

         if (other.zipCode != null)
         {

            return false;

         }

      }
      else if (!zipCode.equals(other.zipCode))
      {

         return false;

      }

      return true;

   }

   @Override
   public String toString()
   {

      return "GAddress [address=" + address + ", fullAddress=" + fullAddress
         + ", zipCode=" + zipCode + ", city=" + city + ", state=" + state
         + ", lat=" + lat + ", lng=" + lng + "]";

   }

}