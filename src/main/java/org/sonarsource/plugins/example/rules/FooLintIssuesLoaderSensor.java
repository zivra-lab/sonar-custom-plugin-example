/*
 * Example Plugin for SonarQube
 * Copyright (C) 2009-2020 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.plugins.example.rules;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLStreamException;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Configuration;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.plugins.example.ExamplePlugin;
import org.sonarsource.plugins.example.languages.FooLanguage;

/**
 * The goal of this Sensor is to load the results of an analysis performed by a
 * fictive external tool named: FooLint Results are provided as an xml file and
 * are corresponding to the rules defined in 'rules.xml'. To be very abstract,
 * these rules are applied on source files made with the fictive language Foo.
 */
public class FooLintIssuesLoaderSensor implements Sensor {

  private static final Logger LOGGER = Loggers.get(FooLintIssuesLoaderSensor.class);

  protected static final String REPORT_PATH_KEY = "sonar.foolint.reportPath";

  protected final Configuration config;
  protected final FileSystem fileSystem;
  protected SensorContext context;

  /**
   * Use of IoC to get Settings, FileSystem, RuleFinder and ResourcePerspectives
   */
  public FooLintIssuesLoaderSensor(final Configuration config, final FileSystem fileSystem) {
    LOGGER.info("JCV FooLintIssuesLoaderSensor() 1");
    this.config = config;
    this.fileSystem = fileSystem;
  }

  @Override
  public void describe(final SensorDescriptor descriptor) {
    LOGGER.info("JCV FooLintIssuesLoaderSensor.describe() 1");
    descriptor.name("FooLint Issues Loader Sensor");
    LOGGER.info("JCV FooLintIssuesLoaderSensor.describe() 2");
    //descriptor.onlyOnLanguage(FooLanguage.KEY);
    LOGGER.info("JCV FooLintIssuesLoaderSensor.describe() 3");
  }

  protected String reportPathKey() {
    return REPORT_PATH_KEY;
  }

  protected String getReportPath() {
    LOGGER.info("JCV FooLintIssuesLoaderSensor.getReportPath() 1");
    final Optional<String> o = config.get(reportPathKey());
    LOGGER.info("JCV FooLintIssuesLoaderSensor.getReportPath() 2");
    if (o.isPresent()) {
      LOGGER.info("JCV FooLintIssuesLoaderSensor.getReportPath() 3");
      return o.get();
    }
    LOGGER.info("JCV FooLintIssuesLoaderSensor.getReportPath() 4");
    return null;
  }

  @Override
  public void execute(final SensorContext context) {
    this.context = context;
    Optional<String> whitelistOptional = config.get(ExamplePlugin.WHITELIST_PROPERTY_KEY);
    String whitelist = whitelistOptional.get();
    LOGGER.info("JCV Whitelist: " + whitelist);
    List<String> whitelistLines = Arrays.asList(whitelist.split("\\n"));
    for(String line: whitelistLines) {
      LOGGER.info("JCV Whitelist Line: " + line);
    }
    LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 1");
    LOGGER.info("JCV (mock) Parsing 'FooLint' Analysis Results");
    final FooLintAnalysisResultsParser parser = new FooLintAnalysisResultsParser();
    LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 2");
    final FileSystem fs = context.fileSystem();
    final Iterable<InputFile> files = context.fileSystem().inputFiles(fs.predicates().all());
    for (InputFile inputFile : files) {
      final List<ErrorDataFromExternalLinter> errors = parser.scanForUrls(inputFile, whitelistLines);
      LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 3");
      for (final ErrorDataFromExternalLinter error : errors) {
        LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 4");
        getResourceAndSaveIssue(error);
      }
    }
  }

  /*
   * @Override public void execute(final SensorContext context) {
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 1"); String reportPath =
   * getReportPath(); LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 2"); if
   * (reportPath != null) {
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 3"); this.context =
   * context; LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 4"); File
   * analysisResultsFile = new File(reportPath);
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 5"); try {
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 6");
   * parseAndSaveResults(analysisResultsFile);
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 7"); } catch
   * (XMLStreamException e) {
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 8"); throw new
   * IllegalStateException("Unable to parse the provided FooLint file", e); }
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 9"); } //JCV START else{
   * this.context = context;
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 10"); try {
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 11");
   * parseAndSaveResultsJCV();
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 12"); } catch
   * (XMLStreamException e) {
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 13"); throw new
   * IllegalStateException("Unable to parse the provided FooLint file", e); }
   * LOGGER.info("JCV FooLintIssuesLoaderSensor.execute() 14"); } //JCV END }
   */

