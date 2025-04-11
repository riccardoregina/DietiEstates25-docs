@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VisitRequestTests {
    private final Faker faker = new Faker();

    @Autowired
    AgencyService agencyService;
    @Autowired
    ListingService listingService;
    @Autowired
    CustomerService customerService;
    @Autowired
    VisitService visitService;

    Agency agency;
    Admin admin;
    Agent agent;
    Customer customer;
    UserDetails userDetails;
    Listing listing;

    @BeforeAll
    void setup() throws Exception {
        agency = new Agency(faker.internet().domainName(), faker.number().toString());
        admin = agencyService.createAdminAndAgency(new Admin(faker.name().toString(), faker.name().toString(), faker.internet().emailAddress(), LocalDate.of(1987, 5, 10), faker.internet().password(), agency, ""));
        agent = agencyService.createAgent(new Agent(faker.name().toString(), faker.name().toString(), faker.internet().emailAddress(), LocalDate.of(1987, 5, 10), faker.internet().password(), agency, admin, "", ""));
        customer = customerService.signUp(new Customer(faker.name().toString(), faker.name().toString(), faker.internet().emailAddress(), LocalDate.of(1987, 5, 10), faker.internet().password(), ""));
        userDetails = User.builder()
                        .username(customer.getEmail())
                        .password(customer.getPasswordHash())
                        .build();
        listing = listingService.createGarageListing(new GarageListingDto(
                faker.name().toString(), 
                Integer.valueOf(100000), 
                faker.pokemon().name(), 
                Integer.valueOf(100), 
                ListingType.BUY, 
                new LocationDto(
                    faker.address().country(), 
                    faker.address().city(), 
                    faker.address().fullAddress(), 
                    Double.valueOf(14.193), 
                    Double.valueOf(40.827)), 
                Integer.valueOf(0), 
                null), agent.getEmail());
    }

    @Test
    void givenWrongListingIdAndNonEmptyAvailabilities _whenCreateVisitRequestCalled_thenEntityNotExists ExceptionIsThrown() {
        var timeSlots = new LinkedList<TimeSlot>();
        timeSlots.add(TimeSlot.FROM_08_TO_10);
        var availabilities = new LinkedList<Availability>();
        availabilities.add(new Availability(LocalDate.now(), timeSlots));
        var visitRequestDto = new VisitRequestDto(listing.getId().toUpperCase(), availabilities);
        Assertions.assertThrows(EntityNotExistsException.class, () ->
            visitService.createVisitRequest(visitRequestDto, userDetails));
    }

    @Test
    void givenValidListingIdAndEmptyAvailabilities _whenCreateVisitRequestCalled_thenVisitRequest WithEmptyAvailabilitiesIsCreated() 
            throws Exception {
        var visitRequestDto = new VisitRequestDto(listing.getId(), new LinkedList<Availability>());
        var visitRequest = visitService.createVisitRequest(visitRequestDto, userDetails);
        Assertions.assertTrue(visitRequest.getAvailabilities().isEmpty());
    }

    @Test
    void givenValidListingIdAndNonEmptyAvailabilities _whenCreateVisitRequestCalled_thenVisitRequest vWithSpecifiedAvailabilitiesIsCreated() 
            throws Exception {
        var timeSlots = new LinkedList<TimeSlot>();
        timeSlots.add(TimeSlot.FROM_08_TO_10);
        var availabilities = new LinkedList<Availability>();
        availabilities.add(new Availability(LocalDate.now(), timeSlots));
        var visitRequestDto = new VisitRequestDto(listing.getId(), availabilities);
        var visitRequest = visitService.createVisitRequest(visitRequestDto, userDetails);
        Assertions.assertEquals(visitRequestDto.availabilities(), visitRequest.getAvailabilities());
    }
}