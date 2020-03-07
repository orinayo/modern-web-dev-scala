package controllers

import actors.StatsActor
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import controllers.Assets.Asset
import java.util.concurrent.TimeUnit
import javax.inject._
import play.api.mvc._
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import services.{WeatherService, SunService}

class Application(
    components: ControllerComponents,
    assets: Assets,
    sunService: SunService,
    weatherService: WeatherService,
    actorSystem: ActorSystem
) extends AbstractController(components) {
  def index = Action.async {
    val lat = -9.1032
    val lon = 7.1050

    val sunInfoF = sunService.getSunInfo(lat, lon)
    val temperatureF = weatherService.getTemperature(lat, lon)

    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    val requestsF =
      (actorSystem.actorSelection(StatsActor.path) ? StatsActor.GetStats)
        .mapTo[Int]

    for {
      sunInfo <- sunInfoF
      temperature <- temperatureF
      requests <- requestsF
    } yield {
      Ok(views.html.index(sunInfo, temperature, requests))
    }
  }

  def versioned(path: String, file: Asset) = assets.versioned(path, file)
}
