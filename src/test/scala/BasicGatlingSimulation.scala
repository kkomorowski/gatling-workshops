import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class BasicGatlingSimulation extends Simulation {

  val httpConf: HttpProtocolBuilder = http
    .baseURL("http://localhost:8080")
    .acceptHeader("application/json")

  val scn: ScenarioBuilder = scenario("BasicGatlingScenario")
    .exec(
      http("Hello, Gatling!")
        .get("/")
    )
    .pause(5)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
