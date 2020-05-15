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
package org.sonarsource.plugins.example.languages;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonarsource.plugins.example.rules.JavaRulesDefinition;

import static org.sonarsource.plugins.example.rules.FooLintRulesDefinition.REPO_KEY;

/**
 * Default, BuiltIn Quality Profile for the projects having files of the language "foo"
 */
public final class FooQualityProfile implements BuiltInQualityProfilesDefinition {

  @Override
  public void define(Context context) {
    buildQualityProfile(context, "Foo FooLint Rules", FooLanguage.KEY, true);
    //buildQualityProfile(context, "Java FooLint Rules", JavaRulesDefinition.JAVA_LANGUAGE, false);

    /*
    NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("FooLint Rules", FooLanguage.KEY);
    profile.setDefault(true);

    NewBuiltInActiveRule rule1 = profile.activateRule(REPO_KEY, "ExampleRule1");
    rule1.overrideSeverity("BLOCKER");
    NewBuiltInActiveRule rule2 = profile.activateRule(REPO_KEY, "ExampleRule2");
    rule2.overrideSeverity("MAJOR");
    NewBuiltInActiveRule rule3 = profile.activateRule(REPO_KEY, "ExampleRule3");
    rule3.overrideSeverity("CRITICAL");
    NewBuiltInActiveRule rule4 = profile.activateRule(REPO_KEY, "foundURL");
    rule4.overrideSeverity("BLOCKER");
    profile.done();
    */
  }

  public void buildQualityProfile(Context context, String name, String language, boolean setDefault){
    NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(name, language);
    profile.setDefault(setDefault);

    NewBuiltInActiveRule rule1 = profile.activateRule(REPO_KEY, "ExampleRule1");
    rule1.overrideSeverity("BLOCKER");
    NewBuiltInActiveRule rule2 = profile.activateRule(REPO_KEY, "ExampleRule2");
    rule2.overrideSeverity("MAJOR");
    NewBuiltInActiveRule rule3 = profile.activateRule(REPO_KEY, "ExampleRule3");
    rule3.overrideSeverity("CRITICAL");
    NewBuiltInActiveRule rule4 = profile.activateRule(REPO_KEY, "foundURL");
    rule4.overrideSeverity("BLOCKER");
    profile.done();
  }

}
