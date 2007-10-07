package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.internal.ui.views.CDOViewHistory;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenResourceEditorAction extends EntryAction
{
  private static final String TITLE = "Open Editor";

  private static final String TOOL_TIP = "Open a CDO editor for this resource";

  public OpenResourceEditorAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image,
      CDOViewHistory.Entry entry)
  {
    super(page, TITLE, TOOL_TIP, null, entry);
  }

  @Override
  protected void doRun() throws Exception
  {
    CDOView view = getEntry().getView();
    String resourcePath = getEntry().getResourcePath();
    CDOEditor.open(getPage(), view, resourcePath);
  }
}