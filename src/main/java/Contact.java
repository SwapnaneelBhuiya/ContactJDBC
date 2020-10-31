import java.time.LocalDate;
import java.util.Objects;

public class Contact {
    public int id;
    public String firstName;
    public String lastName;
    public String address;
    public String city;
    public String state;
    public String zip;
    public String number;
    public String email;
    public LocalDate start;

    public Contact(int id, String firstName, String lastName, String address, String city, String state, String zip, String number, String email, LocalDate start) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.number = number;
        this.email = email;
        this.start = start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return id == contact.id &&
                Objects.equals(firstName, contact.firstName) &&
                Objects.equals(lastName, contact.lastName) &&
                Objects.equals(address, contact.address) &&
                Objects.equals(city, contact.city) &&
                Objects.equals(state, contact.state) &&
                Objects.equals(zip, contact.zip) &&
                Objects.equals(number, contact.number) &&
                Objects.equals(email, contact.email) &&
                Objects.equals(start, contact.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, address, city, state, zip, number, email, start);
    }
}
