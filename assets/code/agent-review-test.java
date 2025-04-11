@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AgentReviewTests {
    private final Faker faker = new Faker();

    @Autowired
    AgencyService agencyService;
    @Autowired
    AgentReviewService agentReviewService;
    @Autowired
    CustomerService customerService;

    Agency agency;
    Admin admin;
    Agent agent;
    Customer customer;
    UserDetails userDetails;

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
    }

    @Test
    void givenCorrectAgentId_whenCreateAgentReviewCalled _thenAgentReviewIsCreated() throws Exception {
        var agentReview = agentReviewService.createAgentReview(new AgentReviewDto(agent.getId(), 5, "Very good"), userDetails);
        var fetchedAgentReview = agentReviewService.getAgentReviews(agent.getId()).getLast();
        Assertions.assertAll(
            () -> Assertions.assertEquals(agentReview.getId(), fetchedAgentReview.getId()),
            () -> Assertions.assertEquals(agentReview.getValue(), fetchedAgentReview.getValue()),
            () -> Assertions.assertEquals(agentReview.getComment(), fetchedAgentReview.getComment())
        );
    }

    @Test
    void givenWrongAgentId_whenCreateAgentCalled _thenEntityNotExistsExceptionIsThrown() {
        Assertions.assertThrows(EntityNotExistsException.class, () -> 
            agentReviewService.createAgentReview(new AgentReviewDto(admin.getId(), 5, "Very good"), userDetails));
    }
}
