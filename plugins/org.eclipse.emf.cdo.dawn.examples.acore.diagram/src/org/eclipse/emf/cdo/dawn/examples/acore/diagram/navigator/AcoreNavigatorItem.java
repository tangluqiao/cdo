/*
 * Copyright (c) 2010, 2012, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class AcoreNavigatorItem extends AcoreAbstractNavigatorItem
{

  /**
   * @generated
   */
  static
  {
    final Class[] supportedTypes = new Class[] { View.class, EObject.class };
    Platform.getAdapterManager().registerAdapters(new IAdapterFactory()
    {

      public Object getAdapter(Object adaptableObject, Class adapterType)
      {
        if (adaptableObject instanceof org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator.AcoreNavigatorItem
            && (adapterType == View.class || adapterType == EObject.class))
        {
          return ((org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator.AcoreNavigatorItem)adaptableObject).getView();
        }
        return null;
      }

      public Class[] getAdapterList()
      {
        return supportedTypes;
      }
    }, org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator.AcoreNavigatorItem.class);
  }

  /**
   * @generated
   */
  private View myView;

  /**
   * @generated
   */
  private boolean myLeaf = false;

  /**
   * @generated
   */
  public AcoreNavigatorItem(View view, Object parent, boolean isLeaf)
  {
    super(parent);
    myView = view;
    myLeaf = isLeaf;
  }

  /**
   * @generated
   */
  public View getView()
  {
    return myView;
  }

  /**
   * @generated
   */
  public boolean isLeaf()
  {
    return myLeaf;
  }

  /**
   * @generated
   */
  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator.AcoreNavigatorItem)
    {
      return EcoreUtil.getURI(getView())
          .equals(EcoreUtil.getURI(((org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator.AcoreNavigatorItem)obj).getView()));
    }
    return super.equals(obj);
  }

  /**
   * @generated
   */
  @Override
  public int hashCode()
  {
    return EcoreUtil.getURI(getView()).hashCode();
  }

}
