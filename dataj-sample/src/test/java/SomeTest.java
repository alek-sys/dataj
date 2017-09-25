import org.dataj.sample.PersonData;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SomeTest {

    @Test
    public void shouldTest() {
        PersonData f1 = new PersonData();
        PersonData f2 = new PersonData();

        assertThat(f1, equalTo(f2));
    }
}
