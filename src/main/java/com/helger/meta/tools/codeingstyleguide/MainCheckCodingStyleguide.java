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
package com.helger.meta.tools.codeingstyleguide;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.helger.commons.annotations.CodingStyleguideUnaware;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.OverrideOnDemand;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.commons.io.file.iterate.FileSystemRecursiveIterator;
import com.helger.commons.lang.CGStringHelper;
import com.helger.commons.state.EContinue;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.meta.AbstractProjectMain;
import com.helger.meta.asm.ASMUtils;
import com.helger.meta.project.EProject;
import com.helger.meta.project.EProjectType;
import com.helger.meta.project.IProject;
import com.helger.meta.project.ProjectList;

public final class MainCheckCodingStyleguide extends AbstractProjectMain
{
  private static final Locale LOCALE_SYSTEM = Locale.US;

  private static void _checkMainClass (@Nonnull final IProject aProject, @Nonnull final ClassNode cn)
  {
    final String sClassLocalName = CGStringHelper.getClassLocalName (CGStringHelper.getClassFromPath (cn.name));
    final boolean bIsSpecialCase = sClassLocalName.equals ("package-info");
    if (bIsSpecialCase)
      return;

    String sInnerClassLocalName = StringHelper.getFromLastExcl (sClassLocalName, '$');
    if (sInnerClassLocalName == null)
      sInnerClassLocalName = sClassLocalName;
    else
      if (StringParser.isUnsignedInt (sInnerClassLocalName))
      {
        // It's an anonymous inner class - use the full name
        sInnerClassLocalName = sClassLocalName;
      }

    final String sPrefix = "[" + sClassLocalName + "] ";
    final boolean bClassIsAbstract = Modifier.isAbstract (cn.access);
    final boolean bClassIsAnnotation = (cn.access & Opcodes.ACC_ANNOTATION) != 0;
    final boolean bClassIsEnum = (cn.access & Opcodes.ACC_ENUM) != 0;
    final boolean bClassIsInterface = Modifier.isInterface (cn.access);

    if (bClassIsInterface)
    {
      if (bClassIsAnnotation)
      {
        // TODO
      }
      else
      {
        if (sInnerClassLocalName.startsWith ("I"))
        {
          if (sInnerClassLocalName.length () > 1 && !Character.isUpperCase (sInnerClassLocalName.charAt (1)))
            _warn (aProject, sPrefix + "Interface names should have an upper case second letter");
        }
        else
          if (!sInnerClassLocalName.startsWith ("I") && !sClassLocalName.endsWith ("MBean"))
            _warn (aProject, sPrefix + "Interface names should start with an uppercase 'I'");
      }
    }
    else
    {
      if (bClassIsEnum)
      {
        if (!sInnerClassLocalName.startsWith ("E"))
          _warn (aProject, sPrefix + "enum classes should start with 'E'");
      }
      else
      {
        if (bClassIsAbstract)
        {
          if (!sInnerClassLocalName.startsWith ("Abstract") &&
              !sInnerClassLocalName.contains ("Singleton") &&
              !sInnerClassLocalName.equals ("NamespacePrefixMapper"))
            _warn (aProject, sPrefix + "Abstract classes should start with 'Abstract'");
        }
      }
    }
  }

