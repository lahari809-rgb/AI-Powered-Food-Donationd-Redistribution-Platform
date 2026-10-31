package com.freshconnect.config;

import com.freshconnect.model.*;
import com.freshconnect.model.User.UserRole;
import com.freshconnect.model.FoodDonation.DonationStatus;
import com.freshconnect.model.FoodRequirement.RequirementStatus;
import com.freshconnect.model.DonationRequest.RequestStatus;
import com.freshconnect.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * Initializes sample data for demonstration purposes.
 * This runs on startup and populates the H2 database.
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired private UserRepository userRepository;
    @Autowired private FoodDonationRepository donationRepository;
    @Autowired private FoodRequirementRepository requirementRepository;
    @Autowired private DonationRequestRepository requestRepository;
    @Autowired private DonationHistoryRepository historyRepository;
    
    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return; // Already initialized
        
        // === ADMIN ===
        User admin = new User("Admin", "admin@freshconnect.com", "admin123", UserRole.ADMIN);
        admin.setOrganizationName("FreshConnect");
        admin.setPhone("9999999999");
        admin = userRepository.save(admin);
        
        // === DONORS ===
        User donor1 = new User("Rajesh Kumar", "rajesh@tajhotel.com", "password123", UserRole.DONOR);
        donor1.setOrganizationName("Taj Hotel");
        donor1.setOrganizationType("hotel");
        donor1.setPhone("9876543210");
        donor1.setAddress("MG Road, Bengaluru");
        donor1.setCity("Bengaluru");
        donor1.setDescription("5-star hotel with surplus banquet food");
        donor1 = userRepository.save(donor1);
        
        User donor2 = new User("Priya Sharma", "priya@freshbakes.com", "password123", UserRole.DONOR);
        donor2.setOrganizationName("Fresh Bakes Bakery");
        donor2.setOrganizationType("bakery");
        donor2.setPhone("9876543211");
        donor2.setAddress("Koramangala, Bengaluru");
        donor2.setCity("Bengaluru");
        donor2.setDescription("Artisan bakery with daily surplus bread and pastries");
        donor2 = userRepository.save(donor2);
        
        User donor3 = new User("Amit Patel", "amit@greenmart.com", "password123", UserRole.DONOR);
        donor3.setOrganizationName("GreenMart Supermarket");
        donor3.setOrganizationType("supermarket");
        donor3.setPhone("9876543212");
        donor3.setAddress("HSR Layout, Bengaluru");
        donor3.setCity("Bengaluru");
        donor3.setDescription("Supermarket chain with daily surplus produce");
        donor3 = userRepository.save(donor3);
        
        User donor4 = new User("Sanjay Mehta", "sanjay@spiceking.com", "password123", UserRole.DONOR);
        donor4.setOrganizationName("Spice King Restaurant");
        donor4.setOrganizationType("restaurant");
        donor4.setPhone("9876543213");
        donor4.setAddress("Indiranagar, Bengaluru");
        donor4.setCity("Bengaluru");
        donor4.setDescription("Popular restaurant with surplus meals");
        donor4 = userRepository.save(donor4);
        
        // === NGOs ===
        User ngo1 = new User("Anita Desai", "anita@hopefoundation.org", "password123", UserRole.NGO);
        ngo1.setOrganizationName("Hope Foundation");
        ngo1.setPhone("9876543220");
        ngo1.setAddress("Whitefield, Bengaluru");
        ngo1.setCity("Bengaluru");
        ngo1.setDescription("Serves meals to 500+ underprivileged people daily");
        ngo1 = userRepository.save(ngo1);
        
        User ngo2 = new User("Vikram Singh", "vikram@caretrust.org", "password123", UserRole.NGO);
        ngo2.setOrganizationName("Care Trust");
        ngo2.setPhone("9876543221");
        ngo2.setAddress("Electronic City, Bengaluru");
        ngo2.setCity("Bengaluru");
        ngo2.setDescription("Provides food to orphanages and old age homes");
        ngo2 = userRepository.save(ngo2);
        
        User ngo3 = new User("Meera Joshi", "meera@feedinghands.org", "password123", UserRole.NGO);
        ngo3.setOrganizationName("Feeding Hands");
        ngo3.setPhone("9876543222");
        ngo3.setAddress("Jayanagar, Bengaluru");
        ngo3.setCity("Bengaluru");
        ngo3.setDescription("Community kitchen serving 200+ meals daily");
        ngo3 = userRepository.save(ngo3);
        
        // === BENEFICIARIES ===
        User b1 = new User("Ramesh Kumar", "ramesh@community.org", "password123", UserRole.BENEFICIARY);
        b1.setOrganizationName("Family Recipient");
        b1.setPhone("9876543230");
        b1.setAddress("Slum Settlement Sector 4");
        b1.setCity("Bengaluru");
        b1.setDescription("Seeking daily hot lunch support for family of 4");
        userRepository.save(b1);

        User b2 = new User("Fatima Begum", "fatima@community.org", "password123", UserRole.BENEFICIARY);
        b2.setOrganizationName("Senior Citizen Recipient");
        b2.setPhone("9876543231");
        b2.setAddress("Shivaji Nagar");
        b2.setCity("Bengaluru");
        b2.setDescription("Elderly resident requiring nutritional diet");
        userRepository.save(b2);
        
        // === FOOD DONATIONS ===
        FoodDonation d1 = createDonation(donor1, "Veg Meal Packets", "meals", 100, "packets", "vegetarian",
                LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(8),
                "Taj Hotel, MG Road", "Bengaluru", "Keep refrigerated during transport");
        d1.setUrgencyScore(72.0);
        d1 = donationRepository.save(d1);
        
        FoodDonation d2 = createDonation(donor2, "Assorted Bread Loaves", "bakery", 50, "pieces", "vegetarian",
                LocalDateTime.now().minusHours(4), LocalDateTime.now().plusHours(12),
                "Fresh Bakes, Koramangala", "Bengaluru", "Best consumed within 6 hours");
        d2.setUrgencyScore(65.0);
        d2 = donationRepository.save(d2);
        
        FoodDonation d3 = createDonation(donor3, "Fresh Vegetables Mix", "vegetables", 30, "kg", "vegan",
                LocalDateTime.now().minusHours(6), LocalDateTime.now().plusHours(18),
                "GreenMart, HSR Layout", "Bengaluru", "Includes tomatoes, potatoes, onions");
        d3.setUrgencyScore(55.0);
        d3 = donationRepository.save(d3);
        
        FoodDonation d4 = createDonation(donor4, "Chicken Biryani", "meals", 80, "packets", "non-vegetarian",
                LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(5),
                "Spice King, Indiranagar", "Bengaluru", "Freshly prepared, needs pickup within 5 hours");
        d4.setUrgencyScore(85.0);
        d4 = donationRepository.save(d4);
        
        FoodDonation d5 = createDonation(donor1, "Fresh Fruit Platter", "fruits", 25, "kg", "vegan",
                LocalDateTime.now().minusHours(3), LocalDateTime.now().plusHours(24),
                "Taj Hotel, MG Road", "Bengaluru", "Seasonal fruits from hotel buffet");
        d5.setUrgencyScore(48.0);
        d5 = donationRepository.save(d5);
        
        FoodDonation d6 = createDonation(donor2, "Pastries & Cakes", "bakery", 40, "pieces", "vegetarian",
                LocalDateTime.now().minusHours(5), LocalDateTime.now().plusHours(3),
                "Fresh Bakes, Koramangala", "Bengaluru", "Today's unsold pastries");
        d6.setUrgencyScore(90.0);
        d6 = donationRepository.save(d6);
        
        // A completed donation for history
        FoodDonation d7 = createDonation(donor3, "Rice & Lentils Pack", "packaged", 60, "kg", "vegetarian",
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                "GreenMart, HSR Layout", "Bengaluru", "Bulk dry goods");
        d7.setStatus(DonationStatus.COMPLETED);
        d7.setUrgencyScore(0.0);
        d7 = donationRepository.save(d7);
        
        FoodDonation d8 = createDonation(donor4, "Paneer Tikka", "meals", 45, "packets", "vegetarian",
                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2),
                "Spice King, Indiranagar", "Bengaluru", "Event leftovers");
        d8.setStatus(DonationStatus.COMPLETED);
        d8.setUrgencyScore(0.0);
        d8 = donationRepository.save(d8);
        
        // An expired donation
        FoodDonation d9 = createDonation(donor1, "Milk & Dairy Pack", "dairy", 20, "liters", "vegetarian",
                LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3),
                "Taj Hotel, MG Road", "Bengaluru", "");
        d9.setStatus(DonationStatus.EXPIRED);
        d9.setUrgencyScore(0.0);
        d9 = donationRepository.save(d9);
        
        // === FOOD REQUIREMENTS ===
        FoodRequirement r1 = createRequirement(ngo1, "meals", "vegetarian", 150, "packets", 150, "high",
                "Daily meal requirement for shelter", LocalDateTime.now().plusDays(1));
        r1 = requirementRepository.save(r1);
        
        FoodRequirement r2 = createRequirement(ngo2, "meals", "any", 80, "packets", 80, "medium",
                "Weekly food supply for orphanage", LocalDateTime.now().plusDays(3));
        r2 = requirementRepository.save(r2);
        
        FoodRequirement r3 = createRequirement(ngo3, "vegetables", "vegan", 50, "kg", 200, "high",
                "Fresh vegetables for community kitchen", LocalDateTime.now().plusDays(1));
        r3 = requirementRepository.save(r3);
        
        FoodRequirement r4 = createRequirement(ngo1, "bakery", "vegetarian", 30, "pieces", 60, "low",
                "Bread for morning breakfast program", LocalDateTime.now().plusDays(2));
        r4 = requirementRepository.save(r4);
        
        // Past requirements for demand prediction
        FoodRequirement r5 = createRequirement(ngo1, "meals", "vegetarian", 120, "packets", 120, "high",
                "Past requirement - fulfilled", null);
        r5.setStatus(RequirementStatus.FULFILLED);
        requirementRepository.save(r5);
        
        FoodRequirement r6 = createRequirement(ngo1, "meals", "vegetarian", 140, "packets", 140, "high",
                "Past requirement - fulfilled", null);
        r6.setStatus(RequirementStatus.FULFILLED);
        requirementRepository.save(r6);
        
        // === DONATION REQUESTS ===
        // Completed request for d7
        DonationRequest req1 = new DonationRequest();
        req1.setDonation(d7);
        req1.setNgo(ngo1);
        req1.setStatus(RequestStatus.COMPLETED);
        req1.setMatchScore(88.0);
        req1.setMatchReason("Category match (packaged). Quantity fits well. Excellent match!");
        req1.setRequestedQuantity(60);
        req1.setAcceptedAt(LocalDateTime.now().minusDays(2));
        req1.setCollectedAt(LocalDateTime.now().minusDays(2).plusHours(2));
        req1.setCompletedAt(LocalDateTime.now().minusDays(1));
        requestRepository.save(req1);
        
        // Completed request for d8
        DonationRequest req2 = new DonationRequest();
        req2.setDonation(d8);
        req2.setNgo(ngo2);
        req2.setStatus(RequestStatus.COMPLETED);
        req2.setMatchScore(75.0);
        req2.setMatchReason("Category match (meals). Good quantity match.");
        req2.setRequestedQuantity(45);
        req2.setAcceptedAt(LocalDateTime.now().minusDays(3));
        req2.setCollectedAt(LocalDateTime.now().minusDays(3).plusHours(1));
        req2.setCompletedAt(LocalDateTime.now().minusDays(2));
        requestRepository.save(req2);
        
        // Pending request for d1
        DonationRequest req3 = new DonationRequest();
        req3.setDonation(d1);
        req3.setNgo(ngo1);
        req3.setRequirement(r1);
        req3.setStatus(RequestStatus.PENDING);
        req3.setMatchScore(92.0);
        req3.setMatchReason("Perfect category match (meals). Quantity fits requirement well (100/150 packets). Good match.");
        req3.setRequestedQuantity(100);
        req3.setMessage("We urgently need these meals for our evening distribution program");
        requestRepository.save(req3);
        
        d1.setStatus(DonationStatus.REQUESTED);
        donationRepository.save(d1);
        
        // === HISTORY ENTRIES ===
        historyRepository.save(new DonationHistory(d7, req1, donor3, "CREATED", "New donation: Rice & Lentils Pack"));
        historyRepository.save(new DonationHistory(d7, req1, ngo1, "REQUESTED", "Hope Foundation requested donation"));
        historyRepository.save(new DonationHistory(d7, req1, donor3, "ACCEPTED", "Request accepted"));
        historyRepository.save(new DonationHistory(d7, req1, ngo1, "COLLECTED", "Food collected by Hope Foundation"));
        historyRepository.save(new DonationHistory(d7, req1, ngo1, "COMPLETED", "Distribution completed"));
        
        historyRepository.save(new DonationHistory(d1, null, donor1, "CREATED", "New donation: Veg Meal Packets (100 packets)"));
        historyRepository.save(new DonationHistory(d2, null, donor2, "CREATED", "New donation: Assorted Bread Loaves (50 pieces)"));
        historyRepository.save(new DonationHistory(d4, null, donor4, "CREATED", "New donation: Chicken Biryani (80 packets)"));
        historyRepository.save(new DonationHistory(d1, req3, ngo1, "REQUESTED", "Hope Foundation requested 100 Veg Meal Packets"));
        
        System.out.println("✅ Sample data initialized successfully!");
        System.out.println("   - 4 Donors, 3 NGOs, 1 Admin");
        System.out.println("   - 9 Donations (6 available, 2 completed, 1 expired)");
        System.out.println("   - 4 Active + 2 Past requirements");
        System.out.println("   - 3 Donation requests");
    }
    
    private FoodDonation createDonation(User donor, String name, String category, int qty, String unit,
                                         String foodType, LocalDateTime prepTime, LocalDateTime expiryTime,
                                         String address, String city, String instructions) {
        FoodDonation d = new FoodDonation();
        d.setDonor(donor);
        d.setFoodName(name);
        d.setCategory(category);
        d.setQuantity(qty);
        d.setUnit(unit);
        d.setFoodType(foodType);
        d.setPreparationTime(prepTime);
        d.setExpiryTime(expiryTime);
        d.setPickupAddress(address);
        d.setPickupCity(city);
        d.setInstructions(instructions);
        d.setAvailableFrom(prepTime);
        d.setAvailableUntil(expiryTime);
        d.setStatus(DonationStatus.AVAILABLE);
        return d;
    }
    
    private FoodRequirement createRequirement(User ngo, String category, String foodType, int qty, String unit,
                                               int beneficiaries, String urgency, String description, LocalDateTime neededBy) {
        FoodRequirement r = new FoodRequirement();
        r.setNgo(ngo);
        r.setFoodCategory(category);
        r.setFoodType(foodType);
        r.setQuantity(qty);
        r.setUnit(unit);
        r.setBeneficiaries(beneficiaries);
        r.setUrgency(urgency);
        r.setDescription(description);
        r.setNeededBy(neededBy);
        r.setStatus(RequirementStatus.ACTIVE);
        return r;
    }
}

