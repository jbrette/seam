package org.jboss.seam.example.webassoc.sponsors;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;

import java.util.Date;

@Name("advertisementController")
public class AdvertisementController
{

   @Asynchronous
   public void processRecurringPayment(@Expiration
      Date date, @IntervalDuration
      Long interval)
   {

   }

}