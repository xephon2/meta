/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.meta.tools.buildsystem;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.ENewLineMode;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.CMeta;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

public final class MainUpdateREADMEFooter extends AbstractProjectMain
{
  private static final String GIT_COMMIT_FOOTER_CHANGE_CMD = "git-commit-footer-change.cmd";
  private static final Charset README_CHARSET = StandardCharsets.UTF_8;
  private static final String SEPARATOR = "\n" + "---\n";
  // Length is approx. 180 chars
  public static final String COMMON_FOOTER = SEPARATOR +
                                             "\n" +
                                             "My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodeingStyleguide.md) |\n" +
                                             "On Twitter: <a href=\"https://twitter.com/philiphelger\">@philiphelger</a>\n";

  public static void main (final String [] args)
  {
    final StringBuilder aSB = new StringBuilder ();

    for (final IProject aProject : ProjectList.getAllProjects (p -> p.isBuildInProject () &&
                                                                    p.getBaseDir ().exists () &&
                                                                    !p.isDeprecated () &&
                                                                    !p.isNestedProject ()))
    {
      if (false)
        _info (aProject, "Checking");
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
          String sNewContent = null;
          if (nIndex < 0)
          {
            _warn (aProject, "footer is missing");
            sNewContent = sContent + COMMON_FOOTER;
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
              sNewContent = sContent.substring (0, nIndex) + COMMON_FOOTER;
            }
          }

          if (sNewContent != null)
          {
            // Convert newline back to system default
            sNewContent = StringHelper.replaceAll (sNewContent, "\n", ENewLineMode.DEFAULT.getText ());

            SimpleFileIO.writeFile (f, sNewContent, README_CHARSET);
            _warn (aProject, f.getName () + " was updated");

            aSB.append ("echo ")
               .append (aProject.getProjectName ())
               .append ("\ncd ")
               .append (aProject.getFullBaseDirName ())
               .append ("\n")
               .append ("git commit -m \"Updated README footer\" README.md\n")
               .append ("git push\n")
               .append ("\nif errorlevel 1 goto error")
               .append ("\ncd ..\n");
          }
        }
      }
      else
        _warn (aProject, f.getName () + " is missing");
    }

    if (aSB.length () > 0)

    {
      aSB.insert (0, BATCH_HEADER);
      // Delete yourself
      // Source:
      // http://stackoverflow.com/questions/20329355/how-to-make-a-batch-file-delete-itself
      aSB.append ("(goto) 2>nul & del \"%~f0\"\n");
      aSB.append (BATCH_FOOTER);
      SimpleFileIO.writeFile (new File (CMeta.GIT_BASE_DIR, GIT_COMMIT_FOOTER_CHANGE_CMD),
                              aSB.toString (),
                              BATCH_CHARSET);
      s_aLogger.info ("Batch file " + GIT_COMMIT_FOOTER_CHANGE_CMD + " written");
    }

    s_aLogger.info ("Done");
  }
}
