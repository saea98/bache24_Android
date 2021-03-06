package com.cmi.bache24.util;

//import org.apache.sanselan.ImageReadException;
//import org.apache.sanselan.ImageWriteException;
//import org.apache.sanselan.Sanselan;
//import org.apache.sanselan.common.IImageMetadata;
//import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
//import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
//import org.apache.sanselan.formats.tiff.TiffImageMetadata;
//import org.apache.sanselan.formats.tiff.constants.TagInfo;
//import org.apache.sanselan.formats.tiff.constants.TiffConstants;
//import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory;
//import org.apache.sanselan.formats.tiff.write.TiffOutputField;
//import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by omar on 4/9/16.
 */
public class ExifUtils {

    /*
    public static boolean copyExifData(File sourceFile, File destFile, List<TagInfo> excludedFields)
    {
        String tempFileName = destFile.getAbsolutePath() + ".tmp";
        File tempFile = null;
        OutputStream tempStream = null;

        try
        {
            tempFile = new File (tempFileName);

            TiffOutputSet sourceSet = getSanselanOutputSet(sourceFile, TiffConstants.DEFAULT_TIFF_BYTE_ORDER);
            TiffOutputSet destSet = getSanselanOutputSet(destFile, sourceSet.byteOrder);

            // If the EXIF data endianess of the source and destination files
            // differ then fail. This only happens if the source and
            // destination images were created on different devices. It's
            // technically possible to copy this data by changing the byte
            // order of the data, but handling this case is outside the scope
            // of this implementation
            if (sourceSet.byteOrder != destSet.byteOrder) return false;

            destSet.getOrCreateExifDirectory();

            // Go through the source directories
            List<?> sourceDirectories = sourceSet.getDirectories();
            for (int i=0; i<sourceDirectories.size(); i++)
            {
                TiffOutputDirectory sourceDirectory = (TiffOutputDirectory)sourceDirectories.get(i);
                TiffOutputDirectory destinationDirectory = getOrCreateExifDirectory(destSet, sourceDirectory);

                if (destinationDirectory == null) continue; // failed to create

                // Loop the fields
                List<?> sourceFields = sourceDirectory.getFields();
                for (int j=0; j<sourceFields.size(); j++)
                {
                    // Get the source field
                    TiffOutputField sourceField = (TiffOutputField) sourceFields.get(j);

                    // Check exclusion list
                    if (excludedFields != null && excludedFields.contains(sourceField.tagInfo))
                    {
                        destinationDirectory.removeField(sourceField.tagInfo);
                        continue;
                    }

                    // Remove any existing field
                    destinationDirectory.removeField(sourceField.tagInfo);

                    // Add field
                    destinationDirectory.add(sourceField);
                }
            }

            // Save data to destination
            tempStream = new BufferedOutputStream(new FileOutputStream(tempFile));
            new ExifRewriter().updateExifMetadataLossless(destFile, tempStream, destSet);
            tempStream.close();

            // Replace file
            if (destFile.delete())
            {
                tempFile.renameTo(destFile);
            }

            return true;
        }
        catch (ImageReadException exception)
        {
            exception.printStackTrace();
        }
        catch (ImageWriteException exception)
        {
            exception.printStackTrace();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            if (tempStream != null)
            {
                try
                {
                    tempStream.close();
                }
                catch (IOException e)
                {
                }
            }

            if (tempFile != null)
            {
                if (tempFile.exists()) tempFile.delete();
            }
        }

        return false;
    }

    private static TiffOutputSet getSanselanOutputSet(File jpegImageFile, int defaultByteOrder)
            throws IOException, ImageReadException, ImageWriteException
    {
        TiffImageMetadata exif = null;
        TiffOutputSet outputSet = null;

        IImageMetadata metadata = Sanselan.getMetadata(jpegImageFile);
        JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
        if (jpegMetadata != null)
        {
            exif = jpegMetadata.getExif();

            if (exif != null)
            {
                outputSet = exif.getOutputSet();
            }
        }

        // If JPEG file contains no EXIF metadata, create an empty set
        // of EXIF metadata. Otherwise, use existing EXIF metadata to
        // keep all other existing tags
        if (outputSet == null)
            outputSet = new TiffOutputSet(exif==null?defaultByteOrder:exif.contents.header.byteOrder);

        return outputSet;
    }

    private static TiffOutputDirectory getOrCreateExifDirectory(TiffOutputSet outputSet, TiffOutputDirectory outputDirectory)
    {
        TiffOutputDirectory result = outputSet.findDirectory(outputDirectory.type);
        if (result != null)
            return result;
        result = new TiffOutputDirectory(outputDirectory.type);
        try
        {
            outputSet.addDirectory(result);
        }
        catch (ImageWriteException e)
        {
            return null;
        }
        return result;
    }
    */
}