  private static void _checkMainMethods (@Nonnull final IProject aProject, @Nonnull final ClassNode cn)
  {
    final String sClassLocalName = CGStringHelper.getClassLocalName (CGStringHelper.getClassFromPath (cn.name));
    final boolean bIsSpecialCase = false;
    if (bIsSpecialCase)
      return;

    final boolean bClassIsAbstract = Modifier.isAbstract (cn.access);
    final boolean bClassIsEnum = (cn.access & Opcodes.ACC_ENUM) != 0;
    final boolean bClassIsFinal = Modifier.isFinal (cn.access);
    final boolean bClassIsInterface = Modifier.isInterface (cn.access);

    final List <MethodNode> aAllCtors = new ArrayList <> ();
    for (final Object oMethod : cn.methods)
    {
      final MethodNode mn = (MethodNode) oMethod;

      if (ASMUtils.containsAnnotation (mn, CodingStyleguideUnaware.class))
        continue;

      final String sPrefix = "[" + sClassLocalName + "::" + mn.name + "] ";

      final boolean bIsConstructor = mn.name.equals ("<init>");
      final boolean bIsPrivate = Modifier.isPrivate (mn.access);
      final boolean bIsFinal = Modifier.isFinal (mn.access);

      if (bIsConstructor)
        aAllCtors.add (mn);

      if (bIsPrivate)
      {
        if (!bIsConstructor &&
            !mn.name.startsWith ("_") &&
            !mn.name.equals ("readObject") &&
            !mn.name.equals ("writeObject") &&
            !mn.name.equals ("readResolve") &&
            !mn.name.startsWith ("lambda$"))
          _warn (aProject, sPrefix + "Privat methods should start with an underscore");
      }

      if (bIsFinal)
      {
        if (bClassIsFinal)
          _warn (aProject, sPrefix + "final method in final class");

        if (ASMUtils.containsAnnotation (mn, OverrideOnDemand.class))
          _warn (aProject, sPrefix + "final method uses @OverrideOnDemand annotation");
      }
      else
      {
        if (bClassIsFinal && ASMUtils.containsAnnotation (mn, OverrideOnDemand.class))
          _warn (aProject, sPrefix + "final class uses @OverrideOnDemand annotation");
      }
    }

    if (bClassIsAbstract && !bClassIsInterface && !bClassIsEnum)
    {
      boolean bAnyNonPrivateCtor = false;
      for (final MethodNode aCtor : aAllCtors)
        if (!Modifier.isPrivate (aCtor.access))
        {
          bAnyNonPrivateCtor = true;
          break;
        }
      if (!bAnyNonPrivateCtor)
        _warn (aProject, "[" + sClassLocalName + "] The abstract class contains only private constructors!");
    }
  }

  private static void _checkMainVariables (@Nonnull final IProject aProject, @Nonnull final ClassNode cn)
  {
    final String sClassLocalName = CGStringHelper.getClassLocalName (CGStringHelper.getClassFromPath (cn.name));
    final boolean bIsSpecialCase = (aProject.getProjectType () == EProjectType.MAVEN_PLUGIN && sClassLocalName.endsWith ("Mojo"));
    if (bIsSpecialCase)
      return;

    final String sPrefix = "[" + sClassLocalName + "] ";
    final boolean bClassIsFinal = Modifier.isFinal (cn.access);

    for (final Object oField : cn.fields)
    {
      final FieldNode fn = (FieldNode) oField;

      if (ASMUtils.containsAnnotation (fn, CodingStyleguideUnaware.class))
        continue;

      final boolean bIsStatic = Modifier.isStatic (fn.access);
      final boolean bIsFinal = Modifier.isFinal (fn.access);
      final boolean bIsPrivate = Modifier.isPrivate (fn.access);

      if (bIsStatic)
      {
        // Internal variable names
        if (fn.name.startsWith ("$SWITCH_TABLE$") ||
            fn.name.equals ("$assertionsDisabled") ||
            fn.name.startsWith ("$SwitchMap$"))
          continue;

        if (bIsFinal)
        {
          if (!fn.name.startsWith ("s_") &&
              !fn.name.equals (fn.name.toUpperCase (LOCALE_SYSTEM)) &&
              !fn.name.equals ("serialVersionUID"))
            _warn (aProject, sPrefix + "Static final member name '" + fn.name + "' does not match naming conventions");
        }
        else
        {
          if (!fn.name.startsWith ("s_"))
            _warn (aProject, sPrefix + "Static member name '" + fn.name + "' does not match naming conventions");

          if (!bIsPrivate)
            _warn (aProject, sPrefix + "Static member '" + fn.name + "' is not private");
        }
      }
      else
      {
        if (fn.name.startsWith ("this$") || fn.name.startsWith ("val$"))
          continue;

        if (!fn.name.startsWith ("m_"))
          _warn (aProject, sPrefix + "Instance member name '" + fn.name + "' does not match");

        if (bClassIsFinal && !bIsPrivate)
          _warn (aProject, sPrefix + "Instance member '" + fn.name + "' is not private");
      }
    }
  }

