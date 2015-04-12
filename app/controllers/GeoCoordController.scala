package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import forms._
import models.User
import scala.concurrent.Future
import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.data._
import play.api.data.Forms._
import services.GeoCoordService
import play.api.Logger
import org.joda.time.DateTime
import models.GeoCoord
import java.util.UUID

import play.api.libs.json._
import play.api.libs.json.Json

/**
 * The basic application controller.
 *
 * @param env The Silhouette environment.
 */
class GeoCoordController @Inject() (val geoCoordService: GeoCoordService) extends Controller {

  def postCoords = Action { implicit request =>
    val apiKey = Form("apiKey" -> text).bindFromRequest.fold( hasErrors => { "" }, value => { value } )
    if ("" == apiKey) {
      Logger.debug("No api key")
      Unauthorized
    } else {
      val latitude = Form("latitude" -> text).bindFromRequest.get.toDouble
      val longitude = Form("longitude" -> text).bindFromRequest.get.toDouble
      val altitude = Form("altitude" -> text).bindFromRequest.get.toDouble
      val accuracy = Form("accuracy" -> text).bindFromRequest.get.toFloat
      val speed = Form("speed" -> text).bindFromRequest.get.toFloat
      val time = new DateTime(Form("time" -> text).bindFromRequest.get.toLong)
      val geoCoord = GeoCoord(
          None,
          UUID.randomUUID(),
          latitude,
          longitude,
          altitude,
          accuracy,
          speed,
          time
      )
      geoCoordService.save(geoCoord, UUID.fromString(apiKey))
      Ok
    }
  }

  implicit val coordWrites = new Writes[GeoCoord] {
    def writes(c: GeoCoord): JsValue = {
      Json.obj(
        "latitude" -> c.latitude,
        "longitude" -> c.longitude,
        "altitude" -> c.altitude,
        "speed" -> c.speed,
        "accuracy" -> c.accuracy,
        "time" -> c.time.getMillis
      )
    }
  }

  def loadLatest = Action { implicit request =>
    val apiKey = Form("apiKey" -> text).bindFromRequest.fold( hasErrors => { "" }, value => { value } )
    if ("" == apiKey) {
      Logger.debug("No api key")
      Unauthorized
    } else {
      Ok(Json.toJson(geoCoordService.loadLatest(UUID.fromString(apiKey))))
    }
  }

}
