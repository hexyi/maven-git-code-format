package com.cosium.code.format;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.cosium.code.format.formatter.CodeFormatter;
import com.cosium.code.format.formatter.CompositeCodeFormatter;
import com.cosium.code.format.formatter.JavaFormatter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Created on 01/11/17.
 *
 * @author Reda.Housni-Alaoui
 */
public abstract class AbstractMavenGitCodeFormatMojo extends AbstractMojo {

  private static final String HOOKS_DIR = "hooks";
  private final CodeFormatter codeFormatter =
      new CompositeCodeFormatter(new JavaFormatter(this::getLog));

  @Parameter(readonly = true, defaultValue = "${project}")
  private MavenProject currentProject;

  protected final Repository gitRepository() {
    Repository gitRepository;
    try {
      gitRepository = new FileRepositoryBuilder().findGitDir(currentProject.getBasedir()).build();
    } catch (IOException e) {
      throw new RuntimeException(
          "Could not find the git repository. Run 'git init' if you did not.", e);
    }
    return gitRepository;
  }

  protected final Path baseDir() {
    return currentProject.getBasedir().toPath();
  }

  protected final String artifactId() {
    return currentProject.getArtifactId();
  }

  protected final CodeFormatter codeFormatter() {
    return codeFormatter;
  }

  protected final boolean isExecutionRoot(){
    return currentProject.isExecutionRoot();
  }

  /**
   * Get or creates the git hooks directory
   *
   * @return The git hooks directory
   */
  protected final Path getOrCreateHooksDirectory() {
    Path hooksDirectory = gitRepository().getDirectory().toPath().resolve(HOOKS_DIR);
    if (!Files.exists(hooksDirectory)) {
      getLog().debug("Creating directory " + hooksDirectory);
      try {
        Files.createDirectories(hooksDirectory);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      getLog().debug(hooksDirectory + " already exists");
    }
    return hooksDirectory;
  }

  protected final Path gitBaseDir() {
    return gitRepository().getDirectory().getParentFile().toPath();
  }
}
