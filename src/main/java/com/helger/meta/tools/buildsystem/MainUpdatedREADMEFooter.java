package com.helger.meta.tools.buildsystem;

import java.io.File;
import java.nio.charset.Charset;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.ENewLineMode;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

public final class MainUpdatedREADMEFooter extends AbstractProjectMain
{
  private static final Charset README_CHARSET = CCharset.CHARSET_UTF_8_OBJ;
  private static final String SEPARATOR = "\n" + "---\n";
  // Length is approx. 180 chars
  public static final String COMMON_FOOTER = SEPARATOR +
                                             "\n" +
                                             "My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodeingStyleguide.md) |\n" +
                                             "On Twitter: <a href=\"https://twitter.com/philiphelger\">@philiphelger</a>\n";

  public static void main (final String [] args)
  {
    for (final IProject aProject : ProjectList.getAllProjects ())
      if (aProject.isBuildInProject () &&
          aProject.getBaseDir ().exists () &&
          !aProject.isDeprecated () &&
          !aProject.isNestedProject ())
      {
        final File f = new File (aProject.getBaseDir (), "README.md");
        if (f.exists ())
        {
          final long nFileSize = f.length ();
          String sContent = SimpleFileIO.getFileAsString (f, README_CHARSET);

          // Unify newlines to "\n"
          sContent = StringHelper.replaceAll (sContent, ENewLineMode.DEFAULT.getText (), "\n");

          // Action to do?
          if (!sContent.contains (COMMON_FOOTER))
          {
            // Search for separator
            final int nIndex = sContent.lastIndexOf (SEPARATOR);
            if (nIndex < 0)
            {
              _warn (aProject, "footer is missing");
            }
            else
            {
              final double dPerc = nIndex * 100.0 / nFileSize;
              final double dThreshold = (nFileSize - 180 * 1.25) * 100 / nFileSize;
              if (dPerc < dThreshold)
              {
                _warn (aProject,
                       "footer too early at " +
                                 dPerc +
                                 "% (" +
                                 nIndex +
                                 " of " +
                                 nFileSize +
                                 "; threshold is " +
                                 dThreshold +
                                 "%). No action!");
              }
              else
              {
                String sNewContent = sContent.substring (0, nIndex) + COMMON_FOOTER;

                // Convert newline back to system default
                sNewContent = StringHelper.replaceAll (sNewContent, "\n", ENewLineMode.DEFAULT.getText ());

                SimpleFileIO.writeFile (f, sNewContent, README_CHARSET);
                _warn (aProject, f.getName () + " was updated");
              }
            }
          }
        }
        else
          _warn (aProject, f.getName () + " is missing");
      }
    s_aLogger.info ("Done");
  }
}