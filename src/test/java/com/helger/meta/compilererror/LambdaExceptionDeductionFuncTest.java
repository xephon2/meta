package com.helger.meta.compilererror;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * This is a dummy class to test for generic exceptions in combinations with
 * Lambdas
 *
 * @author Philip Helger
 */
public class LambdaExceptionDeductionFuncTest
{
  public static interface IBaseGetter
  {
    String get () throws Exception;
  }

  public static interface IThrowingGetter <EX extends Exception> extends IBaseGetter
  {
    String get () throws EX;
  }

  public static interface IThrowingGetterWorking <EX extends Exception>
  {
    String get () throws EX;
  }

  public static void main (final String [] args) throws IOException
  {
    final InputStream aIS = new ByteArrayInputStream ("abc".getBytes (StandardCharsets.UTF_8));
    // This fails when compiling on the commandline!
    final IThrowingGetter <IOException> aGetter = () -> Character.toString ((char) aIS.read ());
    aGetter.get ();
    final IThrowingGetterWorking <IOException> aGetterWorking = () -> Character.toString ((char) aIS.read ());
    aGetterWorking.get ();
  }
}
