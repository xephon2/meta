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
package com.helger.meta.codeingstyleguide;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import com.helger.commons.annotations.CodingStyleguideUnaware;
import com.helger.commons.io.file.FileUtils;
import com.helger.commons.io.file.iterate.FileSystemRecursiveIterator;
import com.helger.commons.lang.CGStringHelper;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.EProject;
import com.helger.meta.EProjectType;
import com.helger.meta.asm.ASMUtils;

public final class MainCheckCodingStyleguide extends AbstractProjectMain
{
  private static final Locale LOCALE_SYSTEM = Locale.US;

  private static void _checkClass (@Nonnull final EProject eProject, @Nonnull final ClassNode cn)
  {
    final String sClassLocalName = CGStringHelper.getClassLocalName (CGStringHelper.getClassFromPath (cn.name));
    final boolean bIsSpecialCase = sClassLocalName.equals ("package-info") ||
                                   eProject == EProject.JCODEMODEL ||
                                   ((eProject == EProject.PH_CSS || eProject == EProject.PH_JSON) && (sClassLocalName.equals ("CharStream") ||
                                                                                                      sClassLocalName.equals ("Node") || (sClassLocalName.startsWith ("Parser") && sClassLocalName.endsWith ("Constants"))));
    if (bIsSpecialCase)
      return;

    final String sPrefix = "[" + sClassLocalName + "] ";
    final boolean bClassIsFinal = Modifier.isFinal (cn.access);
    final boolean bClassIsInterface = Modifier.isInterface (cn.access);
    final boolean bClassIsAnnotation = (cn.access & Opcodes.ACC_ANNOTATION) != 0;

    if (bClassIsInterface)
    {
      if (bClassIsAnnotation)
      {}
      else
      {
        if (!sClassLocalName.startsWith ("I") &&
            !sClassLocalName.contains ("$I") &&
            !sClassLocalName.endsWith ("MBean"))
          _warn (eProject, sPrefix + "Interface names should start with an uppercase 'I'");
      }
    }
  }

  private static void _checkVariables (@Nonnull final EProject eProject, @Nonnull final ClassNode cn)
  {
    final String sClassLocalName = CGStringHelper.getClassLocalName (CGStringHelper.getClassFromPath (cn.name));
    final boolean bIsSpecialCase = (eProject == EProject.PH_CSS && (sClassLocalName.equals ("ParseException") ||
                                                                    sClassLocalName.startsWith ("ParserCSS21") ||
                                                                    sClassLocalName.startsWith ("ParserCSS30") ||
                                                                    sClassLocalName.startsWith ("ParserCSSCharsetDetector") ||
                                                                    sClassLocalName.startsWith ("JJTParserCSS") ||
                                                                    sClassLocalName.equals ("SimpleNode") ||
                                                                    sClassLocalName.equals ("Token") || sClassLocalName.startsWith ("TokenMgrError"))) ||
                                   (eProject == EProject.PH_JSON && (sClassLocalName.equals ("ParseException") ||
                                                                     sClassLocalName.startsWith ("ParserJson") ||
                                                                     sClassLocalName.equals ("Token") || sClassLocalName.startsWith ("TokenMgrError"))) ||
                                   (eProject.getProjectType () == EProjectType.MAVEN_PLUGIN && sClassLocalName.endsWith ("Mojo"));
    if (bIsSpecialCase)
      return;

    final String sPrefix = "[" + sClassLocalName + "] ";
    final boolean bClassIsFinal = Modifier.isFinal (cn.access);

    for (final Object oField : cn.fields)
    {
      final FieldNode fn = (FieldNode) oField;

      final boolean bIsStatic = Modifier.isStatic (fn.access);
      final boolean bIsFinal = Modifier.isFinal (fn.access);
      final boolean bIsPrivate = Modifier.isPrivate (fn.access);

      if (bIsStatic)
      {
        // Internal variable names
        if (fn.name.startsWith ("$SWITCH_TABLE$") ||
            fn.name.equals ("$assertionsDisabled") ||
            ASMUtils.containsAnnotation (fn, CodingStyleguideUnaware.class))
          continue;

        if (bIsFinal)
        {
          if (!fn.name.startsWith ("s_") &&
              !fn.name.equals (fn.name.toUpperCase (LOCALE_SYSTEM)) &&
              !fn.name.equals ("serialVersionUID"))
            _warn (eProject, sPrefix + "Static final member '" + fn.name + "' does not match");
        }
        else
        {
          if (!fn.name.startsWith ("s_"))
            _warn (eProject, sPrefix + "Static member '" + fn.name + "' does not match");

          if (!bIsPrivate)
            _warn (eProject, sPrefix + "Static member '" + fn.name + "' is not private");
        }
      }
      else
      {
        if (eProject == EProject.JCODEMODEL || fn.name.startsWith ("this$") || fn.name.startsWith ("val$"))
          continue;

        if (!fn.name.startsWith ("m_"))
          _warn (eProject, sPrefix + "Instance member '" + fn.name + "' does not match");

        if (bClassIsFinal && !bIsPrivate)
          _warn (eProject, sPrefix + "Instance member '" + fn.name + "' is not private");
      }
    }
  }

  private static void _scanProject (@Nonnull final EProject eProject) throws IOException
  {
    if (false)
      s_aLogger.info ("  " + eProject.getProjectName ());
    final File aTargetDir = FileUtils.getCanonicalFile (new File (eProject.getBaseDir (), "target/classes"));

    // Find all class files
    for (final File aClassFile : new FileSystemRecursiveIterator (aTargetDir))
      if (aClassFile.isFile () && aClassFile.getName ().endsWith (".class"))
      {
        // Interpret byte code
        final ClassNode cn = ASMUtils.readClassFile (aClassFile);

        // Ignore classes explicitly marked as unaware
        if (ASMUtils.containsAnnotation (cn, CodingStyleguideUnaware.class))
          continue;

        _checkClass (eProject, cn);
        _checkVariables (eProject, cn);
      }
  }

  public static void main (final String [] args) throws IOException
  {
    s_aLogger.info ("Start checking coding style guide in .class files!");
    for (final EProject eProject : EProject.values ())
      if (eProject.getProjectType ().hasJavaCode () && eProject != EProject.PH_JAVACC_MAVEN_PLUGIN)
        _scanProject (eProject);
    s_aLogger.info ("Done - " + getWarnCount () + " warning(s)");
  }
}
