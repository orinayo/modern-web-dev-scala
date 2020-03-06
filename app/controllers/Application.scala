package controllers

import controllers.Assets.Asset
import javax.inject._
import play.api.mvc._

class Application @Inject() (
    components: ControllerComponents,
    assets: Assets,
    ws: WSClient
) extends AbstractController(components) {
  def index = Action.async {
    

  }

  def versioned(path: String, file: Asset) = assets.versioned(path, file)
}
