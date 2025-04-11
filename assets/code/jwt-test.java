class JwtUtilTests {
    private final Faker faker = new Faker();

    @Test
    void givenValidToken_whenIsTokenValidCalled _thenTrueIsReturned() throws InvalidTokenException {
        var email = faker.internet().emailAddress();
        var password = faker.internet().password();
        var userDetails = User.builder()
                        .username(email)
                        .password(password)
                        .build();
        var token = JwtUtilClassForTests.generateToken(email);
        Assertions.assertTrue(JwtUtilClassForTests.isTokenValid(token, userDetails));
    }

    @Test
    void givenInvalidToken_whenIsTokenValidCalled _thenInvalidTokenExceptionIsThrown() {
        var email = faker.internet().emailAddress();
        var password = faker.internet().password();
        var userDetails = User.builder()
                .username(email)
                .password(password)
                .build();
        var token = JwtUtilClassForTests.generateToken(email).toUpperCase();
        Assertions.assertThrows(InvalidTokenException.class,() -> JwtUtilClassForTests.isTokenValid(token, userDetails));
    }

    @Test
    void givenEmptyStringAsToken_whenIsTokenValidCalled _thenInvalidTokenExceptionIsThrown() {
        var email = faker.internet().emailAddress();
        var password = faker.internet().password();
        var userDetails = User.builder()
                .username(email)
                .password(password)
                .build();
        var token = "";
        Assertions.assertThrows(InvalidTokenException.class,() -> JwtUtilClassForTests.isTokenValid(token, userDetails));
    }

    @Test
    void givenExpiredToken_whenIsTokenValidCalled _thenInvalidTokenExceptionIsThrown() {
        var email = faker.internet().emailAddress();
        var password = faker.internet().password();
        var userDetails = User.builder()
                .username(email)
                .password(password)
                .build();
        var token = JwtUtilClassForTests.generateExpiredToken(email);
        Assertions.assertThrows(InvalidTokenException.class,() -> JwtUtilClassForTests.isTokenValid(token, userDetails));
    }
}