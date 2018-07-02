package org.jboss.seam.example.webassoc.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;

import java.net.URL;

import java.util.Arrays;
import java.util.HashSet;

import javax.swing.ImageIcon;

class Thumbnail
{

   static final HashSet<String> IMG_CONTENT_TYPES =
      new HashSet<String>(Arrays.asList(
            new String[]
            {
               "image/pjpeg", "image/x-png", "image/jpeg", "image/gif",
               "image/png", "image/bmp"
            }));
   static final HashSet<String> PDF_CONTENT_TYPES =
      new HashSet<String>(Arrays.asList(new String[] {"application/pdf"}));
   static final HashSet<String> WORD_CONTENT_TYPES =
      new HashSet<String>(Arrays.asList(
            new String[]
            {
               "application/msword",
               "application/vnd.openxmlformats-officedocument.wordprocessingml",
               "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
               "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
               "application/vnd.ms-word.document.macroEnabled.12",
               "application/vnd.ms-word.template.macroEnabled.12",
            }));
   static final HashSet<String> EXCEL_CONTENT_TYPES =
      new HashSet<String>(Arrays.asList(
            new String[]
            {
               "application/msexcel",
               "application/vnd.openxmlformats-officedocument.spreadsheetml",
               "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
               "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
               "application/vnd.ms-excel.sheet.macroEnabled.12",
               "application/vnd.ms-excel.template.macroEnabled.12",
               "application/vnd.ms-excel.addin.macroEnabled.12",
               "application/vnd.ms-excel.sheet.binary.macroEnabled.12",
               "application/vnd.ms-xpsdocument"
            }));
   static final HashSet<String> POWERPOINT_CONTENT_TYPES =
      new HashSet<String>(Arrays.asList(
            new String[]
            {
               "application/mspowerpoint", "application/vnd.ms-powerpoint",
               "application/vnd.openxmlformats-officedocument.presentationml",
               "application/vnd.openxmlformats-officedocument.presentationml.presentation",
               "application/vnd.openxmlformats-officedocument.presentationml.template",
               "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
               "application/vnd.ms-powerpoint.addin.macroEnabled.12",
               "application/vnd.ms-powerpoint.presentation.macroEnabled.12",
               "application/vnd.ms-powerpoint.template.macroEnabled.12",
               "application/vnd.ms-powerpoint.slideshow.macroEnabled.12"
            }));

