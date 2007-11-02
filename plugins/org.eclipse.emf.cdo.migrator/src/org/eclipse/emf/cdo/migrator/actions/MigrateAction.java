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
package org.eclipse.emf.cdo.migrator.actions;

import org.eclipse.emf.cdo.migrator.CDOMigrator;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class MigrateAction implements IObjectActionDelegate
{
  @SuppressWarnings("unused")
  private IAction action;

  private ISelection selection;

  public MigrateAction()
  {
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    this.action = action;
    this.selection = selection;
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart)
  {
    this.action = action;
  }

  public void run(IAction action)
  {
    new Job("Migrating EMF model")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        try
        {
          IFile file = getFile();
          if (file == null)
          {
            MessageDialog.openError(new Shell(), "CDO Migrator", "The selected element is not a *.genmodel file.");
          }
          else
          {
            GenModel genModel = getGenModel(file);
            if (genModel == null)
            {
              MessageDialog.openError(new Shell(), "CDO Migrator",
                  "The selected file does not contain a generator model.");
            }
            else
            {
              CDOMigrator.adjustGenModel(genModel, file.getProject());
              MessageDialog.openInformation(new Shell(), "CDO Migrator",
                  "The selected generator model has been migrated.");
            }
          }
        }
        catch (Exception ex)
        {
          return new Status(IStatus.ERROR, "org.eclipse.emf.cdo.migrator", "Problem while migrating EMF model", ex);
        }

        return Status.OK_STATUS;
      }
    }.schedule();
  }

  protected IFile getFile()
  {
    if (selection instanceof IStructuredSelection)
    {
      Object element = ((IStructuredSelection)selection).getFirstElement();
      if (element instanceof IFile)
      {
        IFile file = (IFile)element;
        if ("genmodel".equals(file.getFileExtension()))
        {
          return file;
        }
      }
    }

    return null;
  }

  protected GenModel getGenModel(IFile file)
  {
    ResourceSet resourceSet = new ResourceSetImpl();

    Map<String, Object> map = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    map.put("*", new XMIResourceFactoryImpl());

    URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
    Resource resource = resourceSet.getResource(uri, true);

    EList<EObject> contents = resource.getContents();
    if (!contents.isEmpty())
    {
      EObject object = contents.get(0);
      if (object instanceof GenModel)
      {
        return (GenModel)object;
      }
    }

    return null;
  }
}
