/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.om;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.om.OMBundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Eike Stepper
 */
public abstract class OSGiActivator implements BundleActivator
{
  public OSGiActivator()
  {
  }

  public final void start(BundleContext context) throws Exception
  {
    try
    {
      getOMBundle().setBundleContext(context);
      OM.Activator.traceStart(context);
      start();
    }
    catch (Error error)
    {
      getOMBundle().logger().error(error);
      throw error;
    }
    catch (Exception ex)
    {
      getOMBundle().logger().error(ex);
      throw ex;
    }
  }

  public final void stop(BundleContext context) throws Exception
  {
    try
    {
      OM.Activator.traceStop(context);
      stop();
      getOMBundle().setBundleContext(null);
    }
    catch (Error error)
    {
      getOMBundle().logger().error(error);
      throw error;
    }
    catch (Exception ex)
    {
      getOMBundle().logger().error(ex);
      throw ex;
    }
  }

  protected void start() throws Exception
  {
  }

  protected void stop() throws Exception
  {
  }

  protected abstract OMBundle getOMBundle();

  @Override
  protected final Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  @Override
  public final boolean equals(Object obj)
  {
    return super.equals(obj);
  }

  @Override
  protected final void finalize() throws Throwable
  {
    super.finalize();
  }

  @Override
  public final int hashCode()
  {
    return super.hashCode();
  }

  @Override
  public final String toString()
  {
    return super.toString();
  }
}
