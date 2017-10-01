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
