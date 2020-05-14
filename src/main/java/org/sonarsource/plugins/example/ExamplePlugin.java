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
package org.sonarsource.plugins.example;

import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonarsource.plugins.example.hooks.PostJobInScanner;
import org.sonarsource.plugins.example.hooks.DisplayQualityGateStatus;
import org.sonarsource.plugins.example.languages.FooLanguage;
import org.sonarsource.plugins.example.languages.FooQualityProfile;
import org.sonarsource.plugins.example.measures.ComputeSizeAverage;
import org.sonarsource.plugins.example.measures.ComputeSizeRating;
import org.sonarsource.plugins.example.measures.ExampleMetrics;
import org.sonarsource.plugins.example.measures.SetSizeOnFilesSensor;
import org.sonarsource.plugins.example.rules.CreateIssuesOnJavaFilesSensor;
import org.sonarsource.plugins.example.rules.FooLintIssuesLoaderSensor;
import org.sonarsource.plugins.example.rules.FooLintRulesDefinition;
import org.sonarsource.plugins.example.rules.JavaRulesDefinition;
import org.sonarsource.plugins.example.settings.FooLanguageProperties;
import org.sonarsource.plugins.example.settings.HelloWorldProperties;
import org.sonarsource.plugins.example.settings.SayHelloFromScanner;
import org.sonarsource.plugins.example.web.MyPluginPageDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import static java.util.Arrays.asList;

/**
 * This class is the entry point for all extensions. It is referenced in pom.xml.
 */
public class ExamplePlugin implements Plugin {

  private static final Logger LOGGER = Loggers.get(FooLanguage.class);

  public static final String WHITELIST_PROPERTY_KEY = "sonar.foo.url.whitelist";

  @Override
  public void define(Context context) {
    // tutorial on hooks
    // http://docs.sonarqube.org/display/DEV/Adding+Hooks

    LOGGER.info("JCV ExamplePlugin.define() 1");
    context.addExtensions(PostJobInScanner.class, DisplayQualityGateStatus.class);

    LOGGER.info("JCV ExamplePlugin.define() 2");
    // tutorial on languages
    context.addExtensions(FooLanguage.class, FooQualityProfile.class);
    context.addExtension(FooLanguageProperties.getProperties());

    LOGGER.info("JCV ExamplePlugin.define() 3");
    // tutorial on measures
    context
      .addExtensions(ExampleMetrics.class, SetSizeOnFilesSensor.class, ComputeSizeAverage.class, ComputeSizeRating.class);

    LOGGER.info("JCV ExamplePlugin.define() 4");
    // tutorial on rules
    context.addExtensions(JavaRulesDefinition.class, CreateIssuesOnJavaFilesSensor.class);
    context.addExtensions(FooLintRulesDefinition.class, FooLintIssuesLoaderSensor.class);

    LOGGER.info("JCV ExamplePlugin.define() 5");
    // tutorial on settings
    context
      .addExtensions(HelloWorldProperties.getProperties())
      .addExtension(SayHelloFromScanner.class);

    LOGGER.info("JCV ExamplePlugin.define() 6");

    // tutorial on web extensions
    context.addExtension(MyPluginPageDefinition.class);

    LOGGER.info("JCV ExamplePlugin.define() 7");

    context.addExtensions(asList(
      PropertyDefinition.builder("sonar.foo.file.suffixes")
        .name("Suffixes FooLint")
        .description("Suffixes supported by FooLint")
        .category("FooLint")
        .defaultValue("")
        .build(),
        PropertyDefinition.builder("sonar.foo.url.whitelist")
        .name("URL Whitelist")
        .description("URL regex patterns not marked as errors. One per line")
        .category("FooLint")
        .defaultValue("")
        .type(PropertyType.TEXT)
        .build()
        ));
    
    LOGGER.info("JCV ExamplePlugin.define() 8");
  }
}
