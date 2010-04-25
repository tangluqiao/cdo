/*******************************************************************************
 * Copyright (c) 2009 - 2010 Martin Fluegge (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.util;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.cdo.dawn.internal.util.bundle.OM;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CanonicalEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.parts.DiagramDocumentEditor;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.EditPartService;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.ui.PlatformUI;

/**
 * @author Martin Fluegge
 */
public class DawnDiagramUpdater
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, DawnDiagramUpdater.class);
  public static void refreshEditPart(EditPart editPart)
  {
    refeshEditpartInternal(editPart);
  }

  public static void refreshEditPart(final EditPart editPart, DiagramDocumentEditor editor)
  {
    editor.getEditingDomain().getCommandStack().execute(new RecordingCommand(editor.getEditingDomain())
    {
      public void doExecute()
      {
        DawnDiagramUpdater.refreshEditPart(editPart);
      }
    });
  }

  public static void refreshEditCurrentSelected(TransactionalEditingDomain editingDomain)
  {
    editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain)
    {

      @Override
      protected void doExecute()
      {
        // ((ExamplediagramDocumentProvider)getDocumentProvider()).changed(getEditorInput());
        ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
            .getSelection();
        if (selection instanceof IStructuredSelection)
        {
          IStructuredSelection structuredSelection = (IStructuredSelection)selection;
          if (structuredSelection.size() != 1)
          {
            return;
          }
          if (structuredSelection.getFirstElement() instanceof EditPart
              && ((EditPart)structuredSelection.getFirstElement()).getModel() instanceof View)
          {
            EObject modelElement = ((View)((EditPart)structuredSelection.getFirstElement()).getModel()).getElement();
            List editPolicies = CanonicalEditPolicy.getRegisteredEditPolicies(modelElement);
            for (Iterator it = editPolicies.iterator(); it.hasNext();)
            {
              CanonicalEditPolicy nextEditPolicy = (CanonicalEditPolicy)it.next();
              nextEditPolicy.refresh();
            }
          }
        }
      }
    });
  }

  private static void refeshEditpartInternal(EditPart editPart)
  {
    if (editPart != null)
    {
      try
      {

        // EObject modelElement = ((View)(editPart).getModel()).getElement();
        // List editPolicies = CanonicalEditPolicy.getRegisteredEditPolicies(modelElement);
        // for (Iterator it = editPolicies.iterator(); it.hasNext();)
        // {
        // CanonicalEditPolicy nextEditPolicy = (CanonicalEditPolicy)it.next();
        // nextEditPolicy.refresh();
        // }

        editPart.refresh();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

      if (editPart instanceof DiagramEditPart)
      {
        for (Object childEditPart : ((DiagramEditPart)editPart).getConnections())
        {
          if (childEditPart instanceof EditPart)
          {
            refeshEditpartInternal((EditPart)childEditPart);
          }
        }
      }
      for (Object childEditPart : editPart.getChildren())
      {
        if (childEditPart instanceof EditPart)
        {
          refeshEditpartInternal((EditPart)childEditPart);
        }
      }
    }
    else
    {
      System.err.println("EDITPART is null");
    }
  }

  public static View findView(EObject element)
  {
    if (element instanceof View)
      return (View)element;
    else
    {
      if (element.eContainer() == null)
        return null;
      if (element.eContainer() instanceof View)
      {
        return (View)element.eContainer();
      }
      else
      {
        return findView(element.eContainer());
      }
    }
    // return null;
  }

  public static View findViewForModel(EObject object, DiagramDocumentEditor editor)
  {

    try
    {
      for (EObject e : editor.getDiagram().eContents())
      {
        if (e instanceof View && ((View)e).getElement().equals(object))
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("FOUND View: {0} for view obj: {1} ", e,object); //$NON-NLS-1$
          }
          return (View)e;
        }
      }
    }
    catch (Exception e)
    {
      // quickhack
    }

    return null;
  }

  // copied from DawnGMFTransactionListener
  public static EditPart createOrFindEditPartIfViewExists(View view, DiagramDocumentEditor editor)
  {
    // EditPart editPart = ResourceHelper.findEditPart(view, editor);
    EditPart editPart = findEditPart(view, editor.getDiagramEditPart());

    if (view != null)
    {
      if (editPart == null) // does not exist?
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("EditPart does not exist for view  {0} ", view); //$NON-NLS-1$
        }
        editPart = EditPartService.getInstance().createGraphicEditPart(view);
        setParentEditPart(editor, view, editPart);
      }
    }
    if (TRACER.isEnabled())
    {
      TRACER.format("Found EditPart:  {0} ", editPart); //$NON-NLS-1$
    }
    return editPart;
  }

  // copied from DawnGMFTransactionListener
  public static void setParentEditPart(final DiagramDocumentEditor editor, View view, EditPart editPart)
  {
    View viewParent = (View)view.eContainer();
    EditPart parentEditPart = findEditPart(viewParent, editor);
    if (parentEditPart == null)
    {
      parentEditPart = editor.getDiagramEditPart();
    }
    editPart.setParent(parentEditPart);
  }

  public static EditPart findEditPart(View view, DiagramDocumentEditor dawnDiagramEditor)
  {
    DiagramEditPart diagramEditPart = dawnDiagramEditor.getDiagramEditPart();

    for (Object e : diagramEditPart.getChildren())
    {
      EditPart ep = (EditPart)e;
      if (ep.getModel().equals(view))
      {
        return ep;
      }
    }

    for (Object e : diagramEditPart.getConnections())
    {
      EditPart ep = (EditPart)e;
      if (ep.getModel().equals(view))
      {
        return ep;
      }
    }
    return null;
  }

  public static EditPart findEditPart(View view, EditPart editPart)
  {
    EditPart ret;

    if (editPart.getModel().equals(view))
    {
      return editPart;
    }

    for (Object o : editPart.getChildren())
    {
      EditPart child = (EditPart)o;
      ret = findEditPart(view, child);
      if (ret != null)
      {
        return ret;
      }
    }

    if (editPart instanceof DiagramEditPart)
    {
      for (Object o : ((DiagramEditPart)editPart).getConnections())
      {
        EditPart child = (EditPart)o;
        ret = findEditPart(view, child);
        if (ret != null)
        {
          return ret;
        }
      }
    }
    return null;
  }

  public static EditPart findEditPart(View view, EditPartViewer viewer)
  {
    return (EditPart)viewer.getEditPartRegistry().get(view);
  }

  /**
   * In a normal GMF environment the diagram is loaded from a xml resource. In this scenario the XMLHelper sets "null"
   * to the element of edge. Thus the value is 'set' with a null value. We do not have this in our case because the
   * element is carefully loaded from the CDO repository. But if the value is not set a getElement() call fills the
   * element with the eContainer. See <b>org.eclipse.gmf.runtime.notation.impl.ViewImpl.isSetElement()</b>. This happens
   * when the ConnectionEditPart is activated and the model registered. See
   * <b>org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart.registerModel()</b>
   * <p>
   * In our scenario the Edges will be wrongly connected to the diagram itself, which makes the CanonicalEditingPolicy
   * to remove and restore the edge. Long, short story, we must 'to' the elements here to have the element set.
   * 
   * @param diagram
   */
  public static void initializeElement(Diagram diagram)
  {
    for (Object obj : diagram.getEdges())
    {
      Edge edge = (Edge)obj;
      if (!edge.isSetElement())
      {
        boolean eDeliver = edge.eDeliver();
        edge.eSetDeliver(false);
        edge.setElement(null);
        edge.eSetDeliver(eDeliver);
      }
    }
  }
}
