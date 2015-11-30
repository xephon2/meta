/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

/**
 * Ensure the src/etc/javadoc.css file is the same in all projects
 *
 * @author Philip Helger
 */
public final class MainUpdateJavadocCSS extends AbstractProjectMain
{
  public static void main (final String [] args)
  {
    final String sSrcCSS = SimpleFileIO.getFileAsString (new File ("src/test/resources/source-javadoc.css"), CCharset.CHARSET_UTF_8_OBJ);

    for (final IProject aProject : ProjectList.getAllProjects ())
      if (aProject.isBuildInProject () && aProject.getBaseDir ().exists () && !aProject.isDeprecated () && aProject.getProjectType ().hasJavaCode ())
      {
        final File f = new File (aProject.getBaseDir (), "src/etc/javadoc.css");
        assert f.exists ();
        SimpleFileIO.writeFile (f, sSrcCSS, CCharset.CHARSET_UTF_8_OBJ);
      }
    s_aLogger.info ("Done - run mvn license:format on all projects");
  }
}