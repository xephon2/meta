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
package com.helger.meta.tools.wsdlgen;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.resource.FileSystemResource;
import com.helger.meta.tools.wsdlgen.model.WGInterface;

public final class InterfaceReaderTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (InterfaceReaderTest.class);

  public static final String [] FILENAMES = new String [] { "wsdlgen/example-complex.interface",
                                                            "wsdlgen/example-currencyConvert.interface",
                                                            "wsdlgen/erb/erb-in-invoice-200.interface",
                                                            "wsdlgen/erb/erb-in-test-order-102.interface",
                                                            "wsdlgen/erb/erb-out-invoice-callback-100.interface",
                                                            "wsdlgen/erb/erc-in-async-status-110.interface",
                                                            "wsdlgen/erb/erc-in-status-update-100.interface",
                                                            "wsdlgen/erb/erc-out-async-110.interface",
                                                            "wsdlgen/erb/erc-out-bbg-100.interface",
                                                            "wsdlgen/erb/erc-out-sync-120.interface",
                                                            "wsdlgen/erb/erg-in-status-update-100.interface",
                                                            "wsdlgen/erb/in-progress/erc-out-sync-120.interface",
                                                            "wsdlgen/pp/pp-dvs.interface" };

  @Test
  public void testReadAll ()
  {
    for (final String sFilename : FILENAMES)
    {
      s_aLogger.info (sFilename);
      final File aFile = new File ("src/test/resources", sFilename);
      final WGInterface aInterface = InterfaceReader.readInterface (new FileSystemResource (aFile));
      assertNotNull (sFilename, aInterface);
    }
  }
}
