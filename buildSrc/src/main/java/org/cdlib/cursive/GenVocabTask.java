package org.cdlib.cursive;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

public class GenVocabTask extends DefaultTask {
  private final Logger logger = getProject().getLogger();
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
    if (ensureTargetDirectory()) {
      new GenVocab(logger).generate(targetDir);
    }
  }

  private boolean ensureTargetDirectory() {
    if (targetDir.exists()) {
      try {
        logger.lifecycle("Deleting existing target directory " + targetDir);
        FileUtils.deleteDirectory(targetDir);
        return false;
      } catch (IOException e) {
        logger.error("Unable to delete target directory " + targetDir, e);
      }
    }

    if (!targetDir.mkdirs()) {
      logger.error("Unable to recreate target directory " + targetDir);
      return false;
    }
    return true;
  }
}
