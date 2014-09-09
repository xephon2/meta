package com.helger.meta.cmdline;

import java.io.File;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.charset.CCharset;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.meta.CMeta;
import com.helger.meta.EProject;

/**
 * Create a set of batch files that contains content that in most cases is
 * relevant to all projects.
 *
 * @author Philip Helger
 */
public final class MainCreateBatchFiles
{
  private static final int PROJECT_COUNT = EProject.values ().length;
  private static final String HEADER = "@echo off\n" +
                                       "rem This files is generated - DO NOT EDIT\n" +
                                       "rem A total of " +
                                       PROJECT_COUNT +
                                       " projects are handled\n";
  private static final String FOOTER = "goto end\n"
                                       + ":error\n"
                                       + "echo An error occured!!!\n"
                                       + "pause\n"
                                       + "goto exit\n"
                                       + ":end\n"
                                       + "echo Successfully done\n"
                                       + ":exit\n";
  private static final Charset CHARSET_BATCH = CCharset.CHARSET_ISO_8859_1_OBJ;

  private static void _createBatchFile (@Nonnull @Nonempty final String sCommand,
                                        @Nonnull @Nonempty final String sBatchFileName)
  {
    final StringBuilder aSB = new StringBuilder ();
    aSB.append (HEADER);
    int nIndex = 1;
    for (final EProject e : EProject.values ())
    {
      aSB.append ("echo ")
         .append (e.getProjectName ())
         .append (" [")
         .append (nIndex)
         .append ("/")
         .append (PROJECT_COUNT)
         .append ("]\ncd ")
         .append (e.getProjectName ())
         .append ("\n")
         .append (sCommand)
         .append ("\nif errorlevel 1 goto error\ncd ..\n");
      ++nIndex;
    }
    aSB.append (FOOTER);
    SimpleFileIO.writeFile (new File (CMeta.GIT_BASE_DIR, sBatchFileName), aSB.toString (), CHARSET_BATCH);
  }

  private static void _createMvnBatchFile (@Nonnull @Nonempty final String sMavenCommand,
                                           @Nonnull @Nonempty final String sBatchFileName)
  {
    _createBatchFile ("call mvn " + sMavenCommand + " %*", sBatchFileName);
  }

  public static void main (final String [] args)
  {
    _createMvnBatchFile ("license:format", "mvn_license_format.cmd");
    _createMvnBatchFile ("dependency:go-offline dependency:sources", "mvn_dependency_go_offline_and_sources.cmd");
    _createMvnBatchFile ("clean", "mvn_clean.cmd");
    _createMvnBatchFile ("clean install", "mvn_clean_install.cmd");
    _createMvnBatchFile ("clean install sonar:sonar", "mvn_clean_install_sonar.cmd");
    _createBatchFile ("git pull", "git_pull.cmd");
    System.out.println ("Batch files created in " + CMeta.GIT_BASE_DIR);
  }
}
