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
package com.helger.meta.project;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.version.Version;
import com.helger.meta.CMeta;

/**
 * Defines all the deprecated projects.
 *
 * @author Philip Helger
 */
public enum EProjectDeprecated implements IProject
{
  CIPA_START_JMS_API (null, "cipa-start-jms-api", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.5.0"),
  CIPA_START_JMSRECEIVER (null,
                          "cipa-start-jmsreceiver",
                          EProjectType.JAVA_LIBRARY,
                          EHasPages.FALSE,
                          EHasWiki.FALSE,
                          "1.0.2"),
  CIPA_START_JMSSENDER (null,
                        "cipa-start-jmssender",
                        EProjectType.JAVA_WEB_APPLICATION,
                        EHasPages.FALSE,
                        EHasWiki.FALSE,
                        "1.0.2"),
  JGATSP (null, "jgatsp", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, null),

  PEPPOL_LIME_PARENT_POM ("peppol-lime-parent-pom", "peppol-lime", EHasPages.FALSE, EHasWiki.FALSE, "3.0.1", EJDK.JDK6),
  PEPPOL_LIME_API (PEPPOL_LIME_PARENT_POM, "peppol-lime-api", EProjectType.JAVA_LIBRARY),
  PEPPOL_LIME_CLIENT (PEPPOL_LIME_PARENT_POM, "peppol-lime-client", EProjectType.JAVA_LIBRARY),
  PEPPOL_LIME_SERVER (PEPPOL_LIME_PARENT_POM, "peppol-lime-server", EProjectType.JAVA_WEB_APPLICATION),

  PH_BOOTSTRAP3 (null, "ph-bootstrap3", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.2"),
  PH_JDK5 (null, "ph-jdk5", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "1.1.0"),
  PH_JMS (null, "ph-jms", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "2.0.0"),
  PH_SCOPES (null, "ph-scopes", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "6.6.0"),
  PH_TINYMCE4 (null, "ph-tinymce4", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "0.3.5"),
  PH_WEBAPP_DEMO (null, "ph-webapp-demo", EProjectType.JAVA_WEB_APPLICATION, EHasPages.FALSE, EHasWiki.FALSE, null),
  PH_WEBBASICS (null, "ph-webbasics", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.0.2"),
  PH_WEBCTRLS (null, "ph-webctrls", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "4.0.2"),
  PH_WEBSCOPES (null, "ph-webscopes", EProjectType.JAVA_LIBRARY, EHasPages.FALSE, EHasWiki.FALSE, "6.8.1"),
  PH_PDF_LAYOUT (null,
                 "ph-pdf-layout",
                 "ph-pdf-layout",
                 EProjectType.JAVA_LIBRARY,
                 EHasPages.FALSE,
                 EHasWiki.FALSE,
                 "3.5.2",
                 EJDK.JDK8),

  PH_HTML_PARENT_POM ("ph-html-parent-pom", "ph-html", EHasPages.FALSE, EHasWiki.FALSE, "6.0.4", EJDK.JDK8),
  PH_HTML (PH_HTML_PARENT_POM, "ph-html", EProjectType.JAVA_LIBRARY),
  PH_HTML_JSCODE (PH_HTML_PARENT_POM, "ph-html-jscode", EProjectType.JAVA_LIBRARY),
  PH_HTML_JQUERY (PH_HTML_PARENT_POM, "ph-html-jquery", EProjectType.JAVA_LIBRARY),

  PH_LOCALES_PARENT_POM ("ph-locales-parent-pom", "ph-locales", EHasPages.FALSE, EHasWiki.FALSE, "2.0.0", EJDK.JDK6),
  PH_LOCALES (PH_LOCALES_PARENT_POM, "ph-locales", EProjectType.JAVA_LIBRARY),
  PH_LOCALES16 (PH_LOCALES_PARENT_POM, "ph-locales16", EProjectType.JAVA_LIBRARY),

  PH_DEE_PARENT_POM ("ph-dee-parent-pom", "ph-dee", EHasPages.FALSE, EHasWiki.FALSE, null, EJDK.JDK8),
  PH_DEE_API (PH_DEE_PARENT_POM, "ph-dee-api", EProjectType.JAVA_LIBRARY),
  PH_DEE_ENGINE (PH_DEE_PARENT_POM, "ph-dee-engine", EProjectType.JAVA_LIBRARY),

  PH_WSDL_GEN (null,
               "ph-wsdl-gen",
               "ph-wsdl-gen",
               EProjectType.JAVA_LIBRARY,
               EHasPages.FALSE,
               EHasWiki.FALSE,
               null,
               EJDK.JDK8),
  PH_ZEROMQ (null,
             "ph-zeromq",
             "ph-zeromq",
             EProjectType.JAVA_LIBRARY,
             EHasPages.FALSE,
             EHasWiki.FALSE,
             null,
             EJDK.JDK8);

  private final SimpleProject m_aProject;

  /**
   * Constructor for parent poms
   *
   * @param eParentProject
   * @param sProjectName
   *        Project name as stated in the POM
   * @param sProjectBaseDirName
   *        Project base directory name
   * @param eHasPagesProject
   *        pages project present?
   * @param eHasWikiProject
   *        wiki project present?
   * @param sLastPublishedVersion
   *        Last published version or <code>null</code>
   * @param eMinJDK
   *        Minimum JDK version to use
   */
  private EProjectDeprecated (@Nonnull @Nonempty final String sProjectName,
                              @Nonnull @Nonempty final String sProjectBaseDirName,
                              @Nonnull final EHasPages eHasPagesProject,
                              @Nonnull final EHasWiki eHasWikiProject,
                              @Nullable final String sLastPublishedVersion,
                              @Nonnull final EJDK eMinJDK)
  {
    this (null,
          sProjectName,
          sProjectBaseDirName,
          EProjectType.MAVEN_POM,
          eHasPagesProject,
          eHasWikiProject,
          sLastPublishedVersion,
          eMinJDK);
  }

  /**
   * Constructor for child projects where project name equals directory name and
   * the last published version is identical to the one of the parent project
   *
   * @param eParentProject
   *        Parent project. May not be <code>null</code>.
   * @param sProjectName
   *        Project name
   * @param eProjectType
   *        Project type
   */
  private EProjectDeprecated (@Nonnull final EProjectDeprecated eParentProject,
                              @Nonnull @Nonempty final String sProjectName,
                              @Nonnull final EProjectType eProjectType)
  {
    // Project name equals project base directory name
    this (eParentProject,
          sProjectName,
          sProjectName,
          eProjectType,
          EHasPages.FALSE,
          EHasWiki.FALSE,
          eParentProject.getLastPublishedVersionString (),
          EJDK.JDK6);
  }

  private EProjectDeprecated (@Nullable final EProjectDeprecated eParentProject,
                              @Nonnull @Nonempty final String sProjectName,
                              @Nonnull final EProjectType eProjectType,
                              @Nonnull final EHasPages eHasPagesProject,
                              @Nonnull final EHasWiki eHasWikiProject,
                              @Nullable final String sLastPublishedVersion)
  {
    // Project name equals project base directory name
    this (eParentProject,
          sProjectName,
          sProjectName,
          eProjectType,
          eHasPagesProject,
          eHasWikiProject,
          sLastPublishedVersion,
          EJDK.JDK6);
  }

  private EProjectDeprecated (@Nullable final EProjectDeprecated eParentProject,
                              @Nonnull @Nonempty final String sProjectName,
                              @Nonnull @Nonempty final String sProjectBaseDirName,
                              @Nonnull final EProjectType eProjectType,
                              @Nonnull final EHasPages eHasPagesProject,
                              @Nonnull final EHasWiki eHasWikiProject,
                              @Nullable final String sLastPublishedVersion,
                              @Nonnull final EJDK eMinJDK)
  {
    m_aProject = new SimpleProject (eParentProject,
                                    sProjectName,
                                    eProjectType,
                                    new File (eParentProject != null ? eParentProject.getBaseDir ()
                                                                     : CMeta.GIT_BASE_DIR,
                                              sProjectBaseDirName),
                                    EIsDeprecated.TRUE,
                                    eHasPagesProject,
                                    eHasWikiProject,
                                    sLastPublishedVersion,
                                    eMinJDK);
  }

  public boolean isBuildInProject ()
  {
    return true;
  }

  @Nullable
  public IProject getParentProject ()
  {
    return m_aProject.getParentProject ();
  }

  @Nonnull
  @Nonempty
  public String getProjectName ()
  {
    return m_aProject.getProjectName ();
  }

  @Nonnull
  public EProjectType getProjectType ()
  {
    return m_aProject.getProjectType ();
  }

  @Nonnull
  public File getBaseDir ()
  {
    return m_aProject.getBaseDir ();
  }

  @Nonnull
  @Nonempty
  public String getFullBaseDirName ()
  {
    return m_aProject.getFullBaseDirName ();
  }

  @Nonnull
  public EJDK getMinimumJDKVersion ()
  {
    return m_aProject.getMinimumJDKVersion ();
  }

  @Nonnull
  @Nonempty
  public String getMavenGroupID ()
  {
    return m_aProject.getMavenGroupID ();
  }

  @Nonnull
  @Nonempty
  public String getMavenArtifactID ()
  {
    return m_aProject.getMavenArtifactID ();
  }

  public boolean isDeprecated ()
  {
    return m_aProject.isDeprecated ();
  }

  public boolean hasPagesProject ()
  {
    return m_aProject.hasPagesProject ();
  }

  public boolean hasWikiProject ()
  {
    return m_aProject.hasWikiProject ();
  }

  public String getLastPublishedVersionString ()
  {
    return m_aProject.getLastPublishedVersionString ();
  }

  @Nullable
  public Version getLastPublishedVersion ()
  {
    return m_aProject.getLastPublishedVersion ();
  }

  public int compareTo (@Nonnull final IProject aProject)
  {
    return m_aProject.compareTo (aProject);
  }
}
