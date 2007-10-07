package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.internal.ui.SharedIcons;
import org.eclipse.emf.cdo.internal.ui.dialogs.OpenAuditDialog;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public final class OpenAuditAction extends AbstractOpenViewAction
{
  private static final String TITLE = "Open Audit";

  private static final String TOOL_TIP = "Open a historical CDO view";

  private long timeStamp;

  public OpenAuditAction(IWorkbenchPage page, CDOSession session)
  {
    super(page, TITLE + INTERACTIVE, TOOL_TIP, SharedIcons.getDescriptor(SharedIcons.ETOOL_OPEN_EDITOR), session);
  }

  @Override
  protected void preRun() throws Exception
  {
    OpenAuditDialog dialog = new OpenAuditDialog(getPage());
    if (dialog.open() == OpenAuditDialog.OK)
    {
      timeStamp = dialog.getTimeStamp();
    }
    else
    {
      cancel();
    }
  }

  @Override
  protected void doRun() throws Exception
  {
    getSession().openAudit(new ResourceSetImpl(), timeStamp);
  }
}