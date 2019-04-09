/*
 * Copyright 2019
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
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
package de.tudarmstadt.ukp.inception.recommender.api.evaluation;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.ukp.inception.recommendation.api.evaluation.AnnotatedTokenPair;
import de.tudarmstadt.ukp.inception.recommendation.api.evaluation.EvaluationResult;

public class EvaluationResultTest
{
    private List<AnnotatedTokenPair> instances;

    @Before
    public void setUp()
    {
        instances = new ArrayList<>();
        String[][] instanceLabels = new String[][] { { "PER", "PER" }, { "PER", "ORG" },
                { "ORG", "PER" }, { "ORG", "LOC" }, { "PER", "LOC" }, { "LOC", "ORG" },
                { "LOC", "LOC" }, { "ORG", "LOC" }, { "PER", "ORG" }, { "ORG", "ORG" },
                { "LOC", "LOC" }, { "ORG", "LOC" }, { "PER", "ORG" }, { "ORG", "ORG" },
                { "LOC", "PER" }, { "ORG", "ORG" }, { "PER", "PER" }, { "ORG", "ORG" } };
        for (String[] labels : instanceLabels) {
            instances.add(new AnnotatedTokenPair(labels[0], labels[1]));
        }
    }

    @Test
    public void thatAccuracyWorks()
    {
        EvaluationResult calc = new EvaluationResult(instances.stream());
        
        assertThat(calc.computeAccuracyScore()).as("accuracy is correctly calculated")
                .isEqualTo(4.0 / 9.0);
    }

    @Test
    public void thatPrecisionWorks()
    {
        EvaluationResult calc = new EvaluationResult(instances.stream());
        
        assertThat(calc.computePrecisionScore()).as("precision is correctly calculated")
                .isEqualTo((0.5 + 0.5 + 1.0 / 3.0) / 3);
    }

    @Test
    public void thatRecallWorks()
    {
        EvaluationResult calc = new EvaluationResult(instances.stream());
        
        assertThat(calc.computeRecallScore()).as("recall is correctly calculated")
                .isEqualTo((0.5 + 0.5 + 1.0 / 3.0) / 3);
    }

    @Test
    public void thatF1Works()
    {
        EvaluationResult calc = new EvaluationResult(instances.stream());
        
        assertThat(calc.computeF1Score()).as("f1 is correctly calculated")
                .isEqualTo(2 * (4.0 / 9.0 * 4.0 / 9.0) / (8.0 / 9.0));
    }
    
    @Test
    public void thatIgnoringALabelWorks()
    {
        EvaluationResult calc = new EvaluationResult(asList("PER"), instances.stream());
        
        assertThat(calc.computeF1Score()).as("f1 with ignore label is correctly calculated")
                .isEqualTo(2 * (0.5 * (0.5 + 1.0 / 3) * 0.5)
                        / (0.5 + (0.5 + 1.0 / 3) * 0.5));
        assertThat(calc.computeRecallScore()).as("recall with ignore label is correctly calculated")
                .isEqualTo(0.5);
        assertThat(calc.computeAccuracyScore())
                .as("accuracy with ignore label is correctly calculated").isEqualTo(6.0 / 12);
        assertThat(calc.computePrecisionScore())
                .as("precision with ignore label is correctly calculated")
                .isEqualTo((0.5 + 1.0 / 3) * 0.5);
    }

    @Test
    public void thatNumOfLabelsWorks()
    {
        EvaluationResult calc = new EvaluationResult(asList(), instances.stream());
        assertThat(calc.getNumOfLabels()).as("check num of labels for no ignoreLabel").isEqualTo(3);

        calc = new EvaluationResult(asList("PER"), instances.stream());
        assertThat(calc.getNumOfLabels()).as("check num of labels for one ignoreLabel")
                .isEqualTo(2);

        calc = new EvaluationResult(asList("PER", "ORG"), instances.stream());
        assertThat(calc.getNumOfLabels()).as("check num of labels for two ignoreLabel")
                .isEqualTo(1);
    }

    @Test
    public void thatMissingClassesWorks()
    {
        // test with classes which are never gold or never predicted
        List<AnnotatedTokenPair> newInstances = new ArrayList<>(instances);
        newInstances.add(new AnnotatedTokenPair("PART", "ORG"));
        newInstances.add(new AnnotatedTokenPair("PER", "PUNC"));
        
        EvaluationResult calc = new EvaluationResult(newInstances.stream());
        
        assertThat(calc.computeAccuracyScore())
                .as("accuracy with missing classes is correctly calculated").isEqualTo(2.0 / 5);
        assertThat(calc.computePrecisionScore())
                .as("precision with missing classes is correctly calculated")
                .isEqualTo((0.5 + 7.0 / 9) / 5);
        assertThat(calc.computeRecallScore())
                .as("recall with missing classes is correctly calculated")
                .isEqualTo((2.0 / 7 + 0.5 + 0.5) / 5);
        assertThat(calc.computeF1Score()).as("f1 with missing classes is correctly calculated")
                .isEqualTo(2 * ((0.5 + 7.0 / 9) / 5 * (2.0 / 7 + 0.5 + 0.5) / 5)
                        / ((0.5 + 7.0 / 9) / 5 + (2.0 / 7 + 0.5 + 0.5) / 5));
    }
}
