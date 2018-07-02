package org.jboss.seam.example.webassoc.util;

public interface Cts
{

   // Don't understand yet what it is for. Put it to 200
   public static final int QUERY_MAX_RESULT = 200;

   // Offset used during the late member testing
   public static final int LATE_OFFSET = 0;

   // Name of the JMS queue used internally
   public static final String MAILING_QUEUE = "queue/webfaccMailingQueue";

   // Address used for the mailing
   public static final String DO_NOT_REPLY = "donotreply@faccdallas.com";

   // Address used for the secretary
   public static final String SECRETARY = "info@faccdallas.com";

   // Address used for the contact
   public static final String CONTACT_US = "info@faccdallas.com";

   // main home page
   public static final String HOME_PAGE = "www.faccdallas.com";

   // Address used for the mailing
   public static final String PROJECT_NAME = "FACC Dallas";

   // Default Skin
   public static final String DFLT_SKIN = "webfacc";

   // Default Theme
   public static final String DFLT_THEME = "simple";

   public interface GMAP
   {

      // www.faccdallas.com
      public static final String KEY =
         "ABQIAAAAofyPr7f10fiGr5aoCS4eQRRPOifChhNOdrrcKWOetE-0OJ7ePBSrtzB4xgQNRinCNdpmdcwTVz5QLg";
      public static final String KEY_PC =
         "ABQIAAAAofyPr7f10fiGr5aoCS4eQRTV4L3EGkyMwTUg3FkSO_hEBS5L-RQnV_W1GWnp0PJKpo869IZDe6jkHw";
      public static final Double LAT = new Double(32.84383);
      public static final Double LNG = new Double(-96.90216);

   }

   public interface Delays
   {

      public static final int NEWS_LETTER = 1000;
      public static final int CLUB_EVENT = 1000;
      public static final int VOTE = 1000;

   }

   public interface IPs
   {

      // WINDOWS DEV ENVIRONMENT
      public static final String UGLYSERVERNAME = "76.184.176.135:8080";

      // LINUX ENVIRONMENT
      // public static final String UGLYSERVERNAME = "www.faccdallas.com";
      // WAR NAME
      public static final String UGLYWARNAME = "seam-webfacc";

   }

   public interface PresidentIds
   {

      public static final long FamilyMember = 245;
      public static final long AssocMember = 23;

   }

   public interface ClubIds
   {

      public static final long Putlock = 1;
      public static final long CafeRencontre = 2;
      public static final long EvtSpeciaux = 16;

   }

   public interface Dflt
   {

      public static final String E_MAIL = "assocName@someemail.com";
      public static final String PHONE = "000-000-0000";
      public static final String ZIP = "75201";
      public static final String STATE = "TX";
      public static final String COUNTRY = "USA";
      public static final String CITY = "Dallas";
      public static final String ADDRESS = "1500 Marilla St";
      public static final String APTORNB = "";
      public static final String EMPTY = "";
      public static final String FIRST_NAME = "FirstName";

   }

   public interface Roles
   {

      public static final String webuser = "webuser";
      public static final String member = "member";

   }

   public interface Rgx
   {

      // Email format: A valid email address will have following format:
      //         [\\w\\.-]+: Begins with word characters, (may include periods and hypens).
      //     @: It must have a '@' symbol after initial characters.
      //     ([\\w\\-]+\\.)+: '@' must follow by more alphanumeric characters (may include hypens.).
      // This part must also have a "." to separate domain and subdomain names.
      //     [A-Z]{2,4}$ : Must end with two to four alaphabets.
      // (This will allow domain names with 2, 3 and 4 characters e.g pa, com, net, wxyz)
      //
      // Examples: Following email addresses will pass validation abc@xyz.net; ab.c@tx.gov
      //
      public static final String E_MAIL =
         "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

      // Phone Number formats: (nnn)nnn-nnnn; nnnnnnnnnn; nnn-nnn-nnnn
      //    ^\\(? : May start with an option "(" .
      //    (\\d{3}): Followed by 3 digits.
      //    \\)? : May have an optional ")"
      //    [- ]? : May have an optional "-" after the first 3 digits or after optional ) character.
      //    (\\d{3}) : Followed by 3 digits.
      //     [- ]? : May have another optional "-" after numeric digits.
      //     (\\d{4})$ : ends with four digits.
      //
      //         Examples: Matches following <A href="http://mylife.com">phone numbers</A>:
      //         (123)456-7890, 123-456-7890, 1234567890, (123)-456-7890
      //
      //
      public static final String PHONE =
         "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$";
      public static final String PHONE_STRICT = "^(\\d{3})-(\\d{3})-(\\d{4})$";

      // SSN format xxx-xx-xxxx, xxxxxxxxx, xxx-xxxxxx; xxxxx-xxxx:
      //         ^\\d{3}: Starts with three numeric digits.
      //    [- ]?: Followed by an optional "-"
      //    \\d{2}: Two numeric digits after the optional "-"
      //    [- ]?: May contain an optional second "-" character.
      //    \\d{4}: ends with four numeric digits.
      //
      //        Examples: 879-89-8989; 869878789 etc.
      //
      public static final String SSN = "^\\d{3}[- ]?\\d{2}[- ]?\\d{4}$";
      public static final String SSN_STRICT = "^\\d{3}-\\d{2}-\\d{4}$";

      // Number: A numeric value will have following format:
      //         ^[-+]?: Starts with an optional "+" or "-" sign.
      //     [0-9]*: May have one or more digits.
      //    \\.? : May contain an optional "." (decimal point) character.
      //    [0-9]+$ : ends with numeric digit.
      //
      public static final String NUMBER = "^[-+]?[0-9]*\\.?[0-9]+$";
      public static final String ZIP_STRICT = "^(\\d{5})$";
      public static final String STATE_STRICT = "^([A-Z]{2})$";

   }

}