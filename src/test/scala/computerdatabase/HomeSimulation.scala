package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class HomeSimulation extends Simulation {
  val httpConf = http
    .baseUrl("http://shop.mango.com/es") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
  //.disableFollowRedirect // disable redirects that follow in case of 301, 302, 303 or 307 response

  val homePage = scenario("Home Page")
    .exec(Home.home)

  setUp(
        homePage.inject(atOnceUsers(1)))
    .protocols(httpConf)
    .assertions(global.responseTime.max.lte(3000)
  )
}

object Home {
  val home = exec(http("Home")
    .get("/"))
    .pause(1)
}