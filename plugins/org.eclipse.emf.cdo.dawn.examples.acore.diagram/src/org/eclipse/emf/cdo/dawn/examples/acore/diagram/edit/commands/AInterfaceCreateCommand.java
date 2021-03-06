/*
 * Copyright (c) 2010, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.commands;

import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.AInterface;
import org.eclipse.emf.cdo.dawn.examples.acore.AcoreFactory;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.commands.EditElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.ConfigureRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class AInterfaceCreateCommand extends EditElementCommand
{

  /**
   * @generated
   */
  public AInterfaceCreateCommand(CreateElementRequest req)
  {
    super(req.getLabel(), null, req);
  }

  /**
   * FIXME: replace with setElementToEdit()
   *
   * @generated
   */
  @Override
  protected EObject getElementToEdit()
  {
    EObject container = ((CreateElementRequest)getRequest()).getContainer();
    if (container instanceof View)
    {
      container = ((View)container).getElement();
    }
    return container;
  }

  /**
   * @generated
   */
  @Override
  public boolean canExecute()
  {
    return true;

  }

  /**
   * @generated
   */
  @Override
  protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
  {
    AInterface newElement = AcoreFactory.eINSTANCE.createAInterface();

    ACoreRoot owner = (ACoreRoot)getElementToEdit();
    owner.getInterfaces().add(newElement);

    doConfigure(newElement, monitor, info);

    ((CreateElementRequest)getRequest()).setNewElement(newElement);
    return CommandResult.newOKCommandResult(newElement);
  }

  /**
   * @generated
   */
  protected void doConfigure(AInterface newElement, IProgressMonitor monitor, IAdaptable info) throws ExecutionException
  {
    IElementType elementType = ((CreateElementRequest)getRequest()).getElementType();
    ConfigureRequest configureRequest = new ConfigureRequest(getEditingDomain(), newElement, elementType);
    configureRequest.setClientContext(((CreateElementRequest)getRequest()).getClientContext());
    configureRequest.addParameters(getRequest().getParameters());
    ICommand configureCommand = elementType.getEditCommand(configureRequest);
    if (configureCommand != null && configureCommand.canExecute())
    {
      configureCommand.execute(monitor, info);
    }
  }

}
