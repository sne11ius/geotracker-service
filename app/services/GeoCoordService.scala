package services

import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import scala.concurrent.Future
import models.GeoCoord
import java.util.UUID

trait GeoCoordService {

  def save(geoCoord: GeoCoord, apiKey: UUID): GeoCoord

}