   /**
   * Reads an image in a file and creates a thumbnail in another file.
   * largestDimension is the largest dimension of the thumbnail, the other dimension is scaled accordingly.
   * Utilises weighted stepping method to gradually reduce the image size for better results,
   * i.e. larger steps to start with then smaller steps to finish with.
   * Note: always writes a JPEG because GIF is protected or something - so always make your outFilename end in 'jpg'.
   * PNG's with transparency are given white backgrounds
   */
   public byte[] createThumbnail1(byte[] imagedata, String contentType,
      int largestDimension)
   {

      byte[] thumbnail = null;

      try
      {

         double scale;
         int sizeDifference, originalImageLargestDim;
         Image inImage = null;

         if (IMG_CONTENT_TYPES.contains(contentType))
         {

            inImage = Toolkit.getDefaultToolkit().createImage(imagedata);

         }
         else if (PDF_CONTENT_TYPES.contains(contentType))
         {

            URL url = this.getIconURL("adobepdf.png");

            if (url != null)
            {

               System.out.print("PDF Icon is: " + url.toString());
               inImage = Toolkit.getDefaultToolkit().getImage(url);

            }

         }
         else if (WORD_CONTENT_TYPES.contains(contentType))
         {

            URL url = this.getIconURL("officeword.png");

            if (url != null)
            {

               System.out.print("WORD Icon is: " + url.toString());
               inImage = Toolkit.getDefaultToolkit().getImage(url);

            }

         }
         else if (EXCEL_CONTENT_TYPES.contains(contentType))
         {

            URL url = this.getIconURL("officeexcel.png");

            if (url != null)
            {

               System.out.print("EXCEL Icon is: " + url.toString());
               inImage = Toolkit.getDefaultToolkit().getImage(url);

            }

         }
         else if (POWERPOINT_CONTENT_TYPES.contains(contentType))
         {

            URL url = this.getIconURL("officepowerpoint.png");

            if (url != null)
            {

               System.out.print("POWERPOINT Icon is: " + url.toString());
               inImage = Toolkit.getDefaultToolkit().getImage(url);

            }

         }
         else
         {

            URL url = this.getIconURL("officeoutlook.png");

            if (url != null)
            {

               System.out.print("UNKNOWN MIME Icon is: " + url.toString());
               inImage = Toolkit.getDefaultToolkit().getImage(url);

            }

         }

         if (inImage != null)
         {

            // Wait for the image to be loaded.
            MediaTracker mediaTracker = new MediaTracker(new Container());
            mediaTracker.addImage(inImage, 0);
            mediaTracker.waitForID(0);

            // Start the processing
            int inImageWidth = inImage.getWidth(null);
            int inImageHeight = inImage.getHeight(null);

            if ((inImageWidth == -1) || (inImageHeight == -1))
            {

               thumbnail = null;

            }
            else
            {

               //find biggest dimension
               if (inImageWidth > inImageHeight)
               {

                  scale = (double)largestDimension / (double)inImageWidth;
                  sizeDifference = inImageWidth - largestDimension;
                  originalImageLargestDim = inImageWidth;

               }
               else
               {

                  scale = (double)largestDimension / (double)inImageHeight;
                  sizeDifference = inImageHeight - largestDimension;
                  originalImageLargestDim = inImageHeight;

               }

               //create an image buffer to draw to
               BufferedImage outImage =
                  new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB); //arbitrary init so code compiles
               Graphics2D g2d;
               AffineTransform tx;

               //only scale if desired size is smaller than original
               int numSteps = sizeDifference / 100;

               if ((numSteps != 0) && (scale < 1.0d))
               {

                  int stepSize = sizeDifference / numSteps;
                  int stepWeight = stepSize / 2;
                  int heavierStepSize = stepSize + stepWeight;
                  int lighterStepSize = stepSize - stepWeight;
                  int currentStepSize, centerStep;
                  double scaledW = inImageWidth;
                  double scaledH = inImageHeight;

                  if ((numSteps % 2) == 1) //if there's an odd number of steps
                  {

                     centerStep = (int)Math.ceil((double)numSteps / 2d); //find the center step

                  }
                  else
                  {

                     centerStep = -1; //set it to -1 so it's ignored later

                  }

                  Integer intermediateSize = originalImageLargestDim,
                     previousIntermediateSize = originalImageLargestDim;

                  for (Integer i = 0; i < numSteps; i++)
                  {

                     if ((i + 1) != centerStep) //if this isn't the center step
                     {

                        if (i == (numSteps - 1)) //if this is the last step
                        {

                           //fix the stepsize to account for decimal place errors previously
                           currentStepSize =
                              previousIntermediateSize - largestDimension;

                        }
                        else
                        {

                           if ((numSteps - i) > (numSteps / 2)) //if we're in the first half of the reductions
                           {

                              currentStepSize = heavierStepSize;

                           }
                           else
                           {

                              currentStepSize = lighterStepSize;

                           }

                        }

                     }
                     else //center step, use natural step size
                     {

                        currentStepSize = stepSize;

                     }

                     intermediateSize =
                        previousIntermediateSize - currentStepSize;
                     scale =
                        (double)intermediateSize
                        / (double)previousIntermediateSize;
                     scaledW = (int)scaledW * scale;
                     scaledH = (int)scaledH * scale;
                     outImage =
                        new BufferedImage((int)scaledW, (int)scaledH,
                           BufferedImage.TYPE_INT_RGB);
                     g2d = outImage.createGraphics();
                     g2d.setBackground(Color.WHITE);
                     g2d.clearRect(0, 0, outImage.getWidth(),
                        outImage.getHeight());
                     g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                     tx = new AffineTransform();
                     tx.scale(scale, scale);
                     g2d.drawImage(inImage, tx, null);
                     g2d.dispose();
                     inImage = new ImageIcon(outImage).getImage();
                     previousIntermediateSize = intermediateSize;

                  }

               }
               else
               {

                  //just copy the original
                  outImage =
                     new BufferedImage(inImageWidth, inImageHeight,
                        BufferedImage.TYPE_INT_RGB);
                  g2d = outImage.createGraphics();
                  g2d.setBackground(Color.WHITE);
                  g2d.clearRect(0, 0, outImage.getWidth(),
                     outImage.getHeight());
                  tx = new AffineTransform();
                  tx.setToIdentity(); //use identity matrix so image is copied exactly
                  g2d.drawImage(inImage, tx, null);
                  g2d.dispose();

               }

               //JPEG-encode the image and write to file.
               ByteArrayOutputStream os = new ByteArrayOutputStream();
               JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
               encoder.encode(outImage);
               thumbnail = os.toByteArray();
               os.close();

            }

         }

      }
      catch (Exception ex)
      {

         // log.error("Exception during conversion",ex);
         ex.printStackTrace();
         thumbnail = null;

      }

      return thumbnail;

   }

   public byte[] createThumbnail2(byte[] imagedata, int thumbWidth,
      int thumbHeight, int quality)
   {

      byte[] thumbnail = null;

      try
      {

         // load image from filename
         Image inImage = Toolkit.getDefaultToolkit().createImage(imagedata);

         // Wait for the image to be loaded.
         MediaTracker mediaTracker = new MediaTracker(new Container());
         mediaTracker.addImage(inImage, 0);
         mediaTracker.waitForID(0);

         // Start the processing
         int inImageWidth = inImage.getWidth(null);
         int inImageHeight = inImage.getHeight(null);

         // determine thumbnail size from WIDTH and HEIGHT
         double thumbRatio = (double)thumbWidth / (double)thumbHeight;
         double imageRatio = (double)inImageWidth / (double)inImageHeight;

         if (thumbRatio < imageRatio)
         {

            thumbHeight = (int)(thumbWidth / imageRatio);

         }
         else
         {

            thumbWidth = (int)(thumbHeight * imageRatio);

         }

         // draw original image to thumbnail image object and
         // scale it to the new size on-the-fly
         BufferedImage outImage =
            new BufferedImage(thumbWidth, thumbHeight,
               BufferedImage.TYPE_INT_RGB);
         Graphics2D graphics2D = outImage.createGraphics();
         graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         graphics2D.drawImage(inImage, 0, 0, thumbWidth, thumbHeight, null);

         ByteArrayOutputStream out = new ByteArrayOutputStream();
         JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
         JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(outImage);
         quality = Math.max(0, Math.min(quality, 100));
         param.setQuality((float)quality / 100.0f, false);
         encoder.setJPEGEncodeParam(param);
         encoder.encode(outImage);
         thumbnail = out.toByteArray();
         out.close();

      }
      catch (Exception ex)
      {

         ex.printStackTrace();
         thumbnail = null;

      }

      return thumbnail;

   }

   public URL getIconURL(String name)
   {

      URL url =
         Thread.currentThread().getContextClassLoader().getResource(name);

      if (url == null)
      {

         url =
            Thread.currentThread().getContextClassLoader().getResource(
               "/img/icons/" + name);

      }

      return url;

   }

}