   /*
  // JCV START
  protected void parseAndSaveResultsJCV() throws XMLStreamException {
    LOGGER.info("JCV FooLintIssuesLoaderSensor.parseAndSaveResultsJCV() 1");
    LOGGER.info("JCV (mock) Parsing 'FooLint' Analysis Results");
    final FooLintAnalysisResultsParser parser = new FooLintAnalysisResultsParser();
    LOGGER.info("JCV FooLintIssuesLoaderSensor.parseAndSaveResultsJCV() 2");
    final List<ErrorDataFromExternalLinter> errors = parser.parseJCV();
    LOGGER.info("JCV FooLintIssuesLoaderSensor.parseAndSaveResultsJCV() 3");
    for (final ErrorDataFromExternalLinter error : errors) {
      LOGGER.info("JCV FooLintIssuesLoaderSensor.parseAndSaveResultsJCV() 4");
      getResourceAndSaveIssue(error);
    }
  }
  // JCV END
  */

  protected void parseAndSaveResults(final File file) throws XMLStreamException {
    LOGGER.info("JCV FooLintIssuesLoaderSensor.parseAndSaveResults() 1");
    LOGGER.info("(mock) Parsing 'FooLint' Analysis Results");
    final FooLintAnalysisResultsParser parser = new FooLintAnalysisResultsParser();
    LOGGER.info("JCV FooLintIssuesLoaderSensor.parseAndSaveResults() 2");
    final List<ErrorDataFromExternalLinter> errors = parser.parse(file);
    LOGGER.info("JCV FooLintIssuesLoaderSensor.parseAndSaveResults() 3");
    for (final ErrorDataFromExternalLinter error : errors) {
      LOGGER.info("JCV FooLintIssuesLoaderSensor.parseAndSaveResults() 4");
      getResourceAndSaveIssue(error);
    }
  }

  private void getResourceAndSaveIssue(final ErrorDataFromExternalLinter error) {
    LOGGER.info("JCV FooLintIssuesLoaderSensor.getResourceAndSaveIssue() 1");
    LOGGER.info("JCV error.toString(): " + error.toString());
    // LOGGER.debug(error.toString());

    LOGGER.info("JCV FooLintIssuesLoaderSensor.getResourceAndSaveIssue() 2");
    final InputFile inputFile = fileSystem
        .inputFile(fileSystem.predicates().and(fileSystem.predicates().hasRelativePath(error.getFilePath()),
            fileSystem.predicates().hasType(InputFile.Type.MAIN)));

    LOGGER.info("JCV FooLintIssuesLoaderSensor.getResourceAndSaveIssue() 3");

    LOGGER.info("inputFile null ? " + (inputFile == null));

    if (inputFile != null) {
      LOGGER.info("JCV FooLintIssuesLoaderSensor.getResourceAndSaveIssue() 4");
      saveIssue(inputFile, error.getLine(), error.getType(), error.getDescription());
      LOGGER.info("JCV FooLintIssuesLoaderSensor.getResourceAndSaveIssue() 5");
    } else {
      LOGGER.info("JCV FooLintIssuesLoaderSensor.getResourceAndSaveIssue() 6");
      LOGGER.error("Not able to find a InputFile with " + error.getFilePath());
    }
  }

  private void saveIssue(final InputFile inputFile, final int line, final String externalRuleKey,
      final String message) {
    LOGGER.info("JCV FooLintIssuesLoaderSensor.saveIssue() 1");
    LOGGER.info("JCV inputFile: " + inputFile.toString());
    LOGGER.info("JCV line: " + line);
    LOGGER.info("JCV externalRuleKey (error type): " + externalRuleKey);
    LOGGER.info("JCV message (error description): " + message);
    LOGGER.info("JCV InputFile language: " + inputFile.language());
    final String repoKeyForLang = getRepositoryKeyForLanguage(inputFile.language());

    final RuleKey ruleKey = RuleKey.of(repoKeyForLang, externalRuleKey);

    LOGGER.info("JCV ruleKey: " + ruleKey);

    LOGGER.info("JCV FooLintIssuesLoaderSensor.saveIssue() 2");
    LOGGER.info("JCV context: " + context);
    final NewIssue newIssue = context.newIssue().forRule(ruleKey);

    LOGGER.info("JCV FooLintIssuesLoaderSensor.saveIssue() 3");
    final NewIssueLocation primaryLocation = newIssue.newLocation().on(inputFile).message(message);
    if (line > 0) {
      LOGGER.info("JCV FooLintIssuesLoaderSensor.saveIssue() 4");
      primaryLocation.at(inputFile.selectLine(line));
    }
    LOGGER.info("JCV FooLintIssuesLoaderSensor.saveIssue() 5");
    newIssue.at(primaryLocation);
    LOGGER.info("JCV FooLintIssuesLoaderSensor.saveIssue() 6");
    newIssue.save();
  }

