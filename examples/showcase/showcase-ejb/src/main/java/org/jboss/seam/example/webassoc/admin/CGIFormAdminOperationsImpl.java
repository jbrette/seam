package org.jboss.seam.example.webassoc.admin;

import org.apache.commons.io.IOUtils;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.example.webassoc.clubs.Club;
import org.jboss.seam.example.webassoc.mail.Mailing;
import org.jboss.seam.example.webassoc.mship.AssocMember;
import org.jboss.seam.example.webassoc.mship.ClubMembership;
import org.jboss.seam.example.webassoc.mship.FamilyMember;
import org.jboss.seam.example.webassoc.util.Cts;
import org.jboss.seam.example.webassoc.util.FormEntry;
import org.jboss.seam.example.webassoc.util.GMAPAddress;
import org.jboss.seam.log.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

@Stateful
@Scope(ScopeType.SESSION)
@Name("cgiFormAdminOperations")
public class CGIFormAdminOperationsImpl
        implements AdminOperations,
           Serializable
{

   private static final long serialVersionUID = 7171800156738447863L;
   @Logger
   private Log log;
   @PersistenceContext
   private EntityManager m_em;

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton1()
   {

      log.info("CGIFormAdminOperationsImpl:checkAptOrSuite invoked");
      checkAptOrSuite();

   }

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton2()
   {

      log.info("CGIFormAdminOperationsImpl:updateLngLat invoked");
      updateLngLat();

   }

   @Restrict("#{s:hasRole('admin')}")
   public void invokedByMenuButton3()
   {

      log.info("CGIFormAdminOperationsImpl:checkDatabase invoked");
      checkDatabase();

   }

   @Restrict("#{s:hasRole('admin')}")
   @SuppressWarnings("unchecked")
   public void checkAptOrSuite()
   {

      List<AssocMember> assocMembers =
         m_em.createQuery("from AssocMember a").getResultList();
      String[] lookedStrings =
         new String[] {"#", "Apt", "App", "Appar", "app", ","};

      for (AssocMember assocMember : assocMembers)
      {

         int foundIndex = -1;
         String address = assocMember.getAddress();

         for (int i = 0; (foundIndex == -1) && (i < lookedStrings.length); i++)
         {

            foundIndex = address.indexOf(lookedStrings[i]);

         }

         if (foundIndex != -1)
         {

            if ((assocMember.getAptOrSuite() == null)
               || (assocMember.getAptOrSuite().length() == 0))
            {

               assocMember.setAptOrSuite(address.substring(foundIndex));
               assocMember.setAddress(address.substring(0, foundIndex - 1));
               m_em.persist(assocMember);

            }

         }
         else
         {

            if ((assocMember.getAptOrSuite() == null)
               || (assocMember.getAptOrSuite().length() == 0))
            {

               assocMember.setAptOrSuite("");
               m_em.persist(assocMember);

            }

         }

      }

   }

   @Restrict("#{s:hasRole('admin')}")
   @SuppressWarnings("unchecked")
   public void updateLngLat()
   {

      List<AssocMember> assocMembers =
         m_em.createQuery("from AssocMember a where addressValid = true")
         .getResultList();

      for (AssocMember assocMember : assocMembers)
      {

         try
         {

            GMAPAddress gmapAddr =
               retrieveAddressThroughHTTPXML(assocMember.getGMAPAddress(),
                  true, false, true, false);

            if (gmapAddr != null)
            {

               log.info(gmapAddr);

               if (gmapAddr.getFullAddress() != null)
               {

                  assocMember.setGMAPLat(gmapAddr.getLat());
                  assocMember.setGMAPLng(gmapAddr.getLng());
                  m_em.persist(assocMember);

               }

            }

         }
         catch (XPathExpressionException e)
         {

            log.error(assocMember.getGMAPAddress(), e);

         }
         catch (IOException e)
         {

            log.error(assocMember.getGMAPAddress(), e);

         }
         catch (SAXException e)
         {

            log.error(assocMember.getGMAPAddress(), e);

         }
         catch (ParserConfigurationException e)
         {

            log.error(assocMember.getGMAPAddress(), e);

         }

      }

   }

   /**
    * Retrieve the lat,long through HTTP/JSON
    *
    * @param address
    * @throws IOException
    * @throws XPathExpressionException
    * @throws SAXException
    * @throws ParserConfigurationException
    */
   @Restrict("#{s:hasRole('admin')}")
   public GMAPAddress retrieveAddressThroughHTTPJSON(String address)
           throws IOException, XPathExpressionException, SAXException,
              ParserConfigurationException
   {

      GMAPAddress gmapAddr = new GMAPAddress();

      // prepare a URL to the geocoder
      URL url =
         new URL("http://maps.google.com/maps/api/geocode/json" + "?address="
            + URLEncoder.encode(address, "UTF-8") + "&sensor=false" + "&key="
            + Cts.GMAP.KEY);

      // prepare an HTTP connection to the geocoder
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      String jsonString = null;

      try
      {

         // open the connection and get results as InputSource.
         conn.connect();

         ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
         IOUtils.copy(conn.getInputStream(), output);
         output.close();
         jsonString = output.toString();

      }
      finally
      {

         conn.disconnect();

      }

      log.info("JSON string for [" + address + "] is " + jsonString);

      return gmapAddr;

   }

   /**
    * Retrieve the lat,long through HTTP/XML
    *
    * @param address
    * @throws IOException
    * @throws XPathExpressionException
    * @throws SAXException
    * @throws ParserConfigurationException
    */
   @Restrict("#{s:hasRole('admin')}")
   public GMAPAddress retrieveAddressThroughHTTPXML(String address,
      boolean extractedFormattedAddress, boolean extractLocality,
      boolean extractLongLat1, boolean extractLongLat2)
           throws IOException, XPathExpressionException, SAXException,
              ParserConfigurationException
   {

      GMAPAddress gmapAddr = new GMAPAddress();

      // prepare a URL to the geocoder
      URL url =
         new URL("http://maps.google.com/maps/api/geocode/xml" + "?address="
            + URLEncoder.encode(address, "UTF-8") + "&sensor=false" + "&key="
            + Cts.GMAP.KEY);

      // prepare an HTTP connection to the geocoder
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      Document geocoderResultDocument = null;

      try
      {

         // open the connection and get results as InputSource.
         conn.connect();

         InputSource geocoderResultInputSource =
            new InputSource(conn.getInputStream());

         // read result and parse into XML Document
         geocoderResultDocument =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
               geocoderResultInputSource);

      }
      finally
      {

         conn.disconnect();

      }

      // prepare XPath
      XPath xpath = XPathFactory.newInstance().newXPath();

      // extract the result
      NodeList resultNodeList = null;

      if (extractedFormattedAddress)
      {

         // a) obtain the formatted_address field for every result
         resultNodeList =
            (NodeList)xpath.evaluate(
               "/GeocodeResponse/result/formatted_address",
               geocoderResultDocument, XPathConstants.NODESET);

         for (int i = 0; i < resultNodeList.getLength(); ++i)
         {

            gmapAddr.setFullAddress(resultNodeList.item(i).getTextContent());

         }

      }

      if (extractLocality)
      {

         // b) extract the locality for the first result
         resultNodeList =
            (NodeList)xpath.evaluate(
               "/GeocodeResponse/result[1]/address_component[type/text()='locality']/long_name",
               geocoderResultDocument, XPathConstants.NODESET);

         for (int i = 0; i < resultNodeList.getLength(); ++i)
         {

            System.out.println(resultNodeList.item(i).getTextContent());

         }

      }

      if (extractLongLat1)
      {

         // c) extract the coordinates of the first result
         resultNodeList =
            (NodeList)xpath.evaluate(
               "/GeocodeResponse/result[1]/geometry/location/*",
               geocoderResultDocument, XPathConstants.NODESET);
         gmapAddr.setLat(Cts.GMAP.LAT);
         gmapAddr.setLng(Cts.GMAP.LNG);

         for (int i = 0; i < resultNodeList.getLength(); ++i)
         {

            Node node = resultNodeList.item(i);

            if ("lat".equals(node.getNodeName()))
            {

               gmapAddr.setLat(Double.parseDouble(node.getTextContent()));

            }

            if ("lng".equals(node.getNodeName()))
            {

               gmapAddr.setLng(Double.parseDouble(node.getTextContent()));

            }

         }

      }

      if (extractLongLat2)
      {

         // c) extract the coordinates of the first result
         resultNodeList =
            (NodeList)xpath.evaluate(
               "/GeocodeResponse/result[1]/address_component[type/text() = 'administrative_area_level_1']/country[short_name/text() = 'US']/*",
               geocoderResultDocument, XPathConstants.NODESET);
         gmapAddr.setLat(Cts.GMAP.LAT);
         gmapAddr.setLng(Cts.GMAP.LNG);

         for (int i = 0; i < resultNodeList.getLength(); ++i)
         {

            Node node = resultNodeList.item(i);

            if ("lat".equals(node.getNodeName()))
            {

               gmapAddr.setLat(Double.parseDouble(node.getTextContent()));

            }

            if ("lng".equals(node.getNodeName()))
            {

               gmapAddr.setLng(Double.parseDouble(node.getTextContent()));

            }

         }

      }

      return gmapAddr;

   }

   @Restrict("#{s:hasRole('admin')}")
   @SuppressWarnings("unchecked")
   public void checkDatabase()
   {

      List<AssocMember> assocMembers =
         m_em.createQuery("from AssocMember a").getResultList();

      for (AssocMember assocMember : assocMembers)
      {

         String formulaire = assocMember.getFormulaire();

         if ((formulaire != null) && (formulaire.length() != 0))
         {

            List<FormEntry> parts = assocMember.getFormulaireParts();

            if (parts.size() < 40)
            {

               compareStringValue(assocMember, parts, FormFieldOld.Nom,
                  assocMember.getAssocName(), 1);
               compareStringValue(assocMember, parts, FormFieldOld.Addresse,
                  assocMember.getAddress(), 1);
               compareStringValue(assocMember, parts, FormFieldOld.Ville,
                  assocMember.getCity(), 1);
               compareStringValue(assocMember, parts, FormFieldOld.CodePostal,
                  assocMember.getZip(), 1);
               compareStringValue(assocMember, parts, FormFieldOld.EMail,
                  assocMember.getEmail(), 1);
               compareStringValue(assocMember, parts, FormFieldOld.Telephone,
                  assocMember.getHomePhone().replaceAll("-", ""), 1);

               //               compareBooleanValue(assocMember, parts, FormFieldOld.Guide,
               //                  assocMember.getGuide(), 1);
               List<String> prenoms = new ArrayList<String>();
               String extracted = null;
               extracted =
                  extractOneValue(assocMember, parts, FormFieldOld.Prenom, 1);

               if (extracted != null)
               {

                  prenoms.add(extracted.toLowerCase());

               }

               extracted =
                  extractOneValue(assocMember, parts,
                     FormFieldOld.PrenomConjoint, 1);

               if (extracted != null)
               {

                  prenoms.add(extracted.toLowerCase());

               }

               compareFamillyMembers(assocMember, prenoms);

            }
            else
            {

               compareStringValue(assocMember, parts, FormFieldNew.Nom,
                  assocMember.getAssocName(), 1);
               compareStringValue(assocMember, parts, FormFieldNew.Addresse,
                  assocMember.getAddress(), 1);
               compareStringValue(assocMember, parts, FormFieldNew.Ville,
                  assocMember.getCity(), 1);
               compareStringValue(assocMember, parts, FormFieldNew.CodePostal,
                  assocMember.getZip(), 1);
               compareStringValue(assocMember, parts, FormFieldNew.EMail,
                  assocMember.getEmail(), 1);
               compareStringValue(assocMember, parts, FormFieldNew.Telephone,
                  assocMember.getHomePhone().replaceAll("-", ""), 1);

               //               compareBooleanValue(assocMember, parts, FormFieldNew.Guide,
               //                  assocMember.getGuide(), 1);
               List<String> prenoms = new ArrayList<String>();
               String extracted = null;
               extracted =
                  extractOneValue(assocMember, parts, FormFieldNew.Prenom, 1);

               if (extracted != null)
               {

                  prenoms.add(extracted.toLowerCase());

               }

               extracted =
                  extractOneValue(assocMember, parts,
                     FormFieldNew.PrenomConjoint, 1);

               if (extracted != null)
               {

                  prenoms.add(extracted.toLowerCase());

               }

               extracted =
                  extractOneValue(assocMember, parts,
                     FormFieldNew.PrenonEnfant1, 1);

               if (extracted != null)
               {

                  prenoms.add(extracted.toLowerCase());

               }

               extracted =
                  extractOneValue(assocMember, parts,
                     FormFieldNew.PrenonEnfant2, 1);

               if (extracted != null)
               {

                  prenoms.add(extracted.toLowerCase());

               }

               extracted =
                  extractOneValue(assocMember, parts,
                     FormFieldNew.PrenonEnfant3, 1);

               if (extracted != null)
               {

                  prenoms.add(extracted.toLowerCase());

               }

               extracted =
                  extractOneValue(assocMember, parts,
                     FormFieldNew.PrenonEnfant4, 1);

               if (extracted != null)
               {

                  prenoms.add(extracted.toLowerCase());

               }

               extracted =
                  extractOneValue(assocMember, parts,
                     FormFieldNew.PrenonEnfant5, 1);

               if (extracted != null)
               {

                  prenoms.add(extracted.toLowerCase());

               }

               compareFamillyMembers(assocMember, prenoms);

            }

         }
         else
         {

            // log.info("FOO1");
         }

      }

   }

   private boolean compareFamillyMembers(AssocMember assocMember,
      List<String> prenoms)
   {

      List<FamilyMember> fmembers = assocMember.getFamilyMembers();

      if (fmembers.size() != prenoms.size())
      {

         log.error("PRENOMS for member: [" + assocMember.getAssocName() + "] ["
            + fmembers.size() + "] [" + prenoms.size() + "]");

         return false;

      }
      else
      {

         List<String> dbprenoms = new ArrayList<String>();

         for (FamilyMember fmember : fmembers)
         {

            if (!prenoms.contains(fmember.getFirstName().toLowerCase()))
            {

               log.error("DBHASTOMANY for member: ["
                  + assocMember.getAssocName() + "] FIRSTNAME ["
                  + fmember.getFirstName() + "]");

            }

            dbprenoms.add(fmember.getFirstName().toLowerCase());

         }

         for (String prenom : prenoms)
         {

            if (!dbprenoms.contains(prenom))
            {

               log.error("DBMISSING for member: [" + assocMember
                  .getAssocName() + "] FIRSTNAME [" + prenom + "]");

            }

         }

         return true;

      }

   }

   private boolean compareStringValue(AssocMember assocMember,
      List<FormEntry> parts, int formField, String dbValue, int expectedLength)
   {

      String extracted =
         extractOneValue(assocMember, parts, formField, expectedLength);

      if (extracted == null)
      {

         return false;

      }

      if (!dbValue.equalsIgnoreCase(extracted))
      {

         log.error("MISMATCH for member: [" + assocMember.getAssocName()
            + "] field [" + formField + "]  dbvalue [" + dbValue
            + "] formvalue [" + extracted + "]");

         return false;

      }
      else
      {

         return true;

      }

   }

   private String extractOneValue(AssocMember assocMember,
      List<FormEntry> parts, int formField, int expectedLength)
   {

      List<String> finds =
         extractValue(parts, String.valueOf(formField), false);

      if (finds.size() != expectedLength)
      {

         log.error("MISSING for member: [" + assocMember.getAssocName()
            + "] field [" + formField + "]");

         return null;

      }
      else
      {

         String res = finds.get(0).trim();

         return (((res == null) || (res.length() == 0)) ? null : res);

      }

   }

   private List<String> extractValue(List<FormEntry> parts, String looked,
      boolean removeComas)
   {

      List<String> res = new ArrayList<String>();

      for (FormEntry formEntry : parts)
      {

         if (formEntry.getNb().equals(looked))
         {

            res.add((!removeComas)
               ? formEntry.getValue()
               : formEntry.getValue().replaceAll(",", "").replaceAll("!", "")
                  .trim());

         }

      }

      return res;

   }

   @Restrict("#{s:hasRole('admin')}")
   @SuppressWarnings("unchecked")
   public void initClubMemberships2()
   {

      List<AssocMember> assocMembers =
         m_em.createQuery("from AssocMember a").getResultList();

      for (AssocMember assocMember : assocMembers)
      {

         String formulaire = assocMember.getFormulaire();

         if ((formulaire != null) && (formulaire.length() != 0))
         {

            log.info("Processing [" + assocMember.getAssocName() + "]");

            List<FormEntry> parts = assocMember.getFormulaireParts();

            if (parts.size() < 40)
            {

               List<String> prenoms = new ArrayList<String>();
               String extracted = null;
               extracted =
                  extractOneValue(assocMember, parts, FormFieldOld.Prenom, 1);

               if (extracted != null)
               {

                  prenoms.add(extracted.toLowerCase());

               }

               // extracted = extractOneValue(assocMember, parts, FormFieldOld.PrenomConjoint, 1);
               // if (extracted != null) prenoms.add(extracted.toLowerCase());
               List<String> clubs =
                  extractValue(parts, String.valueOf(FormFieldOld.Clubs),
                     true);
               addMemberships(assocMember, prenoms, clubs);

            }
            else
            {

               List<String> prenoms = new ArrayList<String>();
               String extracted = null;
               extracted =
                  extractOneValue(assocMember, parts, FormFieldNew.Prenom, 1);

               if (extracted != null)
               {

                  prenoms.add(extracted.toLowerCase());

               }

               // extracted = extractOneValue(assocMember, parts, FormFieldNew.PrenomConjoint, 1);
               // if (extracted != null) prenoms.add(extracted.toLowerCase());
               List<String> clubs =
                  extractValue(parts, String.valueOf(FormFieldNew.Clubs),
                     true);
               addMemberships(assocMember, prenoms, clubs);

            }

         }
         else
         {

            log.info("Skipped [" + assocMember.getAssocName() + "]");

         }

      }

   }

   private String dbNameToFormName(String clubName)
   {

      /**
       * NO CLUB FOR THOSE FORM ACTIVITIES:
       *
      * return "Activités culturelles";
      * return "Cercle de lecture";
      * return "Happy hours";
      **/
      // String clubName = clubNameOrg.replaceAll("é","e").replaceAll("è", "e").toLowerCase();
      if (clubName.equals("Potluck"))
      {

         return clubName;

      }
      else if (clubName.equals("Cafe Rencontre"))
      {

         return "Café rencontre";

      }
      else if (clubName.equals("Bibliotheque tournante"))
      {

         return "Bibliothèque";

      }
      else if (clubName.equals("Ceramique"))
      {

         return "Peinture sur céramique";

      }
      else if (clubName.equals("Cinéma"))
      {

         return "Cinéma";

      }
      else if (clubName.equals("Cuisine"))
      {

         return "Cuisine";

      }
      else if (clubName.equals("Conversation Franco-Americaine"))
      {

         return "Conversations Franco américaine";

      }
      else if (clubName.equals("Encadrement"))
      {

         return "Encadrement";

      }
      else if (clubName.equals("Eveil Jeunes Enfants"))
      {

         return "Activités enfants";

      }
      else if (clubName.equals("Marche"))
      {

         return "Sport";

      }
      else if (clubName.equals("Patchwork"))
      {

         return "Patchwork";

      }
      else if (clubName.equals("Tarot"))
      {

         return clubName;

      }
      else if (clubName.equals("VTT Week-end"))
      {

         return clubName;

      }
      else if (clubName.equals("Peinture"))
      {

         return clubName;

      }
      else if (clubName.equals("Evenements speciaux"))
      {

         return clubName;

      }
      else if (clubName.equals("Baby-sitting Motorisé"))
      {

         return clubName;

      }
      else if (clubName.equals("Baby-sitting Non Motorisé"))
      {

         return clubName;

      }
      else
      {

         return clubName;

      }

   }

   @SuppressWarnings("unchecked")
   private boolean addMemberships(AssocMember assocMember,
      List<String> prenoms, List<String> clubNames)
   {

      List<FamilyMember> fmembers = assocMember.getFamilyMembers();

      if ((fmembers.size() == 0) || (prenoms.size() == 0)
         || (clubNames.size() == 0))
      {

         log.error("PRENOMS for member: [" + assocMember.getAssocName() + "] ["
            + fmembers.size() + "] [" + prenoms.size() + "]");

         return false;

      }
      else
      {

         String clubNamesString = "";

         for (String tmp : clubNames)
         {

            clubNamesString += "[" + tmp + "]";

         }

         log.info("Adding club to [" + assocMember.getAssocName() + "] "
            + clubNamesString);

         List<Club> clubs =
            m_em.createQuery(
               "from Club c where c.automaticMembership = :automaticMembership")
            .setParameter("automaticMembership", Boolean.FALSE)
            .getResultList();
         Query alreadyInMailingList =
            m_em.createQuery(
               "select m from ClubMembership m where m.member = :member and m.club = :club");

         for (FamilyMember facilityMember : fmembers)
         {

            if (prenoms.contains(facilityMember.getFirstName().toLowerCase()))
            {

               // We found it
               facilityMember = m_em.merge(facilityMember);

               for (Club club : clubs)
               {

                  facilityMember = m_em.merge(facilityMember);

                  if (clubNames.contains(dbNameToFormName(club.getName())))
                  {

                     alreadyInMailingList.setParameter("member",
                        facilityMember);
                     alreadyInMailingList.setParameter("club", club);

                     List<Mailing> found =
                        alreadyInMailingList.getResultList();

                     if (found.size() == 0)
                     {

                        ClubMembership membership = new ClubMembership();
                        membership.setClub(club);
                        club.getClubMembers().add(membership);
                        membership.setMember(facilityMember);
                        facilityMember.getMemberships().add(membership);
                        membership.setActive(Boolean.TRUE);
                        membership.setFKind(ClubMembership.FKind.member);
                        m_em.persist(membership);

                     }
                     else
                     {

                        log.info(club.getName() + " already in db");

                     }

                  }
                  else
                  {

                     log.info("Not found [" + club.getName() + "]["
                        + dbNameToFormName(club.getName()) + "]");

                  }

               }

            }

         }

         return true;

      }

   }

   @Remove
   public void destroy()
   {

      // TODO Auto-generated method stub
   }

}