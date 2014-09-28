/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.meta.buildsystem;

import java.io.File;

import javax.annotation.Nonnull;

import com.helger.meta.EProject;
import com.helger.meta.EProjectType;

/**
 * Check whether all project has all the required files
 *
 * @author Philip Helger
 */
public final class MainCheckProjectRequiredFiles extends AbstractMainUtils
{
  private static void _checkFileExisting (@Nonnull final EProject eProject, @Nonnull final String sRelativeFilename)
  {
    final File f = new File (eProject.getBaseDir (), sRelativeFilename);
    if (!f.exists ())
      _warn (eProject, "File " + f.getAbsolutePath () + " does not exist!");
  }

  private static void _checkFileNotExisting (@Nonnull final EProject eProject, @Nonnull final String sRelativeFilename)
  {
    final File f = new File (eProject.getBaseDir (), sRelativeFilename);
    if (f.exists ())
      _warn (eProject, "File " + f.getAbsolutePath () + " should not exist!");
  }

  private static void _validateProject (@Nonnull final EProject eProject)
  {
    _checkFileExisting (eProject, ".classpath");
    _checkFileExisting (eProject, ".project");
    _checkFileExisting (eProject, "pom.xml");
    _checkFileExisting (eProject, "README.MD");
    _checkFileExisting (eProject, "findbugs-exclude.xml");
    _checkFileExisting (eProject, "src/etc/javadoc.css");
    _checkFileExisting (eProject, "src/etc/license-template.txt");
    _checkFileExisting (eProject, "src/main/resources/LICENSE");
    _checkFileExisting (eProject, "src/main/resources/NOTICE");
    _checkFileNotExisting (eProject, "LICENSE");
  }

  public static void main (final String [] args)
  {
    for (final EProject e : EProject.values ())
      if (e.getProjectType () != EProjectType.MAVEN_POM)
      {
        _validateProject (e);
      }
    s_aLogger.info ("Done - " + getWarnCount () + " warning(s)");
  }
}