  private static String getRepositoryKeyForLanguage(final String languageKey) {
    LOGGER.info("JCV FooLintIssuesLoaderSensor.getRepositoryKeyForLanguage() 1");
    return languageKey.toLowerCase() + "-" + FooLintRulesDefinition.KEY;
  }

  @Override
  public String toString() {
    return "FooLintIssuesLoaderSensor";
  }

  private class ErrorDataFromExternalLinter {

    private final String externalRuleId;
    private final String issueMessage;
    private final String filePath;
    private final int line;

    public ErrorDataFromExternalLinter(final String externalRuleId, final String issueMessage, final String filePath,
        final int line) {
      LOGGER.info("JCV ErrorDataFromExternalLinter.constructor() 1");
      this.externalRuleId = externalRuleId;
      this.issueMessage = issueMessage;
      this.filePath = filePath;
      this.line = line;
    }

    public String getType() {
      return externalRuleId;
    }

    public String getDescription() {
      return issueMessage;
    }

    public String getFilePath() {
      return filePath;
    }

    public int getLine() {
      return line;
    }

    @Override
    public String toString() {
      final StringBuilder s = new StringBuilder();
      s.append(externalRuleId);
      s.append("|");
      s.append(issueMessage);
      s.append("|");
      s.append(filePath);
      s.append("(");
      s.append(line);
      s.append(")");
      return s.toString();
    }
  }

  private class FooLintAnalysisResultsParser {

    public List<ErrorDataFromExternalLinter> parse(final File file) throws XMLStreamException {
      LOGGER.info("JCV FooLintAnalysisResultsParser.parse() 1");
      LOGGER.info("Parsing file {}", file.getAbsolutePath());

      // as the goal of this example is not to demonstrate how to parse an xml file we
      // return an hard coded list of FooError

      final ErrorDataFromExternalLinter fooError1 = new ErrorDataFromExternalLinter("ExampleRule1",
          "More precise description of the error", "src/MyClass.foo", 2);
      final ErrorDataFromExternalLinter fooError2 = new ErrorDataFromExternalLinter("ExampleRule2",
          "More precise description of the error", "src/MyClass.foo", 7);
      LOGGER.info("JCV FooLintAnalysisResultsParser.parse() 2");

      return Arrays.asList(fooError1, fooError2);
    }

    /*

    // JCV START
    public List<ErrorDataFromExternalLinter> parseJCV() {
      LOGGER.info("JCV FooLintAnalysisResultsParser.parseJCV() 1");
      // LOGGER.info("Parsing file {}", file.getAbsolutePath());
      // as the goal of this example is not to demonstrate how to parse an xml file we
      // return an hard coded list of FooError

      final File file = new File("src/MyClass.foo");
      final List<ErrorDataFromExternalLinter> issues = scanForUrls(file);

      LOGGER.info("JCV FooLintAnalysisResultsParser.parseJCV() 2");

      return issues;

    }
    // JCV END
    */
    

    public List<ErrorDataFromExternalLinter> scanForUrls(final InputFile inputFile, List<String> whitelistLines) {
      final String regex = ".*https?://.*";
      final List<ErrorDataFromExternalLinter> issues = new ArrayList<>();
      int lineNumber = 0;
      LOGGER.info("JCV FooLintAnalysisResultsParser.scanForUrls() 1");
      File file = inputFile.file();
      try (Scanner sc = new Scanner(file, StandardCharsets.UTF_8.name())) {
        while (sc.hasNextLine()) {
          lineNumber++;
          final String text = sc.nextLine();
          final boolean matches = Pattern.matches(regex, text);
          String filePath = inputFile.relativePath();
          if (matches) {
            LOGGER.info(filePath + ":" + lineNumber + "(matches) " + text);
            boolean matchesWhitelistLine = false;
            for(String whitelistLine: whitelistLines) {
              String whitelistLineRegex = ".*" + whitelistLine + ".*";
              matchesWhitelistLine = Pattern.matches(whitelistLineRegex, text);
              if(matchesWhitelistLine){
                LOGGER.info("JCV line matches whitelisted URL");
                break;
              }
            }
            if(!matchesWhitelistLine){
              LOGGER.info("Line added");
              issues.add(new ErrorDataFromExternalLinter("foundURL", "Unexpected URL was found in code",
              filePath , lineNumber));
            }
            else{
              LOGGER.info("Line NOT added");
            }
            //LOGGER.info(text);
          }
        }
      } catch (final IOException e) {
        e.printStackTrace();

      }
      LOGGER.info("JCV FooLintAnalysisResultsParser.scanForUrls() 2");
      return issues;
    }

  }

}
