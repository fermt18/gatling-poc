package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._


class BasicSimulation extends Simulation {

  val httpConf = http
    .baseUrl("http://shop.mango.com/es") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
    //.disableFollowRedirect // disable redirects that follow in case of 301, 302, 303 or 307 response

  val regular = scenario("Regular Users")
      .exec(Search.search, Edits.edits)
  val curious = scenario("Curious Users")
    .exec(Search.search, Browse.browse, Edits.edits)

  setUp(
        regular.inject(atOnceUsers(100)),
        curious.inject(rampUsers(10).during(10)))
    .protocols(httpConf)
    .assertions(global.responseTime.max.lte(25000))
}

object Search {
  val search = exec(http("Home")
    .get("/"))
    .pause(7)
    .exec(http("Search")
      .get("/search?kw=vestidos&brand=she"))
    .pause(2)
    .exec(http("Select")
      .get("/mujer/vestidos-cortos/vestido-entallado-textura_51063763.html?c=02&n=1&s=search"))
    .pause(3)
}

object Browse {
  val feeder = csv("./src/test/resources/data/country_code.csv").random
  val browse = repeat( 1, "n") { // runs for each user!! access iteration with ${n}
    exec(http("Home")
      .get("/"))
      .feed(feeder)
      .exec(http("Marca: ${code}")
        .get("/${code}"))
  /*    .check(css("a:contains('${searchComputerName}')", "href").saveAs("computerURL")))
      .pause(1)
      .exec(http("Select")
        .get("${computerURL}"))
      .pause(3)*/
  }
}

object Edits {
  val edits = exec(http("AllEdits")
      .get("/edits/all-edits"))
}