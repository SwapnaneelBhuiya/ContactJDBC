import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
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
    }
