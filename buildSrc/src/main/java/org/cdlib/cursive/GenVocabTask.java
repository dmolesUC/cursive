package org.cdlib.cursive;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

public class GenVocabTask extends DefaultTask {
  private File targetDir;

  public File getTargetDir() {
    return targetDir;
  }

  public void setTargetDir(File targetDir) {
    this.targetDir = targetDir;
  }

  @TaskAction
  public void generate() {
    new GenVocab().generate(targetDir);
  }
}
