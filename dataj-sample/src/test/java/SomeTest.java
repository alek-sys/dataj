import org.dataj.sample.PersonData;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SomeTest {

    @Test
    public void shouldTest() {
        PersonData f1 = new PersonData("alex", 22, "foo");
        PersonData f2 = new PersonData("alex", 22, "foo");

        assertThat(f1, equalTo(f2));
    }
}
