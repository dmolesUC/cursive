package org.cdlib.cursive;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

public class GenVocabTask extends DefaultTask {
  private File targetDir;

  @InputFiles
  public FileCollection getSourceFiles() {
    return getProject().files("src");
  }

  @Input
  @OutputDirectory
  public File getTargetDir() {
    return targetDir;
  }

  public void setTargetDir(File targetDir) {
    this.targetDir = targetDir;
  }

  @TaskAction
  public void generate() {
    new GenVocab(getProject().getLogger()).generate(targetDir);
  }
}
