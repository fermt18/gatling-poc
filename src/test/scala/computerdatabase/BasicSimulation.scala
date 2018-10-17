package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://shop.pre.mango.com") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
  //.disableFollowRedirect // disable redirects that follow in case of 301, 302, 303 or 307 response

  object Browse {

    val feeder = csv("country_code.csv").random
    val browse = repeat( 2, "n") { // runs for each user
      exec(http("Home")
        .get("/"))
        .pause(3)
        .feed(feeder)
        .exec(http("Random Country: ${code}")
          .get("/${code}"))
        .pause(3)
    }
  }

  object Pedidos {

    val pedidos =
      exec(http("Pedidos")
      .post("/services/miscompras/getDetallePedido/online/BL2V6E/es/ES/8/0/ES?customerId=10581047&token=W0JAMzdjYzliMDExNTM1OTcyMjE3NTU4")
      .header("Content-Length", "552")
      .asJSON
      .check(jsonPath("$.id").exists)
      .check(status is 200))
      .pause(3)
  }

  val not_logged_users = scenario("Not Looged").exec(Browse.browse)
  val logged_users = scenario("Logged").exec(Browse.browse, Pedidos.pedidos)

  setUp(
    not_logged_users.inject(atOnceUsers(100)),
    logged_users.inject(rampUsers(10).over(10 seconds))
  ).protocols(httpConf)
}