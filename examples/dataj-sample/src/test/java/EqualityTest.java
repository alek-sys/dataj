/*
 * Copyright 2017 Alexey Nesterov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.dataj.sample.PersonData;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class EqualityTest {

    @Test
    public void shouldCompareTwoDataClasses() {
        PersonData p1 = new PersonData("Alex", 31, "");
        PersonData p2 = new PersonData("Alex", 31, "");

        assertThat(p1, equalTo(p2));
    }
}
