import javax.annotation.Nonnull;
import java.util.Objects;

class TestData {
    String name;
    int age;

    public TestData(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int value) {
        this.age = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestData other = (TestData) o;
        return Objects.equals(name, other.name) && Objects.equals(age, other.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}