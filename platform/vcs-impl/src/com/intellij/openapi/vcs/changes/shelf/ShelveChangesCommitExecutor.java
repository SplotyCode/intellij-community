// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.intellij.openapi.vcs.changes.shelf;

import com.intellij.CommonBundle;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsBundle;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.util.WaitForProgressToShow;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ShelveChangesCommitExecutor extends LocalCommitExecutor {
  private static final Logger LOG = Logger.getInstance("#com.intellij.openapi.vcs.changes.shelf.ShelveChangesCommitExecutor");

  private final Project myProject;

  public ShelveChangesCommitExecutor(final Project project) {
    myProject = project;
  }

  @Override
  @Nls
  public String getActionText() {
    return VcsBundle.message("shelve.changes.action");
  }

  @Override
  @NotNull
  public CommitSession createCommitSession() {
    return new ShelveChangesCommitSession();
  }

  @Override
  public String getHelpId() {
    return "reference.dialogs.vcs.shelve";
  }

  @Override
  public boolean supportsPartialCommit() {
    return true;
  }

  private class ShelveChangesCommitSession implements CommitSession, CommitSessionContextAware {
    @Override
    public void setContext(CommitContext context) {
    }

    @Override
    public boolean canExecute(Collection<Change> changes, String commitMessage) {
      return changes.size() > 0;
    }

    @Override
    public void execute(@NotNull Collection<Change> changes, @Nullable String commitMessage) {
      if (changes.size() > 0 && !ChangesUtil.hasFileChanges(changes)) {
        WaitForProgressToShow.runOrInvokeLaterAboveProgress(() -> Messages
          .showErrorDialog(myProject, VcsBundle.message("shelve.changes.only.directories"), VcsBundle.message("shelve.changes.action")), null, myProject);
        return;
      }
      try {
        final ShelvedChangeList list = ShelveChangesManager.getInstance(myProject).shelveChanges(changes, commitMessage, true, false, true);
        ShelvedChangesViewManager.getInstance(myProject).activateView(list);

        Change[] changesArray = changes.toArray(new Change[0]);
        LocalChangeList changeList = ChangesUtil.getChangeListIfOnlyOne(myProject, changesArray);
        if (changeList != null) {
          ChangeListManager.getInstance(myProject).scheduleAutomaticEmptyChangeListDeletion(changeList, true);
        }
      }
      catch (final Exception ex) {
        LOG.info(ex);
        WaitForProgressToShow.runOrInvokeLaterAboveProgress(
          () -> Messages.showErrorDialog(myProject, VcsBundle.message("create.patch.error.title", ex.getMessage()), CommonBundle.getErrorTitle()), ModalityState.NON_MODAL, myProject);
      }
    }

    @Override
    public String getHelpId() {
      return null;
    }
  }
}
