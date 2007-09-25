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
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.protocol.CDOID;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.CDOAware;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.internal.cdo.CDOAdapterImpl;
import org.eclipse.emf.internal.cdo.CDOCallbackImpl;
import org.eclipse.emf.internal.cdo.CDOMetaImpl;
import org.eclipse.emf.internal.cdo.CDOViewImpl;
import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.bundle.OM;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public final class FSMUtil
{
  private FSMUtil()
  {
  }

  public static boolean isTransient(CDOObject object)
  {
    CDOState state = object.cdoState();
    return state == CDOState.TRANSIENT || state == CDOState.PREPARED_ATTACH;
  }

  /**
   * @param view
   *          Only needed if object is a meta instance.
   */
  public static InternalCDOObject adapt(Object object, CDOView view)
  {
    if (object == null)
    {
      throw new IllegalArgumentException("object == null");
    }

    if (object instanceof InternalCDOObject)
    {
      return (InternalCDOObject)object;
    }

    if (object instanceof EModelElement || object instanceof EGenericType
        || object instanceof EStringToStringMapEntryImpl)
    {
      InternalEObject eObject = (InternalEObject)object;
      if (view == null)
      {
        throw new IllegalArgumentException("view == null");
      }

      if (eObject.eIsProxy())
      {
        eObject = (InternalEObject)EcoreUtil.resolve(eObject, view.getResourceSet());
      }

      CDOID id = ((CDOViewImpl)view).getSession().lookupMetaInstanceID(eObject);
      if (id != null)
      {
        return new CDOMetaImpl((CDOViewImpl)view, eObject, id);
      }
    }

    try
    {
      InternalCDOObject callback = adaptCallback(object, view);
      if (callback != null)
      {
        return callback;
      }
    }
    catch (Throwable t)
    {
      OM.LOG.info(t);
    }

    if (object instanceof InternalEObject)
    {
      EList<Adapter> adapters = ((InternalEObject)object).eAdapters();
      CDOAdapterImpl adapter = (CDOAdapterImpl)EcoreUtil.getAdapter(adapters, CDOAdapterImpl.class);
      if (adapter == null)
      {
        adapter = new CDOAdapterImpl();
        adapters.add(adapter);
      }

      return adapter;
    }

    return null;
  }

  private static InternalCDOObject adaptCallback(Object object, CDOView view) throws Throwable
  {
    if (object instanceof CDOAware)
    {
      CDOAware aware = (CDOAware)object;
      CDOCallbackImpl callback = (CDOCallbackImpl)aware.getCDOCallback();
      if (callback == null)
      {
        InternalEObject instance = (InternalEObject)aware;
        if (instance.eIsProxy())
        {
          instance = (InternalEObject)EcoreUtil.resolve(instance, view.getResourceSet());
        }

        callback = new CDOCallbackImpl(instance);
        aware.setCDOCallback(callback);
        instance.eAdapters().add(callback);
      }

      return callback;
    }

    return null;
  }

  public static Iterator<InternalCDOObject> iterator(Collection<?> instances, final CDOViewImpl view)
  {
    final Iterator<?> delegate = instances.iterator();
    return new Iterator<InternalCDOObject>()
    {
      private Object next;

      public boolean hasNext()
      {
        while (delegate.hasNext())
        {
          next = adapt(delegate.next(), view);
          if (next instanceof InternalCDOObject)
          {
            return true;
          }
        }

        return false;
      }

      public InternalCDOObject next()
      {
        return (InternalCDOObject)next;
      }

      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}
