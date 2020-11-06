import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContactServiceTest {
        @Test
        public void readData() throws SQLException {
            ContactService contactService = new ContactService();
            List<Contact> contactList = contactService.readContactData();
            Assert.assertEquals(3, contactList.size());

        }
        @Test
        public void readDataInGivenRange() throws SQLException {
            ContactService contactService = new ContactService();
            LocalDate start=LocalDate.of(2018,4,4);
            List<Contact> contactList = contactService.readContactsBetweenDateRange(start,LocalDate.now());
            Assert.assertEquals(3, contactList.size());

        }
        @Test
        public void readDataInGivenCity() throws SQLException {
            ContactService contactService = new ContactService();
            List<Contact> contactList = contactService.readContactsByCity("Kolkata");
            Assert.assertEquals(2, contactList.size());

        }
        @Test
        public void readDataInGivenState() throws SQLException {
            ContactService contactService = new ContactService();
            List<Contact> contactList = contactService.readContactsByState("Maharastra");
            Assert.assertEquals(1, contactList.size());

        }
        @Test
        public void updateDateMatchesWithDB() throws SQLException {
            ContactService contactService = new ContactService();
            List<Contact> employeePayrollDataList = contactService.readContactData();
            contactService.updateContactsAddress("Orko", "Kalighat");
            boolean result = contactService.checkIfDataBaseIsInSync("Orko");
            Assert.assertTrue(result);
        }
        @Test
        public void givenNewEmployeeWhenAddedShouldSyncWithDB() throws SQLException {
            ContactService contactService = new ContactService();
            contactService.readContactData();
            contactService.addContactToAddressBook("Sayak","Mondal","Hazra","Delhi","NCR", "700056","986754534","abc@yahoo.com",LocalDate.now());
            boolean result = contactService.checkIfDataBaseIsInSync("Sayak");
            Assert.assertTrue(result);

        }
        @Test
        public void givenMultipleNamesShouldBeAdded() throws SQLException {
            ContactService contactService= new ContactService();
            contactService.readContactData();
            Contact c= new Contact(0,"Saunak","Mondal","Hazra","Delhi","NCR", "700056","986754534","abc@yahoo.com",LocalDate.now());
            Contact c2= new Contact(0,"Krish","Mondal","Hazra","Delhi","NCR", "700056","986754534","abc@yahoo.com",LocalDate.now());
            List<Contact> c3= new ArrayList<>();
            c3.add(c);
            c3.add(c2);
            Instant start=Instant.now();
            contactService.addMultipleEmployee(c3);
            Instant end=Instant.now();
            System.out.println("Time: "+ Duration.between(start,end));
            Assert.assertEquals(6,contactService.countEntries());

        }
    @Before
    public void setUP(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 3000;
    }
    private Contact[] getContactList() {
        Response response = RestAssured.get("/contacts");
        System.out.println("CONTACT ENTRIES IN JSON Server:\n" + response.asString());
        Contact[] arrayOfContacts = new Gson().fromJson(response.asString(), Contact[].class);
        return arrayOfContacts;
    }
    private Response addContactDataToJsonServer(Contact contact) {
        String contactJson = new Gson().toJson(contact);
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.header("Content-Type", "application/json");
        requestSpecification.body(contactJson);
        return requestSpecification.post("/contacts");
    }

    @Test
    public void givenContactDataInJsonServer_whenRetrieved_shouldMatchTheCount() {
        Contact[] arrayOfContacts = getContactList();
        ContactService addressBookService = new ContactService(Arrays.asList(arrayOfContacts));
        long entries = addressBookService.countEntries();
        Assert.assertEquals(6, entries);
    }
    @Test
    public void givenContactData_whenAddedToJsonServer_ShouldMatchResponse201AndCount() {
        ContactService addressBookService;
        Contact[] arrayOfContacts = getContactList();
        addressBookService = new ContactService(Arrays.asList(arrayOfContacts));
        Contact contact = new Contact(0, "Orko", "Dey", "Ohio", "Kolkata", "WB", "400096", "669855975", "pj@gmail.com", LocalDate.now());
        Response response = addContactDataToJsonServer(contact);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(201, statusCode);
        contact = new Gson().fromJson(response.asString(), Contact.class);
        addressBookService.addEmployeeDataForREST(contact);
        long entries = addressBookService.countEntries();
        Assert.assertEquals(3, entries);
    }
    @Test
    public void givenMultipleContacts_Should_Return_201(){
        ContactService addressBookService;
        Contact[] arrayOfContacts = getContactList();
        addressBookService = new ContactService(Arrays.asList(arrayOfContacts));
        Contact c= new Contact(0,"Saunak","Mondal","Hazra","Delhi","NCR", "700056","986754534","abc@yahoo.com",LocalDate.now());
        Contact c2= new Contact(0,"Krish","Mondal","Hazra","Delhi","NCR", "700056","986754534","abc@yahoo.com",LocalDate.now());
        Contact c4= new Contact(0,"Rohit","Mondal","Hazra","Delhi","NCR", "700056","986754534","abc@yahoo.com",LocalDate.now());
        List<Contact> c3= new ArrayList<>();
        c3.add(c);
        c3.add(c2);
        c3.add(c4);
        for(Contact c5:c3) {
            Response response = addContactDataToJsonServer(c5);
            int statusCode = response.getStatusCode();
            Assert.assertEquals(201, statusCode);
            Contact contact = new Gson().fromJson(response.asString(), Contact.class);
            addressBookService.addEmployeeDataForREST(contact);
        }
        long entries = addressBookService.countEntries();
        Assert.assertEquals(6, entries);

    }
    @Test
    public void givenNameShouldUpdateAddress() {
        Contact[] contacts = getContactList();
        ContactService employeePayrollService = new ContactService(Arrays.asList(contacts));
        employeePayrollService.updateContactsAddressUsingRest("Orko", "Kalighat");
        Contact contact = employeePayrollService.getContactData("Orko");
        System.out.println(contact.address);
        String empJSon = new Gson().toJson(contacts);
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(empJSon);
        Response response = request.put("/contacts/" + contact.id);
        int status = response.getStatusCode();
        Assert.assertEquals(200, status);
    }
    @Test
    public void givenNameShouldBeDeleted() {
        Contact[] contacts = getContactList();
        ContactService employeePayrollService = new ContactService(Arrays.asList(contacts));
        Contact contact = employeePayrollService.getContactData("Krish");
        String empJSon = new Gson().toJson(contacts);
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.body(empJSon);
        Response response = request.delete("/contacts/" + contact.id);
        int status = response.getStatusCode();
        Assert.assertEquals(200, status);
        employeePayrollService.removeContact("Krish");
        Assert.assertEquals(5,employeePayrollService.countEntries());
    }
}
