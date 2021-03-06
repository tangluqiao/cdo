/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions.delegates;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.ui.dialogs.CreateBranchDialog;
import org.eclipse.emf.cdo.internal.ui.handlers.CreateBranchHandler;

import org.eclipse.net4j.util.ui.actions.LongRunningActionDelegate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Eike Stepper
 */
public class CreateBranchActionDelegate extends LongRunningActionDelegate
{
  private CDOBranchPoint base;

  private String name;

  public CreateBranchActionDelegate()
  {
  }

  @Override
  protected void preRun() throws Exception
  {
    ISelection selection = getSelection();
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      Object element = ssel.getFirstElement();
      if (element instanceof CDOBranchPoint)
      {
        base = (CDOBranchPoint)element;
        name = CreateBranchHandler.getValidChildName(base.getBranch());

        CreateBranchDialog dialog = new CreateBranchDialog(getShell(), base, name);
        if (dialog.open() == CreateBranchDialog.OK)
        {
          base = dialog.getBranchPoint();
          name = dialog.getName();
          return;
        }
      }
    }

    base = null;
    name = null;
    cancel();
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    try
    {
      CDOBranch branch = base.getBranch();
      branch.createBranch(name, base.getTimeStamp());
    }
    finally
    {
      base = null;
      name = null;
    }
  }
}
