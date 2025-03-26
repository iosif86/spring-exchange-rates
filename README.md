# Instructions

**Docker Run**
<br/> cd ExchangeRates
<br/> docker-compose up -d
<br/>
**Maven Run**
<br/> cd ExchangeRates
<br/> mvn clean install
<br/> mvn spring-boot:run
<br/>
swagger-ui: http://localhost:8080/swagger-ui/index.html
<br/>
**API Operations**
1. Get exchange rate from Currency A to Currency B   http://localhost:8080/api/v1/exchange/rate?from=EUR&to=RON
2. Get all exchange rates from Currency A   http://localhost:8080/api/v1/exchange/rates?from=EUR
3. Get value conversion from Currency A to Currency B   http://localhost:8080/api/v1/exchange/conversion?from=EUR&to=RON&amount=10
4. Get value conversion from Currency A to a list of supplied currencies <br/>
       http://localhost:8080/api/v1/exchange/conversions?from=EUR&to=RON,USD&amount=10

**GraphQL Queries**
<br/> Go to: localhost:8080/graphiql
enter these queries: 
<br/>1. query {
  getRate(from: EUR, to: RON) {
    currencyPair
    value
  }
}
<br/>2. query {
  getRates(from: EUR) {
    currencyPair
    value
  }
}
<br/>3. query {
  getConversionToCurrency(from: EUR, to: RON, amount: 10) {
    from
    to
    amount
    result
  }
}
<br/>4.query {
  getConversionToCurrencies(from: EUR, to: [RON, USD], amount: 10) {
    from
    to
    amount
    result
  }
}
<br/>validation for negative amounts:
<br/>5. query {
  getConversionToCurrency(from: EUR, to: RON, amount: -10) {
    from
    to
    amount
    result
  }
}
<br/>6. query {
  getConversionToCurrencies(from: EUR, to: [RON, USD], amount: -90) {
    from
    to
    amount
    result
  }
}
   
