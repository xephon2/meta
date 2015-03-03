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
package com.helger.meta.project;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.version.Version;

public interface IProject
{
  /**
   * @return <code>true</code> if this project is part of {@link EProject}.
   */
  boolean isBuildInProject ();

  @Nonnull
  @Nonempty
  String getProjectName ();

  @Nonnull
  File getBaseDir ();

  @Nonnull
  File getPOMFile ();

  @Nonnull
  EProjectType getProjectType ();

  boolean isDeprecated ();

  /**
   * @return <code>true</code> if this project has the auto-generated
   *         <code>gh-pages</code> branch.
   */
  boolean hasPagesProject ();

  @Nonnull
  @Nonempty
  String getPagesProjectName ();

  /**
   * @return <code>true</code> if this project has a special Wiki project.
   */
  boolean hasWikiProject ();

  @Nonnull
  @Nonempty
  String getWikiProjectName ();

  /**
   * @return <code>true</code> if this project had at least one release,
   *         <code>false</code> if not.
   */
  boolean isPublished ();

  @Nullable
  String getLastPublishedVersionString ();

  @Nullable
  Version getLastPublishedVersion ();

  int compareTo (@Nonnull IProject aProject);
}