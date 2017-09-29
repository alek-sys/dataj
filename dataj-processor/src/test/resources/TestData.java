import java.util.Objects;

class TestData {
    String name;
    int age;

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