  @Nonnull
  private static EContinue _doScanMainClass (@Nonnull final IProject aProject,
                                             @Nonnull final String sPackageName,
                                             @Nonnull @Nonempty final String sClassLocalName)
  {
    if (aProject == EProject.ERECHNUNG_WS_CLIENT &&
        (sPackageName.equals ("at.gv.brz.eproc.erb.ws.documentupload._20121205") ||
         sPackageName.equals ("at.gv.brz.eproc.erb.ws.invoicedelivery._201306") ||
         sPackageName.equals ("at.gv.brz.eproc.erb.ws.invoicedeliverycallback._201305") || sPackageName.equals ("at.gv.brz.schema.eproc.invoice_uploadstatus_1_0")))
      return EContinue.BREAK;

    if (aProject == EProject.JCODEMODEL)
      return EContinue.BREAK;

    if (aProject == EProject.PEPPOL_PRACTICAL && sPackageName.equals ("com.helger.peppol.wsclient"))
      return EContinue.BREAK;

    if (aProject == EProject.PH_CSS &&
        (sClassLocalName.equals ("CharStream") ||
         sClassLocalName.equals ("ParseException") ||
         sClassLocalName.startsWith ("ParserCSS21") ||
         sClassLocalName.startsWith ("ParserCSS30") ||
         sClassLocalName.startsWith ("ParserCSSCharsetDetector") ||
         sClassLocalName.equals ("Token") ||
         sClassLocalName.equals ("TokenMgrError") ||
         sClassLocalName.startsWith ("JJTParser") ||
         sClassLocalName.equals ("Node") ||
         (sClassLocalName.startsWith ("Parser") && sClassLocalName.endsWith ("Constants")) || sClassLocalName.equals ("SimpleNode")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_EBINTERFACE &&
        (sPackageName.startsWith ("com.helger.ebinterface.v30") ||
         sPackageName.startsWith ("com.helger.ebinterface.v40") ||
         sPackageName.startsWith ("com.helger.ebinterface.v41") || sPackageName.equals ("com.helger.ebinterface.xmldsig")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_GENERICODE &&
        (sPackageName.equals ("com.helger.cva.v10") || sPackageName.equals ("com.helger.genericode.v04") || sPackageName.equals ("com.helger.genericode.v10")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_JSON &&
        (sClassLocalName.equals ("CharStream") ||
         sClassLocalName.equals ("ParseException") ||
         sClassLocalName.startsWith ("ParserJson") ||
         sClassLocalName.equals ("Token") || sClassLocalName.equals ("TokenMgrError")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_SBDH && sPackageName.equals ("org.unece.cefact.namespaces.sbdh"))
      return EContinue.BREAK;

    if (aProject == EProject.PH_SCHEMATRON && sPackageName.equals ("org.oclc.purl.dsdl.svrl"))
      return EContinue.BREAK;

    if (aProject == EProject.PH_UBL20 &&
        (sPackageName.startsWith ("oasis.names.specification.ubl.schema.xsd.") ||
         sPackageName.startsWith ("un.unece.uncefact.codelist.specification.") || sPackageName.startsWith ("un.unece.uncefact.data.specification.")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_UBL21 &&
        (sPackageName.startsWith ("oasis.names.specification.ubl.schema.xsd.") ||
         sPackageName.startsWith ("org.etsi.uri.") ||
         sPackageName.equals ("org.w3._2000._09.xmldsig") || sPackageName.equals ("un.unece.uncefact.data.specification.corecomponenttypeschemamodule._21")))
      return EContinue.BREAK;

    if (aProject == EProject.PH_XPATH2 &&
        (sClassLocalName.equals ("CharStream") ||
         sClassLocalName.equals ("ParseException") ||
         sClassLocalName.startsWith ("ParserXP2") ||
         sClassLocalName.equals ("Node") ||
         sClassLocalName.equals ("Token") ||
         sClassLocalName.equals ("TokenMgrError") ||
         sClassLocalName.startsWith ("JJTParser") || sClassLocalName.equals ("SimpleNode")))
      return EContinue.BREAK;

    return EContinue.CONTINUE;
  }

  private static void _scanMainCode (@Nonnull final IProject aProject)
  {
    // Find all main class files
    final File aMainClasses = new File (aProject.getBaseDir (), "target/classes");
    for (final File aClassFile : new FileSystemRecursiveIterator (aMainClasses))
      if (aClassFile.isFile () && aClassFile.getName ().endsWith (".class"))
      {
        // Interpret byte code
        final ClassNode cn = ASMUtils.readClassFile (aClassFile);

        // Ignore classes explicitly marked as unaware
        if (ASMUtils.containsAnnotation (cn, CodingStyleguideUnaware.class))
          continue;

        final String sClassName = CGStringHelper.getClassFromPath (cn.name);
        final String sPackageName = CGStringHelper.getClassPackageName (sClassName);
        final String sClassLocalName = CGStringHelper.getClassLocalName (sClassName);

        // Special generated classes
        if (_doScanMainClass (aProject, sPackageName, sClassLocalName).isBreak ())
          continue;

        _checkMainClass (aProject, cn);
        _checkMainVariables (aProject, cn);
        _checkMainMethods (aProject, cn);
      }
  }

  private static void _checkTestClass (@Nonnull final IProject aProject,
                                       @Nullable final String sBaseName,
                                       @Nullable final String sTestClass)
  {
    if (sTestClass.endsWith ("Test") && !sTestClass.endsWith ("FuncTest"))
    {
      final String sMainClass = StringHelper.trimEnd (sTestClass, 4);
      final File aMainClass = new File (aProject.getBaseDir (), "target/classes/" + sMainClass + ".class");
      if (!aMainClass.exists ())
        _warn (aProject, "Test class " + sTestClass + " has no matching main class");
    }
    else
      if (sBaseName.startsWith ("FuncTest"))
      {
        _warn (aProject, "Test class " + sTestClass + " should end with FuncTest instead of starting with it");
      }
  }

  private static void _scanTestCode (@Nonnull final IProject aProject)
  {
    final File aTestClasses = new File (aProject.getBaseDir (), "target/test-classes");
    for (final File aClassFile : new FileSystemRecursiveIterator (aTestClasses))
      if (aClassFile.isFile () && aClassFile.getName ().endsWith (".class"))
      {
        final String sName = aClassFile.getName ();

        // Interpret byte code
        final ClassNode cn = ASMUtils.readClassFile (aClassFile);
        boolean bContainsTestMethod = false;
        for (final Object oMethod : cn.methods)
        {
          final MethodNode mn = (MethodNode) oMethod;
          if (ASMUtils.containsAnnotation (mn, "Lorg/junit/Test;"))
          {
            bContainsTestMethod = true;
            break;
          }
        }

        final String sBaseName = FilenameHelper.getWithoutExtension (sName);

        if (bContainsTestMethod && !sBaseName.endsWith ("Test"))
          _warn (aProject, "Class '" + sName + "' contains @Test annotation but is label weird");

        if (sBaseName.contains ("$") ||
            sBaseName.equals ("SPITest") ||
            sBaseName.equals ("JettyMonitor") ||
            sBaseName.startsWith ("JettyStop") ||
            sBaseName.startsWith ("RunInJetty") ||
            sBaseName.startsWith ("IMock") ||
            sBaseName.startsWith ("Mock") ||
            sBaseName.endsWith ("Mock") ||
            sBaseName.startsWith ("Benchmark") ||
            sBaseName.startsWith ("Issue") ||
            sBaseName.startsWith ("Main") ||
            sBaseName.endsWith ("TestRule"))
        {
          continue;
        }

        final String sTestClass = FilenameHelper.getWithoutExtension (FilenameHelper.getRelativeToParentDirectory (aClassFile,
                                                                                                                   aTestClasses));
        _checkTestClass (aProject, sBaseName, sTestClass);
      }
  }

  private static void _scanProject (@Nonnull final IProject aProject)
  {
    if (false)
      s_aLogger.info ("  " + aProject.getProjectName ());

    _scanMainCode (aProject);
    _scanTestCode (aProject);
  }

  public static void main (final String [] args)
  {
    s_aLogger.info ("Start checking coding style guide in .class files!");
    for (final IProject aProject : ProjectList.getAllProjects ())
      if (aProject.getProjectType ().hasJavaCode () &&
          aProject != EProject.PH_JAVACC_MAVEN_PLUGIN &&
          !aProject.isDeprecated ())
        _scanProject (aProject);
    s_aLogger.info ("Done - " + getWarnCount () + " warning(s) for " + ProjectList.size () + " projects");
  }
}
