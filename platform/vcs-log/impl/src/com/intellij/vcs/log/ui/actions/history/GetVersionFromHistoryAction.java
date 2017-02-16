/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.vcs.log.ui.actions.history;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.history.GetVersionAction;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import org.jetbrains.annotations.NotNull;

public class GetVersionFromHistoryAction extends FileHistorySingleCommitAction {

  @Override
  protected boolean isEnabled(@NotNull AnActionEvent e) {
    FilePath filePath = e.getData(VcsDataKeys.FILE_PATH);
    VcsFileRevision revision = e.getData(VcsDataKeys.VCS_FILE_REVISION);

    return revision != null && filePath != null && !filePath.isDirectory();
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getRequiredData(CommonDataKeys.PROJECT);
    if (ChangeListManager.getInstance(project).isFreezedWithNotification(null)) return;
    GetVersionAction.doGet(project, e.getRequiredData(VcsDataKeys.VCS_FILE_REVISION), e.getRequiredData(VcsDataKeys.FILE_PATH));
  }
}
