package org.jboss.seam.example.webassoc.test;

import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.mock.SeamTest;

import org.testng.annotations.Test;

import java.util.List;

public class WebAssocTest
        extends SeamTest
{

   @Test
   public void testList()
           throws Exception
   {

      new NonFacesRequest("/searchAssocMember.xhtml")
         {

            @SuppressWarnings("unchecked")
            @Override
            protected void renderResponse()
                    throws Exception
            {

               List<AssocMember> assocMembers =
                  (List<AssocMember>)getValue("#{assocMembers.resultList}");
               assert assocMembers.size() == 5;

            }

         }.run();

   }

   @Test
   public void testSearch()
           throws Exception
   {

      new FacesRequest("/searchAssocMember.xhtml")
         {

            @Override
            protected void updateModelValues()
                    throws Exception
            {

               setValue("#{globals.searchedString}", "Norman");

            }

            @Override
            protected void invokeApplication()
                    throws Exception
            {

               setOutcome("/searchAssocMember.xhtml");

            }

            @Override
            protected void afterRequest()
            {

               assert !isRenderResponseBegun();

            }

         }.run();
      new NonFacesRequest("/searchAssocMember.xhtml")
         {

            @Override
            protected void beforeRequest()
            {

               setParameter("firstName", "Norman");

            }

            @SuppressWarnings("unchecked")
            @Override
            protected void renderResponse()
                    throws Exception
            {

               List<AssocMember> assocMembers =
                  (List<AssocMember>)getValue("#{assocMembers.resultList}");
               assert assocMembers.size() == 1;

            }

         }.run();
      new FacesRequest("/searchAssocMember.xhtml")
         {

            @Override
            protected void updateModelValues()
                    throws Exception
            {

               setValue("#{globals.searchedString}", "King");

            }

            @Override
            protected void invokeApplication()
                    throws Exception
            {

               setOutcome("/searchAssocMember.xhtml");

            }

            @Override
            protected void afterRequest()
            {

               assert !isRenderResponseBegun();

            }

         }.run();
      new NonFacesRequest("/searchAssocMember.xhtml")
         {

            @Override
            protected void beforeRequest()
            {

               setParameter("assocName", "King");

            }

            @SuppressWarnings("unchecked")
            @Override
            protected void renderResponse()
                    throws Exception
            {

               List<AssocMember> assocMembers =
                  (List<AssocMember>)getValue("#{assocMembers.resultList}");
               assert assocMembers.size() == 1;

            }

         }.run();

   }

   String assocMemberId;

   @Test
   public void testCreateDeleteAssocMember()
           throws Exception
   {

      new FacesRequest("/editAssocMember.xhtml")
         {

            @Override
            protected void updateModelValues()
                    throws Exception
            {

               setValue("#{assocMember.firstName}", "Emmanuel");
               setValue("#{assocMember.assocName}", "Bernard");
               setValue("#{assocMember.city}", "Paris");

            }

            @Override
            protected void invokeApplication()
                    throws Exception
            {

               assert invokeMethod("#{assocMemberHome.persist}").equals(
                     "persisted");
               assocMemberId = getValue("#{assocMemberHome.id}").toString();

            }

         }.run();
      new NonFacesRequest("/viewAssocMember.xhtml")
         {

            @Override
            protected void beforeRequest()
            {

               setParameter("assocMemberId", assocMemberId);

            }

            @Override
            protected void renderResponse()
                    throws Exception
            {

               assert getValue("#{assocMember.firstName}").equals("Emmanuel");
               assert getValue("#{assocMember.assocName}").equals("Bernard");
               assert getValue("#{assocMember.city}").equals("Paris");

            }

         }.run();
      new FacesRequest("/viewAssocMember.xhtml")
         {

            @Override
            protected void beforeRequest()
            {

               setPageParameter("assocMemberId", new Long(assocMemberId));

            }

            @Override
            protected void invokeApplication()
                    throws Exception
            {

               assert invokeMethod("#{assocMemberHome.remove}").equals(
                     "removed");

            }

         }.run();

   }

}