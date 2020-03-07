import _root_.controllers.AssetsComponents
import actors.StatsActor
import actors.StatsActor.Ping
import akka.actor.Props
import com.softwaremill.macwire._
import controllers.Application
import filters.StatsFilter
import play.api._
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import router.Routes
import scala.concurrent.Future
import services.{SunService, WeatherService}

// Application Loader is a built in Play trait
class AppApplicationLoader extends ApplicationLoader {
  // Invoked every time the app reloads
  def load(context: Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach { cfg =>
      cfg.configure(context.environment)
    }
    new AppComponents(context).application
  }
}

class AppComponents(context: Context)
    extends BuiltInComponentsFromContext(context)
    with AhcWSComponents
    with AssetsComponents
    with HttpFiltersComponents {
  private val log = Logger(this.getClass)
  override lazy val controllerComponents = wire[DefaultControllerComponents]

  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  lazy val applicationController = wire[Application]

  lazy val sunService = wire[SunService]
  lazy val weatherService = wire[WeatherService]

  lazy val statsFilter: Filter = wire[StatsFilter]
  override lazy val httpFilters = Seq(statsFilter)

  lazy val statsActor = actorSystem.actorOf(
    Props(wire[StatsActor]), StatsActor.name)

  applicationLifecycle.addStopHook { () =>
    log.info("The app is about to stop")
    Future.successful(Unit)
  }

  val onStart = {
    log.info("The app is about to start")
    statsActor ! Ping
  }

}
