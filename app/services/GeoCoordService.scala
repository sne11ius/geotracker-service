package services

import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import scala.concurrent.Future
import models.GeoCoord
import java.util.UUID
import models.User

trait GeoCoordService {

  def save(geoCoord: GeoCoord, apiKey: UUID): GeoCoord

  def load(user: User): List[GeoCoord]

  def loadLatest(apiKey: UUID): GeoCoord

}
